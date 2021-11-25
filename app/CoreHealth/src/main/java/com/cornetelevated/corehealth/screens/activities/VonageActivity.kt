package com.cornetelevated.corehealth.screens.activities

import android.app.NotificationManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.App
import com.cornetelevated.corehealth.services.MessagingService
import com.opentok.android.*
import com.opentok.android.PublisherKit.PublisherListener
import com.opentok.android.Session.SessionListener


class VonageActivity : AppCompatActivity(), SessionListener, PublisherListener {

    private var mSession: Session? = null

    private var spinner: ProgressBar? = null
    private var publisherViewContainer: FrameLayout? = null
    private var subscriberViewContainer: FrameLayout? = null
    private var callUIContainer: ConstraintLayout? = null

    private var subscriber: Subscriber? = null
    private var isConnected = false

    private val LOG_TAG = "VONAGE_LOG"

    /*
    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }
*/
    override fun onBackPressed() {
        mSession?.disconnect()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vonage)

        // initialize view objects from your layout
        publisherViewContainer = findViewById(R.id.publisher_container)
        subscriberViewContainer = findViewById(R.id.subscriber_container)
        callUIContainer = findViewById(R.id.call_ui_container)
        spinner = findViewById(R.id.vonageSpinner)
        val btnEnd = findViewById<Button>(R.id.end_button)
        val btnAccept = findViewById<ImageButton>(R.id.btnAcceptCallZoom)
        val btnCancel = findViewById<ImageButton>(R.id.btnCancelZoom)
        val txt_CallerName = findViewById<TextView>(R.id.txtCallerNameZoom)

        // Capture the layout's TextView and set the string as its text
        val intent = intent
        val token = intent.getStringExtra(MessagingService.EXTRA_Token)
        val session = intent.getStringExtra(MessagingService.EXTRA_SessionId)
        val key = intent.getStringExtra(MessagingService.EXTRA_key)
        val callerName = intent.getStringExtra(MessagingService.EXTRA_CallerName)
        txt_CallerName.text = callerName
        btnEnd.setOnClickListener { v: View? ->
            if (isConnected) {
                mSession?.disconnect()
            } else {
                mSession = null
                (this.application as App).setInCall(false)
                finish()
            }
        }
        btnAccept.setOnClickListener { v: View? ->
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            btnEnd.visibility = View.VISIBLE
            join(key, session, token)
        }
        btnCancel.setOnClickListener { v: View? ->
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            (this.application as App).setInCall(false)
            finish()
        }
    }

    fun join(key: String?, sessionId: String?, token: String?) {
        (this.application as App).setInCall(true)
        mSession = Session.Builder(this, key, sessionId).build()
        mSession?.setSessionListener(this)
        mSession?.connect(token)
        callUIContainer!!.visibility = View.GONE
    }

    override fun onConnected(session: Session?) {
        Log.i(LOG_TAG, "Session Connected")
        spinner!!.visibility = View.GONE
        isConnected = true
        val publisher: Publisher = Publisher.Builder(this).build()
        publisher.setPublisherListener(this)
        publisherViewContainer!!.addView(publisher.getView())
        if (publisher.getView() is GLSurfaceView) {
            (publisher.getView() as GLSurfaceView).setZOrderOnTop(true)
        }
        mSession?.publish(publisher)
    }

    override fun onDisconnected(session: Session?) {
        (this.application as App).setInCall(false)
        finish()
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(LOG_TAG, "Stream Received")
        if (subscriber == null) {
            subscriber = Subscriber.Builder(this, stream).build()
            mSession?.subscribe(subscriber)
            subscriberViewContainer!!.addView(subscriber?.getView())
        }
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.i(LOG_TAG, "Stream Dropped")
        if (subscriber != null) {
            subscriber = null
            subscriberViewContainer!!.removeAllViews()
            mSession?.disconnect()
        }
    }

    override fun onError(session: Session?, opentokError: OpentokError?) {}

    // PublisherListener methods
    override fun onStreamCreated(publisherKit: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, "Publisher onStreamCreated")
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed")
    }

    override fun onError(publisherKit: PublisherKit?, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage())
    }

}