package com.cornetelevated.corehealth.screens.fragments

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.screens.activities.HomeActivity
import de.adorsys.android.securestoragelibrary.SecurePreferences

class SettingsFragment : Fragment() {
    private var dateLabel: TextView? = null
    private var spinner: ProgressBar? = null
    private val notificationReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateSyncDate()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_settings, container, false)
        val logoutButton = root.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            signOutTapped()
        }
        dateLabel = root.findViewById(R.id.dateLabel)
        spinner = root.findViewById(R.id.syncSpinner)
        spinner?.visibility = View.GONE
        val syncButton = root.findViewById<Button>(R.id.syncButton)
        syncButton.setOnClickListener {
            syncTapped()
        }
        updateSyncDate()
        return root
    }

    fun updateSyncDate() {
        val defaultString = resources.getString(R.string.textNoSync)
        val activity: HomeActivity? = activity as HomeActivity?
        if (activity != null) {
            val sharedPreferences: SharedPreferences = activity.getSharedPreferences("RPM_PT", Context.MODE_PRIVATE)
            val dateString = sharedPreferences.getString("lastSyncDate", defaultString)
            activity.runOnUiThread {
                spinner?.visibility = View.GONE
                dateLabel?.text = dateString
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.applicationContext?.let { LocalBroadcastManager.getInstance(it).registerReceiver(notificationReceiver, IntentFilter("updateSyncDate")) }
    }

    override fun onDestroy() {
        activity?.applicationContext?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(notificationReceiver) }
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        updateSyncDate()
    }

    private fun signOutTapped() {
        val activity = activity
        if (activity != null) {
            SecurePreferences.removeValue("KW.isLoggedIn")
            SecurePreferences.removeValue("KW.lastSyncDate")
            activity.finish()
        }
    }

    private fun syncTapped() {
        val activity: HomeActivity? = activity as HomeActivity?
        if (activity != null) {
            spinner?.visibility = View.VISIBLE
            Log.i("", "Sync tapped")
            activity.fetchReadings(true)
        }
    }
}