package com.cornetelevated.corehealth.models.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doctor(
    var firstName: String?,
    var lastName: String?,
    var userId: Int?,
    var user: DoctorUser?,
    var speciality: Speciality?,
    var clinicsInformation:  ArrayList<ClinicInformation?>?,
    var id: Int
) : Parcelable {

    constructor() : this(
        "", "", -1, null, null, null, -1)
}

@Parcelize
data class DoctorUser(
    var profilePicture: String?
): Parcelable

@Parcelize
data class ClinicInformation(
    var clinicName: String?,
    var physicianFeesAmount: Int?,
    var onlineFeesAmount: Int?,
    var mobile: String?,
    var email: String?,
    var address: String?,
    var latitude: String?,
    var longitude: String?,
    var physicianId: Int?,
    var id: Int?

) : Parcelable
