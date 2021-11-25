package com.cornetelevated.corehealth.models.fit

import com.cornetelevated.corehealth.models.fit.DataReading
import java.util.*

class FitReadings(
    var steps: ArrayList<DataReading> = ArrayList<DataReading>(),
    var weight: ArrayList<DataReading> = ArrayList<DataReading>(),
    var glucose: ArrayList<DataReading> = ArrayList<DataReading>(),
    var pulse: ArrayList<DataReading> = ArrayList<DataReading>(),
    var oxygen: ArrayList<DataReading> = ArrayList<DataReading>(),
    var bloodPressure: ArrayList<DataReading> = ArrayList<DataReading>()
)