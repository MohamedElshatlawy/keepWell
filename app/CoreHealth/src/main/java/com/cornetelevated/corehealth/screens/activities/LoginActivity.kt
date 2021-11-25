package com.cornetelevated.corehealth.screens.activities

import android.util.Log
import android.widget.Toast
import com.cornetelevated.corehealth.models.common.Patient
import com.cornetelevated.corehealth.models.common.User
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.PatientResponse
import de.adorsys.android.securestoragelibrary.SecurePreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class LoginActivity : Activity() {

    companion object {
        var user: User? = null
        var patient: Patient? = null

        fun getUserID(): Int {
            return user?.userId!!
        }

        fun getUserToken(): String {
            return "Bearer ${user?.token!!}"
        }

        fun userFirstName(): String {
            return patient?.firstName.toString()
        }
    }

    override fun onBackPressed() { }

    override fun onResume() {
        super.onResume()
        checkCredentials()
    }

    override fun onRestart() {
        super.onRestart()
        checkCredentials()
    }

    open fun checkCredentials() { }

    fun storeCredentials(username: String, password: String) {
        SecurePreferences.setValue("KW.username", username)
        SecurePreferences.setValue("KW.password", password)
        SecurePreferences.setValue("KW.isLoggedIn", true)
    }

    open fun handleUser() { }

    fun sendFCMToken() {
        SecurePreferences.getStringValue("KW.FCMtoken", "")?.let { fcmToken ->
            Log.i("FCM Token", "FCM Token: $fcmToken")
            val map = HashMap<String, Any>()
            map["DeviceToken"] = fcmToken
            val tokenCall: Call<PatientResponse> = APIClient.authInterface.setFirebaseToken(getUserToken(), map)
            tokenCall.enqueue(object : Callback<PatientResponse> {
                override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                    patient = response.body()?.response
                }
            })
        }
    }

}