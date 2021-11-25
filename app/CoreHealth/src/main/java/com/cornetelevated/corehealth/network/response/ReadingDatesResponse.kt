package com.cornetelevated.corehealth.network.response

import com.cornetelevated.corehealth.network.models.ReadingDates

data class ReadingDatesResponse(var error: String, var success: Boolean, var response: ReadingDates?)