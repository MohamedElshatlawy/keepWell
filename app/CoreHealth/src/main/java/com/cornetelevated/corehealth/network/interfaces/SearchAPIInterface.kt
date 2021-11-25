package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.DoctorResponse
import com.cornetelevated.corehealth.network.response.SpecialityResponse
import com.cornetelevated.corehealth.network.response.StringArrayResponse
import com.cornetelevated.corehealth.network.response.WorkingDaysResponse
import retrofit2.Call
import retrofit2.http.*


@JvmSuppressWildcards
interface SearchAPIInterface {

    @Headers("Content-Type: application/json")
    @GET("Physician/GetAllSpecialists")
    fun getAllSpecialists(): Call<SpecialityResponse>

    @Headers("Content-Type: application/json")
    @POST("Physician/SearchPhysicians")
    fun  searchPhysicians(
        @Body body: Map<String, Any>
    ): Call<DoctorResponse>
}