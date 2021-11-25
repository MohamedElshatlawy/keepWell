package com.cornetelevated.corehealth.network.response

data class StringArrayResponse(
    var error: String,
    var success: Boolean,
    var response: Array<String>
)