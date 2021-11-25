package com.cornetelevated.corehealth.models.fit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.SyncDatesResponse
import com.cornetelevated.corehealth.screens.activities.HomeActivity
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import de.adorsys.android.securestoragelibrary.SecurePreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FitManager(private var context: Context) {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_BODY_SENSORS = 1
        private const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION: Int = 2
        const val RC_SIGN_IN = 1
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val dateTimeFormatExpanded = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val dateFormatUS = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        private var instance: FitManager? = null

        fun getInstance(context: Context): FitManager? {
            if (instance == null) {
                instance = FitManager(context)
            }
            return instance
        }

    }

    private var fetchedReadings: FitReadings = FitReadings()
    private val syncRequest: SyncRequest = SyncRequest()
    private val timeStamp: SyncTimeStamp = SyncTimeStamp()
    private var shouldSync = false

    var homeActivity: HomeActivity? = null

    fun checkPermissions() {
        if (homeActivity != null) {
            if (ContextCompat.checkSelfPermission(homeActivity!!, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(
                    homeActivity!!,
                    arrayOf(Manifest.permission.BODY_SENSORS),
                    MY_PERMISSIONS_REQUEST_BODY_SENSORS
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            homeActivity!!,
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(
                            homeActivity!!,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION
                        )
                    } else {
                        signInToGoogle()
                    }
                } else {
                    signInToGoogle()
                }
            }
        }
    }

    private fun signInToGoogle() {
        Log.i("", "signInToGoogle requested !!!")
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.FITNESS_BLOOD_PRESSURE_READ_WRITE))
            .requestScopes(Scope(Scopes.FITNESS_BLOOD_GLUCOSE_READ_WRITE))
            .requestScopes(Scope(Scopes.FITNESS_BODY_READ_WRITE))
            .requestScopes(Scope(Scopes.FITNESS_OXYGEN_SATURATION_READ_WRITE))
            .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
            .requestScopes(Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(context, options)
        val signInIntent = mGoogleSignInClient.signInIntent
        homeActivity!!.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun fetchData(sync: Boolean) {
        if (sync) {
            shouldSync = true
        }
        if (!syncRequest.isSyncing()) {
            syncRequest.startSync()
            val cal = Calendar.getInstance()
            val now = Date()
            cal.time = now
            val endTime = cal.timeInMillis
            cal.add(Calendar.YEAR, -1)
            val startTime = cal.timeInMillis
            Log.i(
                "",
                dateTimeFormat.format(now) + " : fetchData ---- start: " + dateTimeFormat.format(startTime) + "  --  " + dateTimeFormat.format(endTime) + " ----"
            )
            val readRequest = DataReadRequest.Builder()
                .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .read(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
                .read(HealthDataTypes.TYPE_OXYGEN_SATURATION)
                .read(DataType.TYPE_HEART_RATE_BPM)
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
            val acc = GoogleSignIn.getLastSignedInAccount(context)
            if (acc != null) {
                val response = Fitness.getHistoryClient(context, acc)
                    .readData(readRequest)
                val fitRequest = FitRequest()
                val fitResponseResult: FitResponseResult = object : FitResponseResult {
                    override fun onSuccess(readings: FitReadings?) {
                        readings?.let {
                            fetchedReadings = it
                            fetchSteps()
                        }
                    }

                    override fun onFailure(error: String?) {
                        if (homeActivity != null) {
                            syncRequest.endSync()
                            homeActivity!!.runOnUiThread {
                                AlertDialog.Builder(
                                    ContextThemeWrapper(
                                        homeActivity,
                                        R.style.appDialog
                                    )
                                ) //new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Login error")
                                    .setMessage(error) // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(R.string.ok, null)
                                    //.setIcon(R.drawable.ic_dialog_alert)
                                    .show()
                            }
                        }
                    }
                }
                fitRequest.setTask(response, fitResponseResult)
                Thread(fitRequest).start()
            } else {
                updateSettings()
            }
        }
    }

    private fun fetchSteps() {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis
        Log.i(
            "",
            dateTimeFormat.format(now) + " : fetchSteps ---- start: " + dateTimeFormat.format(startTime) + "  --  " + dateTimeFormat.format(endTime) + " ----"
        )
        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA) //.aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            //.bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()
        val acc = GoogleSignIn.getLastSignedInAccount(homeActivity)
        if (acc != null) {
            val response =
                Fitness.getHistoryClient(homeActivity!!, acc)
                    .readData(readRequest)
            val fitRequest = FitRequest()
            val fitResponseResult: FitResponseResult = object : FitResponseResult {
                override fun onSuccess(readings: FitReadings?) {
                    val aggregate = ArrayList<DataReading>()
                    readings?.let {
                        for (i in 0 until it.steps.size) {
                            var location = -1
                            val step = readings.steps[i]
                            val stepDate = dateFormat.format(step.date)
                            for (j in aggregate.indices) {
                                val reading = aggregate[j]
                                val readingDate = dateFormat.format(reading.date)
                                if (stepDate == readingDate) {
                                    location = j
                                }
                            }
                            if (location == -1) {
                                aggregate.add(step)
                            } else {
                                val reading = aggregate[location]
                                reading.value += step.value
                                aggregate.removeAt(location)
                                aggregate.add(location, reading)
                            }
                        }
                    }
                    fetchedReadings.steps = aggregate
                    if (shouldSync) {
                        syncReadings()
                    } else {
                        syncRequest.endSync()
                    }
                }

                override fun onFailure(error: String?) {
                    syncRequest.endSync()
                    if (homeActivity != null) {
                        homeActivity!!.runOnUiThread {
                            AlertDialog.Builder(
                                ContextThemeWrapper(
                                    homeActivity,
                                    R.style.appDialog
                                )
                            ) //new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Login error")
                                .setMessage(error) // A null listener allows the button to dismiss the dialog and take no further action.
                                //.setNegativeButton(R.string.ok, null)
                                //.setIcon(R.drawable.ic_dialog_alert)
                                .show()
                        }
                    }
                }
            }
            fitRequest.setTask(response, fitResponseResult)
            Thread(fitRequest).start()
        } else {
            updateSettings()
        }
    }

    private fun syncReadings() {
        shouldSync = false
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        cal.add(Calendar.YEAR, -1)
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
        timeStamp.steps = sharedPreferences.getLong("lastSyncTS_steps", 0)
        timeStamp.glucose = sharedPreferences.getLong("lastSyncTS_glucose", 0)
        timeStamp.pulse = sharedPreferences.getLong("lastSyncTS_pulse", 0)
        timeStamp.oxygen = sharedPreferences.getLong("lastSyncTS_oxygen", 0)
        timeStamp.bloodPressure = sharedPreferences.getLong("lastSyncTS_bp", 0)
        timeStamp.scale = sharedPreferences.getLong("lastSyncTS_scale", 0)

        val call: Call<SyncDatesResponse> = APIClient.readingsInterface.getLastReadingDates(LoginActivity.getUserToken())
        call.enqueue(object: Callback<SyncDatesResponse> {
            override fun onFailure(call: Call<SyncDatesResponse>, t: Throwable) {
                startSyncAnalysis()
            }

            override fun onResponse(call: Call<SyncDatesResponse>, response: Response<SyncDatesResponse>) {
                response.body()?.response?.let {
                    it.stepsData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.steps = it.time - 1000 }
                    }
                    it.glucosesData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.glucose = it.time + 1000 }
                    }
                    it.oximetersData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.oxygen = it.time + 1000 }
                    }
                    it.bloodpressuresData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.bloodPressure = it.time + 1000 }
                    }
                    it.scalesData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.scale = it.time + 1000 }
                    }
                    it.plusesData?.let {
                        dateTimeFormat.parse(it, ParsePosition(0))?.let { timeStamp.pulse = it.time + 1000 }
                    }
                    startSyncAnalysis()
                }
            }
        })

    }

    private fun startSyncAnalysis() {
        for (i in 0 until fetchedReadings.pulse.size) {
            val reading = fetchedReadings.pulse[i]
            if (reading.date.time > timeStamp.pulse) {
                syncRequest.readings.pulse.add(reading)
            }
        }
        for (i in 0 until fetchedReadings.bloodPressure.size) {
            val reading = fetchedReadings.bloodPressure[i]
            if (reading.date.time > timeStamp.bloodPressure) {
                syncRequest.readings.bloodPressure.add(reading)
            }
        }
        for (i in 0 until fetchedReadings.glucose.size) {
            val reading = fetchedReadings.glucose[i]
            if (reading.date.time > timeStamp.glucose) {
                syncRequest.readings.glucose.add(reading)
            }
        }
        for (i in 0 until fetchedReadings.oxygen.size) {
            val reading = fetchedReadings.oxygen[i]
            if (reading.date.time > timeStamp.oxygen) {
                syncRequest.readings.oxygen.add(reading)
            }
        }
        for (i in 0 until fetchedReadings.weight.size) {
            val reading = fetchedReadings.weight[i]
            if (reading.date.time > timeStamp.scale) {
                syncRequest.readings.weight.add(reading)
            }
        }
        for (i in 0 until fetchedReadings.steps.size) {
            val reading = fetchedReadings.steps[i]
            if (reading.date.time > timeStamp.steps) {
                syncRequest.readings.steps.add(reading)
            }
        }
        sendNextReading()
    }

    private fun sendNextReading() {
        when (syncRequest.currentType) {
            0 -> {
                if (syncRequest.readings.pulse.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.pulse[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendPulseReading("", reading.pulseBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_pulse", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            1 -> {
                if (syncRequest.readings.bloodPressure.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.bloodPressure[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendBloodPressureReading("", reading.bloodPressureBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_bp", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            2 -> {
                if (syncRequest.readings.glucose.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.glucose[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendGlucoseReading("", reading.requestBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_glucose", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            3 -> {
                if (syncRequest.readings.oxygen.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.oxygen[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendOxygenReading("", reading.oxygenBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_oxygen", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            4 -> {
                if (syncRequest.readings.weight.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.weight[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendScaleReading("", reading.scaleBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_scale", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            5 -> {
                if (syncRequest.readings.steps.size > syncRequest.currentIndex) {
                    val reading = syncRequest.readings.steps[syncRequest.currentIndex]
                    val call: Call<ResponseBody> = APIClient.readingsInterface.sendStepsReading("", reading.stepsBody())
                    call.enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            updateSettings()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            syncRequest.currentIndex += 1
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putLong("lastSyncTS_steps", reading.date.time)
                            editor.apply()
                            sendNextReading()
                        }
                    })

                } else {
                    syncRequest.currentIndex = 0
                    syncRequest.currentType += 1
                    sendNextReading()
                }
            }
            6 -> {
                updateSettings()
            }
        }
    }

    private fun updateSettings() {
        Log.i("", "sync ended")
        SecurePreferences.setValue("KW.lastSyncDate", dateTimeFormat.format(Date()))
        syncRequest.endSync()
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("updateSyncDate"))
    }

}