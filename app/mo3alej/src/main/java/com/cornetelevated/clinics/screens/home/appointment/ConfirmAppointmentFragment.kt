package com.cornetelevated.clinics.screens.home.appointment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.search.DoctorsFragment
import com.cornetelevated.clinics.screens.views.AppointmentDetailsView
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.AppointmentRequest
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmAppointmentFragment: Fragment() {


    private lateinit var confirmProgressBar: ProgressBar
    private var detailsView: AppointmentDetailsView? = null
    private var physicianName = ""
    private lateinit var acceptCheckBox: CheckBox
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_confirm_appointment, container, false)

        confirmProgressBar = view.findViewById(R.id.confirmProgressBar)
        detailsView = view.findViewById(R.id.detailsView)
        acceptCheckBox = view.findViewById(R.id.acceptCheckBox)

        confirmButton = view.findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {

            if (!acceptCheckBox.isChecked) {
                Toast.makeText(context, "Please accept Terms and Conditions", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            addAppointment()
        }

        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            showCancelDialog()
        }

        initAppointmentData()


        return view
    }

    override fun onStop() {
        detailsView = null
        super.onStop()
    }

    private fun initAppointmentData() {

        if (APP_ID != null) {
            detailsView?.loadAppointmentData(
                doctorName ?: "",
                doctorSpeciality ?: "",
                "${paidAmount ?: 0}",
                AppointmentDateTimeFragment.getSelectedDateAndTimeConfirm(),
                AppointmentDateTimeFragment.getAppointmentType()
            )
        } else {
            detailsView?.loadAppointmentData(
                DoctorsFragment.doctorName,
                DoctorsFragment.doctorSpeciality,
                "${DoctorsFragment.doctorAmount}",
                AppointmentDateTimeFragment.getSelectedDateAndTimeConfirm(),
                AppointmentDateTimeFragment.getAppointmentType()
            )
        }

    }


    private fun showConfirmDialog() {
/*
        // custom dialog
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.setCancelable(false)
        var layout = dialog.findViewById<LinearLayout>(R.id.dialogLinearLayout)
        layout.setOnClickListener {

            dialog.dismiss()
            if (APP_ID != null) {
                //back to Appointment fragment from edit
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
            } else {
                //back to Booking fragment from booking
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
            }
        }
        dialog.show()
*/
    }

    private fun showCancelDialog() {

        val builder = AlertDialog.Builder(context!!)

        // Set the alert dialog title
        builder.setTitle("Are You Sure ?")

        // Display a message on alert dialog
        builder.setMessage("Are you sure, you want to activate cancellation the appointment with $physicianName")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()

            if (APP_ID != null) {
                //back to Appointment fragment
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
            } else {
                //back to Booking fragment
                fragmentManager!!.popBackStack()
                fragmentManager!!.popBackStack()
            }
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    // createAppointment method
    private fun addAppointment() {
        Log.d("onResponseTag", " getSelectedDate =" + AppointmentDateTimeFragment.getSelectedDateForConfirm())
        Log.d("onResponseTag", "AppointmentTypeId =" + AppointmentDateTimeFragment.getAppointmentTypeId())
        Log.d("onResponseTag", "PatientId=" + LoginActivity.getUserID())
        Log.d("onResponseTag", "APP ID=$APP_ID")

        // show progressbar
        confirmProgressBar.visibility = View.VISIBLE

        val call: Call<ResponseBody> = if (APP_ID == null) {

            val model = AppointmentRequest(
                DoctorsFragment.doctorAmount, AppointmentDateTimeAdapter.selectedSlot ?: "",
                AppointmentDateTimeFragment.getSelectedDateForConfirm(),
                AppointmentDateTimeFragment.getAppointmentTypeId(),
                DoctorsFragment.doctorId, null
            )

            APIClient.appointmentInterface.addAppointment(LoginActivity.getUserToken(), model.body())
        } else {
            val model = AppointmentRequest(
                paidAmount ?: 0, AppointmentDateTimeAdapter.selectedSlot ?: "",
                AppointmentDateTimeFragment.getSelectedDateForConfirm(),
                AppointmentDateTimeFragment.getAppointmentTypeId(),
                doctorId ?: 0, APP_ID
            )

            APIClient.appointmentInterface.editAppointment(LoginActivity.getUserToken(), model.editAppBody())
        }
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                // hide progressbar
                confirmProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "onResponse: $response")

                if (response!!.isSuccessful) {

                    val jsonObject = JSONObject(response.body()!!.string())

                    val error: String = jsonObject["error"] as String
                    val success: Boolean = jsonObject["success"] as Boolean

                    if (success) {
                        showConfirmDialog()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }


                } else {
                    Toast.makeText(
                        context,
                        "something went wrong , please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                // hide progressbar
                confirmProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "ErrorOnFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    companion object {
        var APP_ID: Int? = null
        var paidAmount: Int? = null
        var doctorId: Int? = null
        var doctorName: String? = null
        var doctorSpeciality: String? = null


    }

}