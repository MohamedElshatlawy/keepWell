package com.cornetelevated.keepwell.screens

import android.os.Bundle
import android.util.Log
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.project.app.kotlin.R
import com.cornetelevated.corehealth.screens.activities.AddReadingActivity
import com.cornetelevated.corehealth.screens.activities.HomeActivity
import com.cornetelevated.corehealth.screens.fragments.DashboardFragment
import com.cornetelevated.corehealth.screens.fragments.ReadingsHistoryFragment
import com.cornetelevated.corehealth.screens.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity: HomeActivity() {

    private var dashboardFragment: DashboardFragment = DashboardFragment()
    private var settingsFragment: SettingsFragment = SettingsFragment()
    private var historyFragment: ReadingsHistoryFragment = ReadingsHistoryFragment()
    private var canGoBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigationView = findViewById(R.id.navView)
        welcomeLabel = findViewById(R.id.welcomeLabel)
        titleLabel = findViewById(R.id.titleLabel)
        updateWelcomeText()
        fragments = ArrayList()

        dashboardFragment.title = resources.getString(R.string.titleDashboard)
        settingsFragment.title = resources.getString(R.string.titleSettings)
        fragments = arrayListOf(dashboardFragment, settingsFragment, historyFragment)
        navView.setOnNavigationItemSelectedListener {
            var success = false
            when (it.itemId) {
                R.id.navigationDashboard -> {
                    loadFragment(0)
                    fetchServerReadings()
                    success = true
                }
                R.id.navigationSettings -> {
                    loadFragment(1)
                    success = true
                }
            }
            success
        }
        FitManager.getInstance(applicationContext)?.homeActivity = this
        FitManager.getInstance(applicationContext)?.checkPermissions()
    }

    override fun showUI() {
        // Show Dashboard
        loadFragment(0)
        Log.i("", "handleSignInResult")
        fetchServerReadings()
        fetchReadings(true)
        scheduleAlarm()
        requestPermissions()
    }

    override fun onBackPressed() {
        if (canGoBack) {
            canGoBack = false
            loadFragment(0)
        }
    }

    override fun updateDashboard() {
        dashboardFragment.updateScale(readings.weight)
        dashboardFragment.updateSteps(readings.steps)
        dashboardFragment.updateBloodPressure(readings.bloodPressure)
        dashboardFragment.updateGlucose(readings.glucose)
        dashboardFragment.updateHeartRate(readings.pulse)
        dashboardFragment.updateOxygen(readings.oxygen)
    }

    override fun showReadingHistory(type: AddReadingActivity.ReadingType) {
        historyFragment.readingType = type
        when (type) {
            AddReadingActivity.ReadingType.Steps -> {
                historyFragment.updateData(readings.steps)
                historyFragment.title = resources.getString(R.string.unitSteps)
            }
            AddReadingActivity.ReadingType.Oxygen -> {
                historyFragment.updateData(readings.oxygen)
                historyFragment.title = resources.getString(R.string.textOxygen)
            }
            AddReadingActivity.ReadingType.Weight -> {
                historyFragment.updateData(readings.weight)
                historyFragment.title = resources.getString(R.string.textWeight)
            }
            AddReadingActivity.ReadingType.Glucose -> {
                historyFragment.updateData(readings.glucose)
                historyFragment.title = resources.getString(R.string.textGlucose)
            }
            AddReadingActivity.ReadingType.HeartRate -> {
                historyFragment.updateData(readings.pulse)
                historyFragment.title = resources.getString(R.string.textPulse)
            }
            AddReadingActivity.ReadingType.BloodPressure -> {
                historyFragment.updateData(readings.bloodPressure)
                historyFragment.title = resources.getString(R.string.textBloodPressure)
            }
        }
        loadFragment(2)
        canGoBack = true
    }
}