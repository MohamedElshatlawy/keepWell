package com.cornetelevated.clinics.screens.authentication.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.cornetelevated.clinics.screens.authentication.LoginActivity
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.authentication.ConfirmActivity
import com.cornetelevated.clinics.screens.authentication.ForgotPasswordActivity
import com.cornetelevated.corehealth.extensions.isValidPhone
import com.cornetelevated.corehealth.screens.fragments.Fragment
import com.cornetelevated.corehealth.screens.views.TextField
import de.adorsys.android.securestoragelibrary.SecurePreferences

class LoginFragment: Fragment() {

    private lateinit var phoneTextField: TextField
    private lateinit var passwordTextField: TextField

    private lateinit var loginButton: Button
    private lateinit var forgotTextView: TextView
    private lateinit var rootView: View

    private var isMobileValid: Boolean = false
    private var isPasswordValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_login, container, false)

        loginButton = rootView.findViewById(R.id.loginButton)
        forgotTextView = rootView.findViewById(R.id.forgotTextView)

        passwordTextField = rootView.findViewById(R.id.passwordTextField)
        phoneTextField = rootView.findViewById(R.id.phoneTextField)

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
                loginButton.isEnabled = (isMobileValid && isPasswordValid)
            }
        })

        passwordTextField.errorText = ""
        passwordTextField.placeholderText = getString(R.string.password)
        passwordTextField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordTextField.setPassword()
        passwordTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    passwordTextField.errorText = ""
                    isPasswordValid = true
                } else {
                    isPasswordValid = false
                    passwordTextField.errorText = getString(R.string.passwordRequired)
                }
                loginButton.isEnabled = (isMobileValid && isPasswordValid)
            }
        })

        loginButton.setOnClickListener { loginTapped() }
        forgotTextView.setOnClickListener {
            activity?.let {
                it.startActivity(Intent(it, ForgotPasswordActivity::class.java))
            }
        }

        checkCredentials()

        return rootView
    }

    private fun loginTapped() {
        if (phoneTextField.text.isEmpty()) {
            phoneTextField.errorText = getString(R.string.mobileRequired)
            return
        }
        // check if phone number is valid
        if (!phoneTextField.text.isValidPhone()) {
            phoneTextField.errorText = getString(R.string.mobileInvalid)
            return
        }
        // check if password is empty
        if (passwordTextField.text.isEmpty()) {
            passwordTextField.errorText = getString(R.string.passwordRequired)
            return
        }
        (activity as LoginActivity).login(phoneTextField.text, passwordTextField.text, loginButton)
    }

    private fun checkCredentials() {
        SecurePreferences.getStringValue("KW.username", "")?.let { username ->
            SecurePreferences.getStringValue("KW.password", "")?.let { password ->
                val isLoggedIn = SecurePreferences.getBooleanValue("KW.isLoggedIn", false)
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    phoneTextField.text = username
                    passwordTextField.text = password
                    if (isLoggedIn) {
                        loginTapped()
                    } else {
                        if (phoneTextField.requestFocus()) {
                            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        }
                    }
                } else {
                    if (phoneTextField.requestFocus()) {
                        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                }
            }
        }
    }

}