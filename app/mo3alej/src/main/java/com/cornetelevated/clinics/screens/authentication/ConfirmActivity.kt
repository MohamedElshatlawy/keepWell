package com.cornetelevated.clinics.screens.authentication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.screens.activities.Activity

class ConfirmActivity: Activity() {

    private lateinit var backButton: Button
    private var isReset = false

    override fun onBackPressed() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        isReset = intent.getBooleanExtra("isReset", false)

        backButton = findViewById(R.id.actionButton)
        backButton.setOnClickListener { finish() }

        if (isReset) {
            backButton.text = getString(R.string.backToLogin)
        } else {
            backButton.text = getString(R.string.backToHome)
        }

    }

}