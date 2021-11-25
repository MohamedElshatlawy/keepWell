package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.Appointment

data class AppointmentsResponse(
    var error: String?,
    var success: Boolean,
    var total: Int?,
    var response: ArrayList<Appointment>?
)