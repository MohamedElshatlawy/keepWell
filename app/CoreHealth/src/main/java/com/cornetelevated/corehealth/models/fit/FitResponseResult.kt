package com.cornetelevated.corehealth.models.fit

import com.cornetelevated.corehealth.models.fit.FitReadings

interface FitResponseResult {
    fun onSuccess(readings: FitReadings?)
    fun onFailure(error: String?)
}