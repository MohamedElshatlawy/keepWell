package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.models.common.Profile

data class ProfileResponse(
    var error: String?,
    var success: String?,
    var response: Profile?
)