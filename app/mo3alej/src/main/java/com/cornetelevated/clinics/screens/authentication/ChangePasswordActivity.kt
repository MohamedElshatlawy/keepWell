package com.cornetelevated.clinics.screens.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.extensions.isValidPassword
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.ChangePasswordRequest
import com.cornetelevated.corehealth.network.request.ResetPasswordRequest
import com.cornetelevated.corehealth.network.response.StringResponse
import com.cornetelevated.corehealth.screens.activities.Activity
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.views.TextField
import de.adorsys.android.securestoragelibrary.SecurePreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity: Activity() {
    private lateinit var oldPasswordTextField: TextField
    private lateinit var newPasswordTextField: TextField
    private lateinit var confirmPasswordTextField: TextField
    private lateinit var changeButton: Button

    private var userId: Int = 0
    private var isReset = false
    private var isOldPasswordValid = false
    private var isNewPasswordValid = false
    private var isConfirmPasswordValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        userId = intent.getIntExtra("userId", 0)
        isReset = intent.getBooleanExtra("isReset", false)

        oldPasswordTextField = findViewById(R.id.oldPasswordTextField)
        newPasswordTextField = findViewById(R.id.newPasswordTextField)
        confirmPasswordTextField = findViewById(R.id.confirmPasswordTextField)
        changeButton = findViewById(R.id.actionButton)
        spinner = findViewById(R.id.spinner)

        if (isReset) {
            oldPasswordTextField.visibility = View.GONE
            isOldPasswordValid = true
        } else {
            oldPasswordTextField.visibility = View.VISIBLE
            isOldPasswordValid = false
        }

        oldPasswordTextField.errorText = ""
        oldPasswordTextField.placeholderText = getString(R.string.oldPassword)
        oldPasswordTextField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        oldPasswordTextField.setPassword()

        newPasswordTextField.errorText = ""
        newPasswordTextField.placeholderText = getString(R.string.password)
        newPasswordTextField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordTextField.setPassword()
        newPasswordTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isValidPassword()) {
                    if (s.toString() == oldPasswordTextField.text) {
                        isNewPasswordValid = false
                        newPasswordTextField.errorText = getString(R.string.samePasswordInvalid)
                    } else {
                        newPasswordTextField.errorText = ""
                        isNewPasswordValid = true
                    }
                } else {
                    isNewPasswordValid = false
                    newPasswordTextField.errorText = getString(R.string.passwordRequired)
                }
                changeButton.isEnabled = (isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid)
            }
        })

        confirmPasswordTextField.errorText = ""
        confirmPasswordTextField.placeholderText = getString(R.string.confirmPassword)
        confirmPasswordTextField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordTextField.setPassword()
        confirmPasswordTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == newPasswordTextField.text) {
                    isConfirmPasswordValid = true
                    confirmPasswordTextField.errorText = ""
                } else {
                    isConfirmPasswordValid = false
                    confirmPasswordTextField.errorText = getString(R.string.passwordMismatch)
                }
                changeButton.isEnabled = (isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid)
            }
        })

        changeButton.setOnClickListener { changeTapped() }

    }

    private fun changeTapped() {

        if (!isReset) {
            if (oldPasswordTextField.text.isEmpty()) {
                oldPasswordTextField.errorText = getString(R.string.old_password_required)
                return
            }
        }
        if (newPasswordTextField.text.isEmpty()) {
            newPasswordTextField.errorText = getString(R.string.passwordRequired)
            return
        }
        if (confirmPasswordTextField.text.isEmpty()) {
            confirmPasswordTextField.errorText = getString(R.string.confirmPasswordRequired)
            return
        }
        if (!isReset) {
            if (oldPasswordTextField.text == newPasswordTextField.text) {
                newPasswordTextField.errorText = getString(R.string.samePasswordInvalid)
                return
            }
        }
        if (confirmPasswordTextField.text != newPasswordTextField.text) {
            confirmPasswordTextField.errorText = getString(R.string.passwordMismatch)
            return
        }

        showProgress(changeButton)
        if (isReset) {
            setPassword(newPasswordTextField.text)
        } else {
            changePassword(oldPasswordTextField.text, newPasswordTextField.text)
        }
    }

    private fun setPassword(newPassword: String) {
        val model = ResetPasswordRequest(userId, newPassword)
        val call: Call<ResponseBody> = APIClient.authInterface.resetPassword(model.body())
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                hideProgress(changeButton)
                SecurePreferences.setValue("KW.password", newPassword)
                showConfirm()
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                hideProgress(changeButton)
                Toast.makeText(applicationContext, t?.message ?: "something went wrong , please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val model = ChangePasswordRequest(oldPassword, newPassword)
        val call: Call<StringResponse> = APIClient.authInterface.changePassword(LoginActivity.getUserToken(), model.body())
        call.enqueue(object : Callback<StringResponse> {
            override fun onResponse(call: Call<StringResponse>?, response: Response<StringResponse>?) {
                hideProgress(changeButton)
                response?.body()?.let {
                    if (it.success) {
                        SecurePreferences.setValue("KW.password", newPassword)
                        showConfirm()
                    } else {
                        Toast.makeText(applicationContext, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<StringResponse>?, t: Throwable?) {
                hideProgress(changeButton)
                Toast.makeText(applicationContext, t?.message ?: "something went wrong , please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showConfirm() {
        val confirm = Intent(this, ConfirmActivity::class.java)
        confirm.putExtra("isReset", isReset)
        startActivity(confirm)
        finish()
    }

}