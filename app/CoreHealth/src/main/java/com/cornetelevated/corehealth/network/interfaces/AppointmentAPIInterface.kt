package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.AppointmentResponse
import com.cornetelevated.corehealth.network.response.AppointmentsResponse
import com.cornetelevated.corehealth.network.response.StringArrayResponse
import com.cornetelevated.corehealth.network.response.WorkingDaysResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface AppointmentAPIInterface {

    @POST("Appointment/{link}")
    fun getSlots(
        @Header("Authorization") token: String,
        @Path("link") link: String,
        @Body body: Map<String, Any>
    ): Call<StringArrayResponse>

    @GET("Appointment/{link}/{id}")
    fun getSlotsDate(@Header("Authorization") token: String,
                     @Path("link") link: String,
                     @Path("id") id: Int): Call<WorkingDaysResponse>

    @POST("Appointment/AddAppointment")
    fun addAppointment(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("Appointment/GetHistory")
    fun getAppHistory(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<AppointmentsResponse>


    @Headers("Content-Type: application/json")
    @POST("Appointment/ConfirmCancel")
    fun cancelAppointment(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>


    @Headers("Content-Type: application/json")
    @POST("Appointment/EditAppointment")
    fun editAppointment(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("Appointment/GetRecent")
    fun getSchedule(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<AppointmentsResponse>

    @Headers("Content-Type: application/json")
    @GET("/Appointment/GetAppointmentDetails/{id}")
    fun getAppointmentDetails(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<AppointmentResponse>

}