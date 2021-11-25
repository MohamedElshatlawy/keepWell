package com.cornetelevated.clinics.screens.authentication.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.authentication.LoginActivity
import com.cornetelevated.corehealth.extensions.isValidEmail
import com.cornetelevated.corehealth.extensions.isValidPassword
import com.cornetelevated.corehealth.extensions.isValidPhone
import com.cornetelevated.corehealth.extensions.isValidUsername
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.network.request.RegisterRequest
import com.cornetelevated.corehealth.screens.fragments.Fragment
import com.cornetelevated.corehealth.screens.views.TextField
import java.util.*

class RegisterFragment: Fragment() {

    private lateinit var usernameTextField: TextField
    private lateinit var phoneTextField: TextField
    private lateinit var firstNameTextField: TextField
    private lateinit var lastNameTextField: TextField
    private lateinit var dobTextField: TextField
    private lateinit var emailTextField: TextField
    private lateinit var passwordTextField: TextField
    private lateinit var confirmPasswordTextField: TextField

    private lateinit var registerButton: Button
    private lateinit var rootView: View

    private var isEmailValid: Boolean = false
    private var isUsernameValid: Boolean = false
    private var isMobileValid: Boolean = false
    private var isFirstNameValid: Boolean = false
    private var isLastNameValid: Boolean = false
    private var isDobValid: Boolean = false
    private var isPasswordValid: Boolean = false
    private var isConfirmValid: Boolean = false

    private val calendar: Calendar = Calendar.getInstance()
    private var date: Date = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_register, container, false)
        registerButton = rootView.findViewById(R.id.registerButton)

        usernameTextField = rootView.findViewById(R.id.usernameTextField)
        phoneTextField = rootView.findViewById(R.id.phoneTextField)
        firstNameTextField = rootView.findViewById(R.id.firstNameTextField)
        lastNameTextField = rootView.findViewById(R.id.lastNameTextField)
        dobTextField = rootView.findViewById(R.id.dobTextField)
        emailTextField = rootView.findViewById(R.id.emailTextField)
        passwordTextField = rootView.findViewById(R.id.passwordTextField)
        confirmPasswordTextField = rootView.findViewById(R.id.confirmPasswordTextField)

        usernameTextField.errorText = ""
        usernameTextField.placeholderText = getString(R.string.username)
        usernameTextField.inputType = InputType.TYPE_CLASS_TEXT
        usernameTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length > 20 || s.toString().length < 2) {
                    isUsernameValid = false
                    usernameTextField.errorText = getString(R.string.usernameInvalid)
                } else if (!usernameTextField.text.isValidUsername()) {
                    isUsernameValid = false
                    usernameTextField.errorText = getString(R.string.usernameInvalidType)
                } else {
                    isUsernameValid = true
                    usernameTextField.errorText = ""
                }
                registerButton.isEnabled =
                    (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                            && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        phoneTextField.errorText = ""
        phoneTextField.placeholderText = getString(R.string.phone)
        phoneTextField.inputType = InputType.TYPE_CLASS_PHONE
        phoneTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isValidPhone()) {
                    phoneTextField.errorText = ""
                    isMobileValid = true
                } else {
                    isMobileValid = false
                    phoneTextField.errorText = getString(R.string.mobileInvalid)
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                            && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        firstNameTextField.errorText = ""
        firstNameTextField.placeholderText = getString(R.string.firstName)
        firstNameTextField.inputType = InputType.TYPE_CLASS_TEXT
        firstNameTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (firstNameTextField.text.isEmpty()) {
                    isFirstNameValid = false
                    firstNameTextField.errorText = getString(R.string.fnRequired)
                } else {
                    isFirstNameValid = true
                    firstNameTextField.errorText = ""
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                            && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        lastNameTextField.errorText = ""
        lastNameTextField.placeholderText = getString(R.string.lastName)
        lastNameTextField.inputType = InputType.TYPE_CLASS_TEXT
        lastNameTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (lastNameTextField.text.isEmpty()) {
                    isLastNameValid = false
                    lastNameTextField.errorText = getString(R.string.lnRequired)
                } else {
                    isLastNameValid = true
                    lastNameTextField.errorText = ""
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                        && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        val datePicker = DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = monthOfYear
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                date = calendar.time
                dobTextField.text = FitManager.dateFormat.format(date)
            }

        dobTextField.errorText = ""
        dobTextField.placeholderText = getString(R.string.dateOfBirth)
        dobTextField.setClickListener(View.OnClickListener {
            activity?.let {
                val dialog = DatePickerDialog(
                    it,
                    datePicker,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                dialog.datePicker.maxDate = Date().time
                dialog.show()
            }
        })
        dobTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (dobTextField.text.isEmpty()) {
                    isDobValid = false
                    dobTextField.errorText = getString(R.string.dobRequired)
                } else {
                    isDobValid = true
                    dobTextField.errorText = ""
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                        && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        emailTextField.errorText = ""
        emailTextField.placeholderText = getString(R.string.email)
        emailTextField.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (emailTextField.text.isValidEmail()) {
                    isEmailValid = true
                    emailTextField.errorText = ""
                } else {
                    isEmailValid = false
                    emailTextField.errorText = getString(R.string.emailInvalid)
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                        && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        passwordTextField.errorText = ""
        passwordTextField.placeholderText = getString(R.string.password)
        passwordTextField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordTextField.setPassword()
        passwordTextField.addTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isValidPassword()) {
                    passwordTextField.errorText = ""
                    isPasswordValid = true
                } else {
                    isPasswordValid = false
                    passwordTextField.errorText = getString(R.string.passwordRequired)
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                            && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
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
                if (s.toString() == passwordTextField.text) {
                    isConfirmValid = true
                    confirmPasswordTextField.errorText = ""
                } else {
                    isConfirmValid = false
                    confirmPasswordTextField.errorText = getString(R.string.passwordMismatch)
                }
                registerButton.isEnabled = (isUsernameValid && isMobileValid && isFirstNameValid && isLastNameValid
                            && isDobValid && isEmailValid && isPasswordValid && isConfirmValid)
            }
        })

        registerButton.setOnClickListener { registerTapped() }

        if (usernameTextField.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return rootView
    }

    private fun registerTapped() {

        // check if username is empty
        if (usernameTextField.text.isEmpty()) {
            usernameTextField.errorText = getString(R.string.usernameRequired)
            return
        }
        // check if mobile number is empty
        if (phoneTextField.text.isEmpty()) {
            phoneTextField.errorText = getString(R.string.mobileRequired)
            return
        }
        // check if first name is empty
        if (firstNameTextField.text.isEmpty()) {
            firstNameTextField.errorText = getString(R.string.fnRequired)
            return
        }
        // check if last name is empty
        if (lastNameTextField.text.isEmpty()) {
            lastNameTextField.errorText = getString(R.string.lnRequired)
            return
        }
        // check if dateOfBirth is empty
        if (dobTextField.text.isEmpty()) {
            dobTextField.errorText = getString(R.string.dobRequired)
            return
        }
        // check if email is empty
        if (emailTextField.text.isEmpty()) {
            emailTextField.errorText = getString(R.string.emailRequired)
            return
        }
        // check if password is empty
        if (passwordTextField.text.isEmpty()) {
            passwordTextField.errorText = getString(R.string.passwordRequired)
            return
        }
        // check if confirm password is empty
        if (confirmPasswordTextField.text.isEmpty()) {
            confirmPasswordTextField.errorText = getString(R.string.confirmPasswordRequired)
            return
        }

        val model = RegisterRequest(phoneTextField.text, usernameTextField.text, firstNameTextField.text, lastNameTextField.text, dobTextField.text,
            emailTextField.text, passwordTextField.text)
        (activity as LoginActivity).register(model, registerButton)

    }

}