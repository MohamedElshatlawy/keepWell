package com.cornetelevated.clinics.screens.home.profile.interactors

import android.util.Log
import com.cornetelevated.clinics.screens.home.profile.presenters.ProfilePresenter
import com.cornetelevated.corehealth.models.common.Profile
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.ProfileResponse
import com.cornetelevated.corehealth.network.response.StringResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileInteractor(var presenter: ProfilePresenter) {

    var profile: Profile? = null

    fun fetchProfileData() {
        presenter.showProgress()
        val call: Call<ProfileResponse> = APIClient.profileInterface.getProfile(LoginActivity.getUserToken())
        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>?, response: Response<ProfileResponse>?
            ) {
                Log.d("onResponseTag", "onResponse= $response")
                Log.d("onResponseTag", "onResponse success =" + response?.body()?.success)
                profile = response?.body()?.response
                fetchImageData()
                presenter.updateUI()
            }

            override fun onFailure(call: Call<ProfileResponse>?, t: Throwable?) {
                presenter.hideProgress()
                presenter.showError(t?.message ?: "something went wrong , please try again")
            }
        })

    }

    private fun fetchImageData() {
        if (profile?.profilePicture != null) {
            val call: Call<StringResponse> = APIClient.profileInterface.getImageData(profile?.profilePicture!!)
            call.enqueue(object : Callback<StringResponse> {
                override fun onResponse(
                    call: Call<StringResponse>?, response: Response<StringResponse>?
                ) {
                    presenter.hideProgress()
                    profile?.profilePictureData = response!!.body()?.response
                    presenter.updateUI()
                }

                override fun onFailure(call: Call<StringResponse>?, t: Throwable?) {
                    presenter.hideProgress()
                    presenter.showError(t?.message ?: "something went wrong , please try again")
                }
            })
        } else {
            presenter.hideProgress()
        }
    }

}