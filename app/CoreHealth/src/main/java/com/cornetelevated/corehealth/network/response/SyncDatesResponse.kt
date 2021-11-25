package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.network.models.SyncDates

data class SyncDatesResponse(var error: String, var success: Boolean, var response: SyncDates?)