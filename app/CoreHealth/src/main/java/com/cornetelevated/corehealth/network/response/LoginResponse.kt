package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.User

data class LoginResponse(
    var error: String,
    var success: Boolean,
    var response: User?
)