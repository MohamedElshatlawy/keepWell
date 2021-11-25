package com.cornetelevated.corehealth.models.fit

import java.util.*

class DataReading {
    var date: Date = Date()
    var value: Double = 0.0
    var maxValue: Double = 0.0
    var minValue: Double = 0.0
    var unitId: Int = 1
    var isManual: Boolean = false

    fun pulseBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["PluseValue"] = value
        map["UnitId"] = unitId
        return map
    }

    fun requestBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["Reading"] = value
        map["UnitId"] = unitId
        return map
    }

    fun stepsBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["StepsValue"] = value
        map["UnitId"] = unitId
        return map
    }

    fun oxygenBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["OxygenLevel"] = value
        map["OxygenLevelUnitId"] = unitId
        return map
    }

    fun scaleBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["Weight"] = value
        map["WeightUnitId"] = unitId
        return map
    }

    fun bloodPressureBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["ReadingSourceId"] = if (isManual) 4 else 2
        map["CreationDate"] = FitManager.dateTimeFormat.format(date)
        map["SYS"] = maxValue
        map["SYSUnitId"] = unitId
        map["DIA"] = minValue
        map["DIAUnitId"] = unitId
        return map
    }

}