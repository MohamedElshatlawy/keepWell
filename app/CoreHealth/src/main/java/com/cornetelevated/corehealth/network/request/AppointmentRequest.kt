package com.cornetelevated.corehealth.network.request

import com.cornetelevated.corehealth.screens.activities.LoginActivity

class AppointmentRequest(private val paid: Int, private val slot: String, private val slotDate: String, private val typeId: Int, private val physicianId: Int,private val appId: Int?) {

    fun body(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["PaidAmount"] = paid
        map["slot"] = slot
        map["fromTimeString"] = slotDate
        map["AppointmentTypeId"] = typeId
        map["AppointmentStatusId"] = 1
        map["PatientId"] = LoginActivity.getUserID()
        map["PhysicianId"] = physicianId
        return map
    }


    fun editAppBody(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["PaidAmount"] = paid
        map["slot"] = slot
        map["fromTimeString"] = slotDate
        map["AppointmentTypeId"] = typeId
        map["AppointmentStatusId"] = 1
        map["PatientId"] = LoginActivity.getUserID()
        map["PhysicianId"] = physicianId
        map["Id"] = appId!!
        return map
    }
}