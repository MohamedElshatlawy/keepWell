package com.cornetelevated.corehealth.network.interfaces

import com.cornetelevated.corehealth.network.response.PrescriptionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

@JvmSuppressWildcards
interface PrescriptionAPIInterface {

    @GET("Prescription/ListPrescriptionsPDF/patient/{id}")
    fun getPrescriptions(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<PrescriptionResponse>

}