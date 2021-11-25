package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.ProfileResponse
import com.cornetelevated.corehealth.network.response.StringResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface ProfileAPIInterface {

    @Headers("Content-Type: application/json")
    @GET("Profile/GetProfileData")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @GET("Account/GetImage/{id}")
    fun getImageData(@Path("id") id: String): Call<StringResponse>


    //@Headers("Content-Type: multipart/form-data; boundary=<calculated when request is sent>")
    @POST("Account/UpdateProfileInfo")
    @Multipart
    fun updateProfileData(
        @Header("Authorization") token: String,
        @Part profileImage: MultipartBody.Part,
        @Part("UserName") userName: RequestBody,
        @Part("Email") email: RequestBody
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("Account/AddProfilePicture")
    @Multipart
    fun addProfilePicture(
        @Header("Authorization") token: String,
        @Part("Id") userId: Int,
        @Part profileImage: MultipartBody.Part
    ): Call<ResponseBody>

}