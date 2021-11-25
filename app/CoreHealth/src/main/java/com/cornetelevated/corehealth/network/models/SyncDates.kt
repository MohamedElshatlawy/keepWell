package com.cornetelevated.corehealth.network.models

data class SyncDates(
    var stepsData: String?,
    var glucosesData: String?,
    var plusesData: String?,
    var oximetersData: String?,
    var bloodpressuresData: String?,
    var scalesData: String?
)