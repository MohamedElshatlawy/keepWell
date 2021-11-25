package com.cornetelevated.corehealth.models.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Appointment(
    var paidAmount: Int?,
    var fromTime: String?,
    var toTime: String?,
    var appointmentTypeId: Int?,
    var appointmentStatusId: Int?,
    var physician: Physician?,
    var id: Int?
) : Parcelable {

    constructor() : this(
        -1, "", "", -1,
        -1, null, -1
    )
}


@Parcelize
data class Physician(
    var firstName: String?,
    var middleName: String?,
    var lastName: String?,
    var address: String?,
    var clinicName: String?,
    var clinicAddress: String?,
    var speciality :Speciality?
) : Parcelable


@Parcelize
data class Speciality(
    var id: Int?,
    var name: String?
) : Parcelable