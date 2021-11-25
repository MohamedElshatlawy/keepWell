package com.cornetelevated.corehealth.services

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.App
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.screens.activities.VonageActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.adorsys.android.securestoragelibrary.SecurePreferences
import java.util.*

class MessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgService"
        const val EXTRA_CallerName = "EXTRA_vonageCallerName"
        const val EXTRA_Token = "EXTRA_vonageToken"
        const val EXTRA_SessionId = "EXTRA_vonageSessionId"
        const val EXTRA_key = "EXTRA_vonageKey"
        private const val notificationId = 1
        private const val channelId = "VoipChannel"
        private const val channelName = "Voip Channel"
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        var sessionId = ""
        var callerName: String? = "Doctor"
        var apiKey: String? = ""
        var token: String? = ""
        for ((key, value) in remoteMessage.data.entries) {
            if (key == "SESSION_ID") {
                sessionId = value
            }
            if (key == "token") {
                token = value
            }
            if (key == "key") {
                apiKey = value
            }
            if (key == "callerName") {
                callerName = value
            }
        }
        if (sessionId.isEmpty()) {
            // Sync data
            FitManager.getInstance(applicationContext)?.fetchData(true)
        } else {
            // Show UI for call handling
            val app = this.application as? App
            app?.let {
                if (!it.inCall()) {
                    val callIntent = Intent(this.applicationContext, VonageActivity::class.java)
                    callIntent.putExtra(EXTRA_CallerName, callerName)
                    callIntent.putExtra(EXTRA_SessionId, sessionId)
                    callIntent.putExtra(EXTRA_key, apiKey)
                    callIntent.putExtra(EXTRA_Token, token)
                    callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    if (foregrounded()) {
                        (this.application as App).setInCall(true)
                        startActivity(callIntent)
                    } else {
                        val pendingIntent = PendingIntent.getActivity(this, 0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        createChannel()
                        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                            .setContentText(callerName)
                            .setContentTitle("Incoming Call")
                            .setSmallIcon(R.drawable.app_icon_foreground)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL) //.addAction(R.drawable.zm_accept_normal, "Accept", pendingIntent)
                            //.addAction(R.drawable.zm_decline_normal, "Accept", null)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                            .setFullScreenIntent(pendingIntent, true)
                        var incomingCallNotification: Notification? = null
                        if (notificationBuilder != null) {
                            incomingCallNotification = notificationBuilder.build()
                            //incomingCallNotification.sound = uri
                        }
                        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(notificationId, incomingCallNotification)
                    }
                }
            }
        }
    }

    /**
     *Creates notification channel if OS version is greater than or eqaul to Oreo
     */
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Call Notifications"
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_RING)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build()
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), attributes)

            Objects.requireNonNull<NotificationManager>(
                this.getSystemService(
                    NotificationManager::class.java
                )
            ).createNotificationChannel(channel)
        }
    }

    /**
     * Checks if the app is in foreground
     */
    private fun foregrounded(): Boolean {
        val appProcessInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        SecurePreferences.setValue("KW.FCMtoken", token)
    }


}
