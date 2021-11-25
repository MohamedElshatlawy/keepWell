package com.cornetelevated.corehealth.network

import com.cornetelevated.corehealth.BuildConfig
import com.cornetelevated.corehealth.network.interfaces.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class APIClient {
    companion object {

        private var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authInterface = retrofit.create(AuthenticationAPIInterface::class.java)
        val profileInterface = retrofit.create(ProfileAPIInterface::class.java)
        val readingsInterface = retrofit.create(ReadingsAPIInterface::class.java)
        val appointmentInterface = retrofit.create(AppointmentAPIInterface::class.java)
        val searchInterface = retrofit.create(SearchAPIInterface::class.java)
        val prescriptionInterface = retrofit.create(PrescriptionAPIInterface::class.java)

    }
}