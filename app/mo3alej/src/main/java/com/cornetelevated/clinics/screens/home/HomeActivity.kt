package com.cornetelevated.clinics.screens.home

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.profile.ProfileFragment
import com.cornetelevated.clinics.screens.home.search.SearchFragment
import com.cornetelevated.corehealth.screens.activities.HomeActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment

class HomeActivity: HomeActivity() {

    var settingsFragment = SettingsFragment()
    var profileFragment = ProfileFragment()
    var searchFragment = SearchFragment()

    lateinit var searchButton: ImageButton

    private var backStepsAvailable: Int = 0

    override fun onBackPressed() {
        if (backStepsAvailable > 0) {
            popFragments(1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigationView = findViewById(R.id.navView)
        welcomeLabel = findViewById(R.id.welcomeLabel)
        titleLabel = findViewById(R.id.titleLabel)
        searchButton = findViewById(R.id.searchButton)
        updateWelcomeText()

        settingsFragment.title = "Settings"
        profileFragment.title = "Profile"
        fragments = arrayListOf(settingsFragment, searchFragment, profileFragment)

        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigationSettings -> {
                    showTopText()
                    hideKeyboard()
                    loadFragment(0)
                }
                R.id.navigationSearch -> {
                    searchTapped()
                }
                R.id.navigationProfile -> {
                    showTopText()
                    hideKeyboard()
                    loadFragment(2)
                }
            }
            true
        }

        searchButton.setOnClickListener { searchTapped() }

        loadFragment(0)
    }

    private fun searchTapped() {
        hideTopText()
        loadFragment(1)
    }

    private fun hideTopText() {
        welcomeLabel.visibility = View.GONE
        titleLabel.visibility = View.GONE
    }

    private fun showTopText() {
        welcomeLabel.visibility = View.VISIBLE
        titleLabel.visibility = View.VISIBLE
    }

    fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            com.cornetelevated.corehealth.R.anim.fade_in,
            com.cornetelevated.corehealth.R.anim.fade_out,
            com.cornetelevated.corehealth.R.anim.fade_in,
            com.cornetelevated.corehealth.R.anim.fade_out
        )
        transaction.add(com.cornetelevated.corehealth.R.id.fragmentContainer, fragment)
        transaction.commit()
        backStepsAvailable += 1
    }

    fun popFragments(count: Int) {
        for (i in 1..count) {
            supportFragmentManager.popBackStack()
        }
        backStepsAvailable -= count
    }

}