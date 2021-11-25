package com.cornetelevated.corehealth.models.fit

data class SyncTimeStamp(
    var steps: Long = 0,
    var glucose: Long = 0,
    var oxygen: Long = 0,
    var bloodPressure: Long = 0,
    var scale: Long = 0,
    var pulse: Long = 0
)