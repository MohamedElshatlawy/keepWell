package com.cornetelevated.corehealth.screens.activities

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.models.fit.DataReading
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.models.fit.FitReadings
import com.cornetelevated.corehealth.network.response.*
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.receivers.SyncReceiver
import com.cornetelevated.corehealth.screens.fragments.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

open class HomeActivity : Activity() {

    lateinit var navigationView: BottomNavigationView
    lateinit var welcomeLabel: TextView
    lateinit var titleLabel: TextView
    lateinit var fragments:ArrayList<Fragment>
    var readings = FitReadings()

    private val notificationReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            fetchServerReadings()
        }
    }

    companion object {
        private const val RC_VIDEO_APP_PERM = 124
    }

    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragments = ArrayList()
    }

    override fun onStart() {
        super.onStart()
        applicationContext?.let { LocalBroadcastManager.getInstance(it).registerReceiver(notificationReceiver, IntentFilter("fetchServerReadings")) }
    }

    override fun onDestroy() {
        applicationContext?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(notificationReceiver) }
        super.onDestroy()
    }

    open fun showUI() {

    }

    fun updateWelcomeText() {
        runOnUiThread {
            var welcomeString = resources.getString(R.string.welcome)
            welcomeString += " "
            welcomeString += LoginActivity.userFirstName()
            welcomeLabel.text = welcomeString
        }
    }

    open fun showReadingHistory(type: AddReadingActivity.ReadingType) { }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            navigationView.visibility = View.VISIBLE
            showUI()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("", "signInResult:failed code=" + e.statusCode)
            AlertDialog.Builder(ContextThemeWrapper(this@HomeActivity, R.style.appDialog))
                .setTitle("SignInResult")
                .setMessage(" Failed with code =" + e.statusCode) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    fun loadFragment(index: Int) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
        for (i in fragments.indices) {
            val fragment: Fragment = fragments[i]
            if (i == index) {
                titleLabel.text = fragment.title
                if (fragment.isLoaded) {
                    transaction.show(fragment)
                } else {
                    transaction.add(R.id.fragmentContainer, fragment)
                    fragment.isLoaded = true
                }
            } else {
                if (fragment.isLoaded) {
                    transaction.hide(fragment)
                }
            }
        }
        transaction.commit()
    }

    fun fetchReadings(sync: Boolean) {
        FitManager.getInstance(this)?.fetchData(sync)
    }

    fun fetchServerReadings() {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = FitManager.dateTimeFormat.format(cal.timeInMillis)
        cal.add(Calendar.MONTH, -1)
        val startTime = FitManager.dateTimeFormat.format(cal.timeInMillis)

        val map = HashMap<String, Any>()
        map["From"] = startTime
        map["To"] = endTime

        val stepsCall: Call<StepsResponse> = APIClient.readingsInterface.getStepsReadings(LoginActivity.getUserToken(), map)
        stepsCall.enqueue(object : Callback<StepsResponse> {
            override fun onResponse(call: Call<StepsResponse>, response: Response<StepsResponse>) {
                readings.steps = ArrayList()
                response.body()?.response?.let {
                    for (steps in it) {
                        FitManager.dateTimeFormat.parse(steps.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.value = steps.stepsValue
                            readings.steps.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<StepsResponse>, t: Throwable) { }
        })

        val weightCall: Call<ScaleResponse> = APIClient.readingsInterface.getScaleReadings(LoginActivity.getUserToken(), map)
        weightCall.enqueue(object : Callback<ScaleResponse> {
            override fun onResponse(call: Call<ScaleResponse>, response: Response<ScaleResponse>) {
                readings.weight = ArrayList()
                response.body()?.response?.let {
                    for (item in it) {
                        FitManager.dateTimeFormat.parse(item.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.value = item.weight
                            readings.weight.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<ScaleResponse>, t: Throwable) { }
        })

        val pulseCall: Call<PulseResponse> = APIClient.readingsInterface.getPulseReadings(LoginActivity.getUserToken(), map)
        pulseCall.enqueue(object : Callback<PulseResponse> {
            override fun onResponse(call: Call<PulseResponse>, response: Response<PulseResponse>) {
                readings.pulse = ArrayList()
                response.body()?.response?.let {
                    for (item in it) {
                        FitManager.dateTimeFormat.parse(item.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.value = item.pluseValue
                            readings.pulse.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<PulseResponse>, t: Throwable) { }
        })

        val bpCall: Call<BloodPressureResponse> = APIClient.readingsInterface.getBloodPressureReadings(LoginActivity.getUserToken(), map)
        bpCall.enqueue(object : Callback<BloodPressureResponse> {
            override fun onResponse(call: Call<BloodPressureResponse>, response: Response<BloodPressureResponse>) {
                readings.bloodPressure = ArrayList()
                response.body()?.response?.let {
                    for (item in it) {
                        FitManager.dateTimeFormat.parse(item.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.maxValue = item.sys
                            reading.minValue = item.dia
                            readings.bloodPressure.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<BloodPressureResponse>, t: Throwable) { }
        })

        val oxygenCall: Call<OxygenResponse> = APIClient.readingsInterface.getOxygenReadings(LoginActivity.getUserToken(), map)
        oxygenCall.enqueue(object : Callback<OxygenResponse> {
            override fun onResponse(call: Call<OxygenResponse>, response: Response<OxygenResponse>) {
                readings.oxygen = ArrayList()
                response.body()?.response?.let {
                    for (item in it) {
                        FitManager.dateTimeFormat.parse(item.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.value = item.oxygenLevel
                            readings.oxygen.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<OxygenResponse>, t: Throwable) { }
        })

        val glucoseCall: Call<ReadingsResponse> = APIClient.readingsInterface.getGlucoseReadings(LoginActivity.getUserToken(), map)
        glucoseCall.enqueue(object : Callback<ReadingsResponse> {
            override fun onResponse(call: Call<ReadingsResponse>, response: Response<ReadingsResponse>) {
                readings.glucose = ArrayList()
                response.body()?.response?.let {
                    for (item in it) {
                        FitManager.dateTimeFormat.parse(item.creationDate)?.let {
                            val reading = DataReading()
                            reading.date = it
                            reading.value = item.reading
                            readings.glucose.add(reading)
                        }
                    }
                    updateDashboard()
                }
            }
            override fun onFailure(call: Call<ReadingsResponse>, t: Throwable) { }
        })
    }

    open fun updateDashboard() {

    }

    // Permission Delegate
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (i in permissions.indices) {
            val permission = permissions[i]
            val result = grantResults[i]
            if (permission == Manifest.permission.BODY_SENSORS || permission == Manifest.permission.ACTIVITY_RECOGNITION) {
                // Fit related permissions
                if (result == PackageManager.PERMISSION_GRANTED) {
                    FitManager.getInstance(this)?.checkPermissions()
                } else {
                    AlertDialog.Builder(ContextThemeWrapper(this@HomeActivity, R.style.appDialog))
                        .setTitle("Permission error")
                        .setMessage("Please allow RPM to access GoogleFit data to continue") // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == FitManager.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    fun requestPermissions() {
        val perms = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera and mic to make video calls",
                RC_VIDEO_APP_PERM,
                *perms
            )
        }
    }

    // Setup a recurring alarm every half hour
    fun scheduleAlarm() {
        cancelAlarm()
        // Construct an intent that will execute the AlarmReceiver
        val intent = Intent(applicationContext, SyncReceiver::class.java)
        // Create a PendingIntent to be triggered when the alarm goes off
        val pIntent = PendingIntent.getBroadcast(
            this,
            SyncReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Setup periodic alarm every every half hour from this point onwards
        val firstMillis = System.currentTimeMillis() // alarm is set right away
        val alarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pIntent
        )
    }

    private fun cancelAlarm() {
        val intent = Intent(applicationContext, SyncReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(
            this,
            SyncReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
    }

}