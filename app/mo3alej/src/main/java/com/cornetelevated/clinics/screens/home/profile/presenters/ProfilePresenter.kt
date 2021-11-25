package com.cornetelevated.clinics.screens.home.profile.presenters

import android.util.Log
import android.view.View
import android.widget.Toast
import com.cornetelevated.clinics.screens.home.profile.ProfileFragment
import com.cornetelevated.clinics.screens.home.profile.interactors.ProfileInteractor
import com.cornetelevated.corehealth.models.common.Profile

class ProfilePresenter(var profileFragment: ProfileFragment) {
    var interactor: ProfileInteractor = ProfileInteractor(this)

    fun showProgress() {
        profileFragment.profileProgressBar.visibility = View.VISIBLE
        profileFragment.isDataAvailable = false
    }

    fun hideProgress() {
        profileFragment.profileProgressBar.visibility = View.GONE
    }

    fun updateUI() {
        profileFragment.isDataAvailable = true
        profileFragment.updateUI()
    }

    fun showError(message: String) {
        Log.d("onResponseTag", "ErrorOnFailure: $message")
        Toast.makeText(profileFragment.context, message, Toast.LENGTH_SHORT).show()
    }

    fun getProfile(): Profile? {
        return interactor.profile
    }
}