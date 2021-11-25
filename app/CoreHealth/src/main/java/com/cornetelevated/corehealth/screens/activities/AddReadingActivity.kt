package com.cornetelevated.corehealth.screens.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.models.fit.DataReading
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.ReadingDatesResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddReadingActivity : Activity() {

    enum class ReadingType {
        BloodPressure, Glucose, Weight, HeartRate, Oxygen, Steps
    }

    private var date = Date()
    private val myCalendar: Calendar = Calendar.getInstance()
    private var readingType = ReadingType.Weight
    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reading)
        val intent = intent
        readingType = intent.getSerializableExtra("ReadingType") as ReadingType
        spinner = findViewById(R.id.spinner)
        spinner?.visibility = View.GONE
        val welcomeLabel = findViewById<TextView>(R.id.welcomeLabel)
        val titleLabel = findViewById<TextView>(R.id.titleLabel)
        val dateText = findViewById<EditText>(R.id.dateText)
        val valueText = findViewById<EditText>(R.id.valueText)
        val value2Text = findViewById<EditText>(R.id.value2Text)
        val addButton = findViewById<Button>(R.id.addButton)
        dateText.setText(FitManager.dateFormatUS.format(date))
        startDate = Date().time - 1000L * 60 * 60 * 24 * 29
        endDate = Date().time
        var welcomeString = resources.getString(R.string.welcome)
        welcomeString += " "
        val sharedPreferences = getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
        welcomeString += sharedPreferences.getString("RPM_firstName", "")
        welcomeLabel.text = welcomeString
        value2Text.visibility = View.GONE
        when (readingType) {
            ReadingType.Steps -> {
                titleLabel.text = getString(R.string.add_steps)
                valueText.hint = "Steps count"
            }
            ReadingType.Oxygen -> {
                titleLabel.text = getString(R.string.add_oxygen)
                valueText.hint = "Oxygen saturation (%)"
            }
            ReadingType.Weight -> {
                titleLabel.text = getString(R.string.add_weight)
                valueText.hint = "Weight (lbs)"
            }
            ReadingType.Glucose -> {
                titleLabel.text = getString(R.string.add_glucose)
                valueText.hint = "Glucose (mg/dl)"
            }
            ReadingType.HeartRate -> {
                titleLabel.text = getString(R.string.add_heart_rate)
                valueText.hint = "Heart rate (BPM)"
            }
            ReadingType.BloodPressure -> {
                value2Text.visibility = View.VISIBLE
                titleLabel.text = getString(R.string.add_blood_pressure)
                value2Text.hint = "Sys (mmHg)"
                valueText.hint = "Dia (mmHg)"
            }
        }
        val datePicker = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            date = myCalendar.time
            dateText.setText(FitManager.dateFormatUS.format(date))
        }
        dateText.setOnClickListener {
            val dialog = DatePickerDialog(
                this@AddReadingActivity,
                datePicker,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            dialog.datePicker.maxDate = endDate
            dialog.datePicker.minDate = startDate
            dialog.show()
        }
        addButton.setOnClickListener {
            dateText.clearFocus()
            valueText.clearFocus()
            value2Text.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(valueText.windowToken, 0)
            try {
                val value1 = valueText.text.toString().toFloat()
                if (value1 > 0) {
                    val reading = DataReading()
                    reading.date = date
                    reading.isManual = true
                    reading.value = value1.toDouble()

                    var call: Call<ResponseBody> = APIClient.readingsInterface.sendGlucoseReading(LoginActivity.getUserToken(), reading.requestBody())

                    when (readingType) {
                        ReadingType.Steps -> call = APIClient.readingsInterface.sendStepsReading(LoginActivity.getUserToken(), reading.stepsBody())
                        ReadingType.Oxygen -> {
                            reading.value = value1.toDouble() / 100
                            call = APIClient.readingsInterface.sendOxygenReading(LoginActivity.getUserToken(), reading.oxygenBody())
                        }
                        ReadingType.Weight -> {
                            reading.unitId = 6
                            reading.value = value1.toDouble() * 0.453592
                            call = APIClient.readingsInterface.sendScaleReading(LoginActivity.getUserToken(), reading.scaleBody())
                        }
                        ReadingType.HeartRate -> call = APIClient.readingsInterface.sendPulseReading(LoginActivity.getUserToken(), reading.pulseBody())
                        ReadingType.BloodPressure -> {
                            reading.unitId = 3
                            val value2 = value2Text.text.toString().toFloat()
                            if (value2 > 0) {
                                reading.minValue = value1.toDouble()
                                reading.maxValue = value2.toDouble()
                                call = APIClient.readingsInterface.sendBloodPressureReading(LoginActivity.getUserToken(), reading.bloodPressureBody())
                            } else {
                                Toast.makeText(applicationContext, "Invalid value", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                        }
                        ReadingType.Glucose -> reading.unitId = 4
                    }

                    spinner?.visibility = View.VISIBLE
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                            spinner?.visibility = View.GONE
                            Log.d("onResponseTag", "onResponse: $response")
                            Toast.makeText(applicationContext, "Reading added successfully", Toast.LENGTH_SHORT).show()
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent("fetchServerReadings"))
                            finish()
                        }

                        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                            // hide progressbar
                            spinner?.visibility = View.GONE
                            Log.d("onResponseTag", "onFailure: " + t?.message)
                            Toast.makeText(applicationContext, "Error adding Reading: " + t?.message, Toast.LENGTH_SHORT).show()
                        }
                    })

                } else {
                    Toast.makeText(applicationContext, "Invalid value", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Invalid value", Toast.LENGTH_SHORT).show()
            }
        }
        fetchDates()
    }

    private fun fetchDates() {
        spinner?.visibility = View.VISIBLE
        val call: Call<ReadingDatesResponse> = APIClient.readingsInterface.getAvailableReadingDates(LoginActivity.getUserToken())
        call.enqueue(object : Callback<ReadingDatesResponse> {
            override fun onResponse(call: Call<ReadingDatesResponse>?, response: Response<ReadingDatesResponse>?) {
                spinner?.visibility = View.GONE
                Log.d("onResponseTag", "onResponse: $response")
                response?.body()?.response?.minDate?.let {
                    (FitManager.dateTimeFormat.parse(it))?.let {
                        startDate = it.time
                    }
                }
                response?.body()?.response?.maxDate?.let {
                    (FitManager.dateTimeFormat.parse(it))?.let {
                        endDate = it.time
                    }
                }
            }

            override fun onFailure(call: Call<ReadingDatesResponse>?, t: Throwable?) {
                // hide progressbar
                spinner?.visibility = View.GONE
                Log.d("onResponseTag", "onFailure: " + t?.message)
                Toast.makeText(applicationContext, "Error adding Reading: " + t?.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}