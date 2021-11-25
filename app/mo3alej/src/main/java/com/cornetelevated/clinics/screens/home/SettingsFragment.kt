package com.cornetelevated.clinics.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.authentication.ChangePasswordActivity
import com.cornetelevated.corehealth.screens.activities.AddReadingActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import de.adorsys.android.securestoragelibrary.SecurePreferences

class SettingsFragment: Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var notificationsTextView: TextView
    private lateinit var insuranceTextView: TextView
    private lateinit var paymentsTextView: TextView
    private lateinit var contactUsTextView: TextView
    private lateinit var changePasswordTextView: TextView
    private lateinit var languageTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_settings, container, false)
        usernameTextView = root.findViewById(R.id.usernameTextView)
        notificationsTextView = root.findViewById(R.id.notificationsTextView)
        insuranceTextView = root.findViewById(R.id.insuranceTextView)
        paymentsTextView = root.findViewById(R.id.paymentsTextView)
        contactUsTextView = root.findViewById(R.id.contactUsTextView)
        changePasswordTextView = root.findViewById(R.id.changePasswordTextView)
        languageTextView = root.findViewById(R.id.languageTextView)

        logoutButton = root.findViewById(R.id.logoutButton)

        changePasswordTextView.setOnClickListener { v: View? ->
            startActivity(Intent(activity, ChangePasswordActivity::class.java))
        }

        logoutButton.setOnClickListener { logOutTapped() }

        return root
    }

    private fun logOutTapped() {
        SecurePreferences.removeValue("KW.isLoggedIn")
        SecurePreferences.removeValue("KW.lastSyncDate")
        activity?.finish()
    }

}