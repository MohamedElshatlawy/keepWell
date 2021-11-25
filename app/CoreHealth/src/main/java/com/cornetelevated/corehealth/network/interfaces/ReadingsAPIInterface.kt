package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

@JvmSuppressWildcards
interface ReadingsAPIInterface {

/*
    @POST("DevicesReadings/GetLastPatientReading")
    fun setFirebaseToken(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<StringResponse>
*/

    @GET("DevicesReadings/GetMaxMinDates")
    fun getAvailableReadingDates(
        @Header("Authorization") token: String
    ): Call<ReadingDatesResponse>

    @GET("DevicesReadings/GetLastPatientReading")
    fun getLastReadingDates(
        @Header("Authorization") token: String
    ): Call<SyncDatesResponse>

    @POST("DevicesReadings/Scale/ScaleReadings")
    fun getScaleReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ScaleResponse>

    @POST("DevicesReadings/Steps/StepsReadings")
    fun getStepsReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<StepsResponse>

    @POST("DevicesReadings/Glucose/GlucoseReadings")
    fun getGlucoseReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ReadingsResponse>

    @POST("DevicesReadings/Pluse/PluseReadings")
    fun getPulseReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<PulseResponse>

    @POST("DevicesReadings/Oximete/OximeteReadings")
    fun getOxygenReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<OxygenResponse>

    @POST("DevicesReadings/BloodPressure/BloodPressureReadings")
    fun getBloodPressureReadings(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<BloodPressureResponse>


    @POST("DevicesReadings/Scale/SaveScaleReadings")
    fun sendScaleReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @POST("DevicesReadings/Steps/SaveStepsReadings")
    fun sendStepsReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @POST("DevicesReadings/Glucose/SaveGlucoseReadings")
    fun sendGlucoseReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @POST("DevicesReadings/Pluse/SavePluseReadings")
    fun sendPulseReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @POST("DevicesReadings/Oximete/SaveOximeterReadings")
    fun sendOxygenReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>

    @POST("DevicesReadings/BloodPressure/SaveBloodPressureReadings")
    fun sendBloodPressureReading(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ResponseBody>



}