package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.Speciality

data class SpecialityResponse(
    var error: String?,
    var success: Boolean,
    var response: ArrayList<Speciality>?
)