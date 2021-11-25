package com.cornetelevated.clinics.screens.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.extensions.isValidPhone
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.SendCodeRequest
import com.cornetelevated.corehealth.network.response.IntResponse
import com.cornetelevated.corehealth.screens.activities.Activity
import com.cornetelevated.corehealth.screens.views.TextField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity: Activity() {

    private lateinit var phoneTextField: TextField
    private lateinit var actionButton: Button

    private var isMobileValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        phoneTextField = findViewById(R.id.phoneTextField)
        actionButton = findViewById(R.id.actionButton)
        spinner = findViewById(R.id.spinner)

        phoneTextField.errorText = ""
        phoneTextField.placeholderText = getString(R.string.phone)
        phoneTextField.inputType = InputType.TYPE_CLASS_PHONE
        phoneTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isValidPhone()) {
                    phoneTextField.errorText = ""
                    isMobileValid = true
                } else {
                    isMobileValid = false
                    phoneTextField.errorText = getString(R.string.mobileInvalid)
                }
                actionButton.isEnabled = isMobileValid
            }
        })

        actionButton.setOnClickListener { actionTapped() }

    }

    private fun actionTapped() {
        if (phoneTextField.text.isEmpty()) {
            phoneTextField.errorText = getString(R.string.mobileRequired)
            return
        }
        showProgress(actionButton)
        val model = SendCodeRequest(phoneTextField.text)
        val call: Call<IntResponse> = APIClient.authInterface.forgotPassword(model.body())

        call.enqueue(object : Callback<IntResponse> {

            override fun onResponse(call: Call<IntResponse>?, response: Response<IntResponse>?) {
                hideProgress(actionButton)
                val verify = Intent(applicationContext, VerifyActivity::class.java)
                verify.putExtra("mobile", phoneTextField.text)
                verify.putExtra("isActivate", false)
                startActivity(verify)
                finish()
            }

            override fun onFailure(call: Call<IntResponse>?, t: Throwable?) {
                hideProgress(actionButton)
                Log.e("ErrorOnFailure", "onFailure: " + t!!.message)
                Toast.makeText(applicationContext, "something went wrong , please try again", Toast.LENGTH_SHORT).show()
            }

        })
    }

}