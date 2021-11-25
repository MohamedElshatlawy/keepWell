package com.cornetelevated.corehealth.network.response

import android.os.Parcelable
import com.cornetelevated.corehealth.models.common.Doctor
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorResponse(
    var error: String,
    var success: Boolean,
    var total: Int,
    var response: ArrayList<Doctor>
) : Parcelable