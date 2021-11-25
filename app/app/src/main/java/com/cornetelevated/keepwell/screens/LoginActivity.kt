package com.cornetelevated.keepwell.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cornetelevated.project.app.kotlin.R
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.LoginRequest
import com.cornetelevated.corehealth.network.response.LoginResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import de.adorsys.android.securestoragelibrary.SecurePreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : LoginActivity() {

    private lateinit var unTextField: EditText
    private lateinit var pwTextField: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        spinner = findViewById(R.id.spinner)
        unTextField = findViewById(R.id.usernameTextField)
        pwTextField = findViewById(R.id.passwordTextField)
        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            loginTapped()
        }
        spinner?.visibility = View.GONE
    }

    override fun checkCredentials() {
        SecurePreferences.getStringValue("KW.username", "")?.let { username ->
            SecurePreferences.getStringValue("KW.password", "")?.let { password ->
                val isLoggedIn = SecurePreferences.getBooleanValue("KW.isLoggedIn", false)
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    unTextField.setText(username)
                    pwTextField.setText(password)
                    if (isLoggedIn) {
                        login(username, password)
                    } else {
                        if (unTextField.requestFocus()) {
                            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        }
                    }
                } else {
                    if (unTextField.requestFocus()) {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                }
            }
        }
    }

    private fun loginTapped() {
        hideKeyboard()
        val username = unTextField.text.toString()
        val password = pwTextField.text.toString()
        if (username.isEmpty()) {
            unTextField.error = "Username can't be empty"
            return
        }
        if (password.isEmpty()) {
            pwTextField.error = "Password can't be empty"
            return
        }
        login(username, password)
    }

    private fun login(username: String, password: String) {
        showProgress(loginButton)
        val model = LoginRequest(username, null, password)
        val call: Call<LoginResponse> = APIClient.authInterface.login(model.body())
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                hideProgress(loginButton)
                Log.d("onResponseTag", "onResponse: $response")
                response?.body()?.let { body ->
                    if (body.success) {
                        storeCredentials(username, password)
                        user = body.response
                        handleUser()
                    } else {
                        Toast.makeText(applicationContext, body.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                hideProgress()
                Log.d("onResponseTag", "onFailure: " + t?.message)
                Toast.makeText(applicationContext, t?.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun handleUser() {
        user?.let {
            if (it.userNeedsToResetPassword) {
                // Show Create pass
                val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
                startActivity(intent)
            } else {
                sendFCMToken()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

}