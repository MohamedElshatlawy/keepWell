package com.cornetelevated.keepwell.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cornetelevated.project.app.kotlin.R
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.screens.activities.Activity
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import de.adorsys.android.securestoragelibrary.SecurePreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePasswordActivity: Activity() {

    private lateinit var pwTextField: EditText
    private lateinit var confirmTextField: EditText
    private lateinit var actionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pass)
        pwTextField = findViewById(R.id.passTextField)
        confirmTextField = findViewById(R.id.confirmPassTextField)
        actionButton = findViewById(R.id.actionButton)
        actionButton.setOnClickListener {
            actionTapped()
        }
        spinner = findViewById(R.id.spinner)
        spinner?.visibility = View.GONE
    }

    private fun actionTapped() {
        hideKeyboard()
        val confirm = confirmTextField.text.toString()
        val password = pwTextField.text.toString()
        if (password.isEmpty()) {
            pwTextField.error = "Password can't be empty"
            return
        }
        if (confirm.isEmpty() || confirm != password) {
            confirmTextField.error = "Password & confirm password should match"
            return
        }
        showProgress(actionButton)

        val map = HashMap<String, Any>()
        map["Password"] = password
        val call: Call<ResponseBody> = APIClient.authInterface.setPassword(LoginActivity.getUserToken(), map)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                hideProgress(actionButton)
                Log.d("onResponseTag", "onResponse: $response")
                SecurePreferences.setValue("KW.password", password)
                SecurePreferences.setValue("KW.isLoggedIn", true)
                finish()
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                hideProgress(actionButton)
                Log.d("onResponseTag", "onFailure: " + t?.message)
                Toast.makeText(applicationContext, t?.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

}