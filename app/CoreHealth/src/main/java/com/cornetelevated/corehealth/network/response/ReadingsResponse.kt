package com.cornetelevated.corehealth.network.response

data class Reading(val creationDate: String, val reading: Double)

data class Scale(val creationDate: String, val weight: Double)

data class Steps(val creationDate: String, val stepsValue: Double)

data class Oxygen(val creationDate: String, val oxygenLevel: Double)

data class Pulse(val creationDate: String, val pluseValue: Double)

data class BloodPressure(val creationDate: String, val sys: Double, val dia: Double)

data class ReadingsResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Reading>
)

data class ScaleResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Scale>
)

data class StepsResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Steps>
)

data class OxygenResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Oxygen>
)

data class PulseResponse(
    var error: String,
    var success: Boolean,
    var response: Array<Pulse>
)

data class BloodPressureResponse(
    var error: String,
    var success: Boolean,
    var response: Array<BloodPressure>
)

