package com.cornetelevated.corehealth.models.fit

import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.HealthFields

import com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_GLUCOSE_LEVEL
import com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC
import com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class FitRequest: Runnable {
    private var responseResult: FitResponseResult? = null
    private var fitTask: Task<DataReadResponse>? = null

    fun setTask(task: Task<DataReadResponse>?, result: FitResponseResult?) {
        fitTask = task
        responseResult = result
    }

    override fun run() {
        try {
            val readDataResult: DataReadResponse? = fitTask?.let { Tasks.await(it) }
            readDataResult?.let { analyzeResults(it) }
        } catch (e: ExecutionException) {
            responseResult?.onFailure(e.message)
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
            responseResult?.onFailure(ex.message)
        }
    }

    private fun analyzeResults(response: DataReadResponse) {
        val readings = FitReadings()
        val dataSets: List<DataSet> = response.getDataSets()
        for (i in dataSets.indices) {
            val dataset: DataSet = dataSets[i]
            val points: List<DataPoint> = dataset.getDataPoints()
            val typeString: String = dataset.getDataType().getName()
            if (points.size > 0) {
                when (typeString) {
                    "com.google.blood_pressure" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val diaString: String =
                            dp.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC).toString()
                        val sysString: String =
                            dp.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC).toString()
                        if (diaString != "unset" && sysString != "unset") {
                            reading.maxValue = sysString.toFloat().toDouble()
                            reading.minValue = diaString.toFloat().toDouble()
                            reading.date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            readings.bloodPressure.add(reading)
                        }
                    }
                    "com.google.blood_glucose" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val valueString: String =
                            dp.getValue(FIELD_BLOOD_GLUCOSE_LEVEL).toString()
                        if (valueString != "unset") {
                            reading.value = valueString.toFloat().toDouble()
                            reading.date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            reading.unitId = 4
                            readings.glucose.add(reading)
                        }
                    }
                    "com.google.weight" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val valueString: String = dp.getValue(Field.FIELD_WEIGHT).toString()
                        if (valueString != "unset") {
                            val date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            reading.value = valueString.toFloat().toDouble()
                            reading.date = date
                            readings.weight.add(reading)
                        }
                    }
                    "com.google.oxygen_saturation" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val valueString: String =
                            dp.getValue(HealthFields.FIELD_OXYGEN_SATURATION).toString()
                        if (valueString != "unset") {
                            reading.value = valueString.toFloat().toDouble()
                            reading.date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            readings.oxygen.add(reading)
                        }
                    }
                    "com.google.heart_rate.bpm" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val valueString: String = dp.getValue(Field.FIELD_BPM).toString()
                        if (valueString != "unset") {
                            reading.value = valueString.toFloat().toDouble()
                            reading.date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            readings.pulse.add(reading)
                        }
                    }
                    "com.google.step_count.delta" -> for (dp in dataset.getDataPoints()) {
                        val reading = DataReading()
                        val valueString: String = dp.getValue(Field.FIELD_STEPS).toString()
                        if (valueString != "unset") {
                            reading.value = valueString.toFloat().toDouble()
                            reading.date =
                                Date(dp.getStartTime(TimeUnit.MILLISECONDS))
                            readings.steps.add(reading)
                        }
                    }
                }
            }
        }
        responseResult?.onSuccess(readings)
    }

}