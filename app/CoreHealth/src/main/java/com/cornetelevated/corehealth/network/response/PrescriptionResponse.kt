package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.Prescription

data class PrescriptionResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Prescription>
)