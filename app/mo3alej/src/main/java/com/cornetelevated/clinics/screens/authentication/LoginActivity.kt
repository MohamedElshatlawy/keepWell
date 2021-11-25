package com.cornetelevated.clinics.screens.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cornetelevated.clinics.screens.authentication.fragments.LoginFragment
import com.cornetelevated.clinics.screens.authentication.fragments.RegisterFragment
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.HomeActivity
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.LoginRequest
import com.cornetelevated.corehealth.network.request.RegisterRequest
import com.cornetelevated.corehealth.network.response.JSONResponse
import com.cornetelevated.corehealth.network.response.LoginResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : LoginActivity() {

    private var loginFragment: LoginFragment = LoginFragment()
    private var registerFragment: RegisterFragment = RegisterFragment()
    private var fragments = arrayListOf(loginFragment, registerFragment)

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        spinner = findViewById(R.id.spinner)

        loginButton.setOnClickListener {
            loadFragment(0)
            loginButton.background = ContextCompat.getDrawable(this, R.drawable.bg_segment_left_seleted)
            loginButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryButtonText))
            registerButton.background = ContextCompat.getDrawable(this, R.drawable.bg_segment_right)
            registerButton.setTextColor(ContextCompat.getColor(this, R.color.colorBorderedButtonText))
        }

        registerButton.setOnClickListener {
            loadFragment(1)
            loginButton.background = ContextCompat.getDrawable(this, R.drawable.bg_segment_left)
            loginButton.setTextColor(ContextCompat.getColor(this, R.color.colorBorderedButtonText))
            registerButton.background = ContextCompat.getDrawable(this, R.drawable.bg_segment_right_selected)
            registerButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryButtonText))
        }

        spinner?.visibility = View.GONE
        loadFragment(0)
    }

    private fun loadFragment(index: Int) {
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

    fun login(mobile: String, password: String, actionButton: Button? = null) {
        showProgress(actionButton)
        val model = LoginRequest(null, mobile, password)
        val call: Call<LoginResponse> = APIClient.authInterface.login(model.body())
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                hideProgress(actionButton)
                Log.d("onResponseTag", "onResponse: $response")
                response?.body()?.let { body ->
                    if (body.success) {
                        storeCredentials(mobile, password)
                        user = body.response
                        handleUser()
                    } else {
                        if (response.body()?.error == "User not activated") {
                            val verify = Intent(this@LoginActivity, VerifyActivity::class.java)
                            verify.putExtra("mobile", mobile)
                            verify.putExtra("isActivate", true)
                            verify.putExtra("shouldRequestCode", true)
                            startActivity(verify)
                            finish()
                        }
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

    fun register(model: RegisterRequest, actionButton: Button? = null) {
        showProgress(actionButton)
        val call: Call<JSONResponse> = APIClient.authInterface.register(model.body())
        call.enqueue(object : Callback<JSONResponse> {
            override fun onResponse(call: Call<JSONResponse>?, response: Response<JSONResponse>?) {
                hideProgress(actionButton)
                Log.d("onResponseTag", "onResponse: $response")
                response?.body()?.let { body ->
                    if (body.success) {
                        // Show verify
                        val verify = Intent(this@LoginActivity, VerifyActivity::class.java)
                        verify.putExtra("mobile", model.mobile)
                        verify.putExtra("isActivate", true)
                        startActivity(verify)
                        finish()

                    } else {
                        Toast.makeText(applicationContext, body.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<JSONResponse>?, t: Throwable?) {
                hideProgress()
                Log.d("onResponseTag", "onFailure: " + t?.message)
                Toast.makeText(applicationContext, t?.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun handleUser() {
        user?.let {
            if (it.userNeedsToResetPassword) {
                // Show Change Password (Not implemented in Mo3alej)
            } else {
                sendFCMToken()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

}