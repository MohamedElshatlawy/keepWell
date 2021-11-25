package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.Appointment

data class AppointmentResponse(
    var error: String?,
    var success: Boolean,
    var response: Appointment?
)