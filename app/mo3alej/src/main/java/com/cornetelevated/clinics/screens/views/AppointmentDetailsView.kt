package com.cornetelevated.clinics.screens.views

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.models.fit.FitManager
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var locationLayout: LinearLayout
    private lateinit var addressTextView: TextView

    //private lateinit var locationMap: GoogleMap
    private lateinit var nameTextView: TextView
    private lateinit var specialistTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var feeTextView: TextView

    init {

        LinearLayout.inflate(context, R.layout.view_appointment_details, this)

        locationLayout = findViewById(R.id.locationLayout)
        addressTextView = findViewById(R.id.addressTextView)
        //locationMap = findViewById(R.id.locationMap)
        nameTextView = findViewById(R.id.nameTextView)
        specialistTextView = findViewById(R.id.specialistTextView)
        dateTextView = findViewById(R.id.dateTextView)
        typeTextView = findViewById(R.id.typeTextView)
        feeTextView = findViewById(R.id.feeTextView)

        locationLayout.visibility = View.GONE

    }

    fun loadAppointment(appointment: Appointment) {
        nameTextView.text = appointment.physician?.firstName ?: "" + " " + appointment.physician?.lastName ?: ""
        specialistTextView.text = appointment.physician?.speciality?.name ?: ""

        feeTextView.text = "${appointment.paidAmount ?: 0}"

        val appDate: Date = FitManager.dateTimeFormat.parse(appointment.fromTime ?: "") ?: Date()
        val day = DateFormat.format("dd", appDate) as String
        val month = DateFormat.format("MM", appDate) as String
        val year = DateFormat.format("yyyy", appDate) as String
        val hours = DateFormat.format("HH", appDate) as String
        val minutes = DateFormat.format("mm", appDate) as String
        dateTextView.text = "$year/$month/$day | $hours : $minutes"

        typeTextView.text = when (appointment.appointmentTypeId ?: 0) {
            1 -> "Physical Session"
            2 -> "Online Session"
            else -> "Session"
        }
    }

    fun loadAppointmentData(
        physicianName: String, physicianSpeciality: String, paidAmount: String,
        appointmentData: String, appointmentType: String
    ) {
        nameTextView.text = physicianName
        specialistTextView.text = physicianSpeciality
        feeTextView.text = paidAmount
        dateTextView.text = appointmentData
        typeTextView.text = appointmentType
    }
}