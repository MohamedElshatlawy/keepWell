package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@JvmSuppressWildcards
interface AuthenticationAPIInterface {

    /**
     * Shared
     */

    @POST("Account/Login")
    fun login(@Body body: Map<String, Any>): Call<LoginResponse>

    @POST("DevicesReadings/Patient/AssignDeviceToken")
    fun setFirebaseToken(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<PatientResponse>

    /**
     * 3yadat
     */

    @POST("Account/PatientSignUp")
    fun register(@Body body: Map<String, Any>): Call<JSONResponse>

    @POST("Account/ActivateUser")
    fun activate(@Body body: Map<String, Any>): Call<JSONResponse>

    @POST("Account/Reactivate")
    fun resendCode(@Body body: Map<String, Any>): Call<IntResponse>

    @POST("Account/SendVerification")
    fun forgotPassword(@Body body: Map<String, Any>): Call<IntResponse>

    @POST("Account/SubmitVerification")
    fun verify(@Body body: Map<String, Any>): Call<IntResponse>

    @POST("Account/ResetPassword")
    fun resetPassword(@Body body: Map<String, Any>): Call<ResponseBody>

    @POST("Account/ChangePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<StringResponse>

    /**
     * Keepwell
     */

    @POST("Account/PasswordResetAtFirstLogin")
    fun setPassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

}