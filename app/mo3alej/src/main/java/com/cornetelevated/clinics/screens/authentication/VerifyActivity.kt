package com.cornetelevated.clinics.screens.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.SendCodeRequest
import com.cornetelevated.corehealth.network.request.VerifyRequest
import com.cornetelevated.corehealth.network.response.IntResponse
import com.cornetelevated.corehealth.network.response.JSONResponse
import com.cornetelevated.corehealth.screens.activities.Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class VerifyActivity: Activity() {

    private lateinit var phoneLabel: TextView
    private lateinit var resendLayout: LinearLayout
    private lateinit var timerLabel: TextView
    private lateinit var resendTextView: TextView
    private lateinit var errorLabel: TextView
    private lateinit var verifyButton: Button
    private lateinit var pinTextField: PinEntryEditText
    private var isCode: Boolean = false
    private var savedMobile: String = ""
    private var newMobile: String = ""
    private var isActivate: Boolean = false
    private var shouldRequestCode: Boolean = false
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        newMobile = intent.getStringExtra("mobile") ?: "your phone number"
        isActivate = intent.getBooleanExtra("isActivate", false)
        shouldRequestCode = intent.getBooleanExtra("shouldRequestCode", false)

        Log.d("mTimerRunning", "onCreate newMobile= $newMobile")

        spinner = findViewById(R.id.spinner)
        phoneLabel = findViewById(R.id.phoneLabel)
        errorLabel = findViewById(R.id.errorLabel)
        resendLayout = findViewById(R.id.resendLayout)
        resendTextView = findViewById(R.id.resendTextView)
        timerLabel = findViewById(R.id.timerLabel)
        verifyButton = findViewById(R.id.actionButton)
        pinTextField = findViewById(R.id.pinTextField)

        phoneLabel.text = newMobile

        resendLayout.setOnClickListener {
            if (mTimerRunning) {
                Toast.makeText(this, "can not resend code now", Toast.LENGTH_SHORT).show()
            } else {
                mTimerRunning = true
                resendCode()
            }
        }
        pinTextField.setOnPinEnteredListener { str ->
            if (str.length == 6) {
                isCode = true
                verifyCode(str.toString())
            } else {
                isCode = false
            }
            verifyButton.isEnabled = isCode
        }
        verifyButton.setOnClickListener { verifyTapped() }

    }

    private fun verifyTapped() {
        if (isCode) {
            // check if pin text is empty
            if (pinTextField.text.toString().isEmpty()) {
                errorLabel.visibility = View.VISIBLE
                errorLabel.error = "Verification code is required"
                Toast.makeText(this, "Verification code is required", Toast.LENGTH_SHORT).show()
                return
            }
            // check if pin text is valid
            if (pinTextField.text.toString().length != 6) {
                errorLabel.visibility = View.VISIBLE
                errorLabel.error = "In-correct verification code"
                Toast.makeText(this, "In-correct verification code", Toast.LENGTH_SHORT).show()
                return
            }
            errorLabel.visibility = View.GONE
            verifyCode(pinTextField.text.toString())
        }
    }

    private fun startTimer(time: Long) {
        mEndTime = System.currentTimeMillis() + time
        resendTextView.visibility = View.GONE
        timerLabel.visibility = View.VISIBLE
        mTimerRunning = true
        mCountDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
                val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
                val timeLeftFormatted: String =
                    java.lang.String.format(
                        Locale.getDefault(),
                        "Resend will be activated in %02d:%02d", minutes, seconds
                    )
                timerLabel.text = timeLeftFormatted
            }
            override fun onFinish() {
                mTimeLeftInMillis = 0
                mEndTime = 0
                mTimerRunning = false
                resendTextView.visibility = View.VISIBLE
                timerLabel.visibility = View.GONE
            }
        }.start()
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("mobile", newMobile)
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)
        editor.apply()
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        savedMobile = prefs.getString("mobile", "savedMobile") ?: "savedMobile"
        if (savedMobile == newMobile) {
            mTimeLeftInMillis = prefs.getLong("millisLeft", 0)
            mTimerRunning = prefs.getBoolean("timerRunning", false)
            if (mTimerRunning) {
                mEndTime = prefs.getLong("endTime", 0)
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis()

                if (mTimeLeftInMillis < 0) {
                    mTimerRunning = false
                    //  mTimeLeftInMillis = START_TIME_IN_MILLIS
                    mTimeLeftInMillis = 0
                    resendTextView.visibility = View.VISIBLE
                    timerLabel.visibility = View.GONE

                } else {
                    mTimerRunning = true
                    startTimer(mTimeLeftInMillis)
                }
            }
        } else {
            mTimeLeftInMillis = 0
            mTimerRunning = true
            resendCode()
        }
    }

    // resend code method to send a new code to the user
    private fun resendCode() {
        showProgress(verifyButton)
        val model = SendCodeRequest(newMobile)
        var call: Call<IntResponse> = APIClient.authInterface.forgotPassword(model.body())
        if (isActivate) {
            call = APIClient.authInterface.resendCode(model.body())
        }
        call.enqueue(object : Callback<IntResponse> {
            override fun onResponse(call: Call<IntResponse>?, response: Response<IntResponse>?) {
                hideProgress(verifyButton)
                response?.body()?.let {
                    if (it.success) {
                        if (it.response > 0) {
                            Toast.makeText(this@VerifyActivity, "code is sent , enter the new code", Toast.LENGTH_SHORT).show()
                            mTimeLeftInMillis = it.response * 1000L
                            mTimerRunning = true
                            startTimer(mTimeLeftInMillis)
                        } else {
                            Log.d("mTimerRunning", "leftTime =else")
                            mTimerRunning = false
                            resendTextView.visibility = View.VISIBLE
                            timerLabel.visibility = View.GONE
                        }
                    } else {
                        mTimerRunning = false
                        resendTextView.visibility = View.VISIBLE
                        timerLabel.visibility = View.GONE
                        Toast.makeText(this@VerifyActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<IntResponse>?, t: Throwable?) {
                hideProgress(verifyButton)
                mTimerRunning = false
                resendTextView.visibility = View.VISIBLE
                timerLabel.visibility = View.GONE
                Log.d("mTimerRunning", "onFailure: ${t!!.message}")
                Toast.makeText(this@VerifyActivity, "something went wrong , please try again", Toast.LENGTH_SHORT).show()
            }
        })

    }

    // check the validation of the code that the user entered to active this account
    private fun verifyCode(code: String) {
        Log.d("mTimerRunning", "verifyCode")
        showProgress(verifyButton)
        if (isActivate) {
            val model = VerifyRequest(null, code.toInt())
            val call: Call<JSONResponse> = APIClient.authInterface.activate(model.body())
            call.enqueue(object : Callback<JSONResponse> {
                override fun onResponse(call: Call<JSONResponse>?, response: Response<JSONResponse>?) {
                    hideProgress(verifyButton)
                    response?.body()?.let {
                        if (it.success) {
                            Toast.makeText(this@VerifyActivity, "Account had been activated successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@VerifyActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@VerifyActivity, it.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JSONResponse>?, t: Throwable?) {
                    hideProgress(verifyButton)
                    Log.e("ErrorOnFailure", "onFailure: " + t!!.message)
                    Toast.makeText(this@VerifyActivity, t.message ?: "something went wrong , please try again", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            val model = VerifyRequest(newMobile, code.toInt())
            val call: Call<IntResponse> = APIClient.authInterface.verify(model.body())
            call.enqueue(object : Callback<IntResponse> {
                override fun onResponse(call: Call<IntResponse>, response: Response<IntResponse>) {
                    hideProgress(verifyButton)
                    val userId = response.body()?.response ?: -1
                    val error = response.body()?.error ?: ""
                    if ((error.isEmpty()) && (userId != -1)) {
                        val passwordActivity = Intent(this@VerifyActivity, ChangePasswordActivity::class.java)
                        passwordActivity.putExtra("isReset", true)
                        passwordActivity.putExtra("userId", userId)
                        startActivity(passwordActivity)
                        finish()
                    } else {
                        Toast.makeText(this@VerifyActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<IntResponse>, t: Throwable) {
                    hideProgress(verifyButton)
                    Log.e("ErrorOnFailure", "onFailure: " + t.message)
                    Toast.makeText(this@VerifyActivity, t.message ?: "something went wrong , please try again", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

}