package com.cornetelevated.clinics.screens.home.appointment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.views.AppointmentDetailsView
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.AppointmentResponse
import com.cornetelevated.corehealth.network.response.AppointmentsResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsFragment : Fragment() {


    //    private lateinit var buttonsLayout: LinearLayout
//    private lateinit var nameTextView: TextView
//    private lateinit var specialistTextView: TextView
//    private lateinit var dateTextView: TextView
//    private lateinit var typeTextView: TextView
//    private lateinit var feeTextView: TextView
//    private lateinit var addressTextView: TextView
//    private lateinit var locationMap: GoogleMap
//    private lateinit var locationLayout: LinearLayout
//    private lateinit var cancelButton: Button
//    private lateinit var editButton: Button
    private var appointmentId: Int = -1
    private lateinit var detailsView: AppointmentDetailsView
    private lateinit var detailsProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_appointment_details, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = childFragmentManager.findFragmentById(R.id.locationMap) as SupportMapFragment
//        mapFragment.getMapAsync(this)

//        HomeActivity.showWelcome()
//        HomeActivity.hideStepper()

        detailsView = rootView.findViewById(R.id.detailsView)
        detailsProgressBar = rootView.findViewById(R.id.detailsProgressBar)

//        buttonsLayout = rootView.findViewById(R.id.buttonsLayout)
//        nameTextView = rootView.findViewById(R.id.nameTextView)
//        specialistTextView = rootView.findViewById(R.id.specialistTextView)
//        dateTextView = rootView.findViewById(R.id.dateTextView)
//        typeTextView = rootView.findViewById(R.id.typeTextView)
//        feeTextView = rootView.findViewById(R.id.feeTextView)
//        addressTextView = rootView.findViewById(R.id.addressTextView)
//        locationLayout = rootView.findViewById(R.id.locationLayout)
//        cancelButton = rootView.findViewById(R.id.cancelButton)
//        editButton = rootView.findViewById(R.id.editButton)
//
//        locationLayout.visibility = View.GONE

//        cancelButton.setOnClickListener {
//
//            if (appointmentId != -1) {
//                showCancelDialog(appointmentId)
//            }
//
//        }
//
//        editButton.setOnClickListener {
//            if (appointmentId != -1) {
//                ConfirmFragment.APP_ID = appointmentId
//                openBookingFragment()
//            }
//
//        }

        // get data from intent
        val dataBundle = this.arguments

        if (dataBundle != null) {
            val appModel = dataBundle.getParcelable<Appointment>("AppointmentObject")


            appointmentId = appModel!!.id ?: -1

            Log.d("appointmentId", "onCreateView: appointmentId =$appointmentId")
            if (appointmentId != -1) {
                appointmentDetails(appointmentId)
            }

//            if (appModel!!.physician != null) {
//                nameTextView.text =
//                    appModel!!.physician?.firstName!! + " " + appModel!!.physician?.lastName!!
//
//                specialistTextView.text =
//                    appModel!!.physician?.speciality ?:"speciality"
//
//                addressTextView.text =  appModel!!.physician?.clinicAddress ?:"clinicAddress"
//            }
//
//            feeTextView.text = "" + appModel!!.paidAmount
//
//            when (appModel!!.appointmentTypeId) {
//                1 -> {
//                    typeTextView.text = "Physical"
//                }
//                2 -> {
//                    typeTextView.text = "Online"
//                }
//            }
//
//            when (appModel!!.appointmentStatusId) {
//                1 -> {
//                    buttonsLayout.visibility = View.VISIBLE
//                }
//                2, 3 -> {
//                    buttonsLayout.visibility = View.GONE
//                }
//            }
//
//            // date and time
//            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val appDate: Date = sdf.parse(appModel!!.fromTime)
//            val day = DateFormat.format("dd", appDate) as String
//            val month = DateFormat.format("MMM", appDate) as String
//            val year = DateFormat.format("yyyy", appDate) as String
//            val hours = DateFormat.format("HH", appDate) as String
//            val minutes = DateFormat.format("mm", appDate) as String
//            dateTextView.text = "$year-$month-$day"
//            timeTextView.text = "$hours:$minutes"


        }


        return rootView

    }


    private fun appointmentDetails(appId: Int) {

        // show progressbar
        detailsProgressBar.visibility = View.VISIBLE

        Log.d("onResponseTag", "AppointmentId = $appId")

        val call: Call<AppointmentResponse> = APIClient.appointmentInterface.getAppointmentDetails(LoginActivity.getUserToken(), appId)
        call.enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>?, response: Response<AppointmentResponse>?) {
                // hide progressbar
                detailsProgressBar.visibility = View.GONE
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                it.response?.let {
                                    val typeName = ""

                                    //detailsView.loadAppointmentData("${it.physician?.firstName ?: ""} ${it.physician?.lastName ?: ""}" , it.physician?.speciality?.name ?: "", "${it.paidAmount ?: 0}" , "$year-$month-$day | $hours:$minutes" ,typeName)
                                }
                            } else {
                                Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                            }
                        }

                        /*
                        val stringResponse = response!!.body()!!.string() ?: "stringResponse"
                        Log.d("onResponseTag", "response string = $stringResponse")

                        var jsonRes = JSONObject(stringResponse)

                        var error: String? = jsonRes["error"] as String
                        var success: Boolean? = jsonRes["success"] as Boolean

                        if (success!!) {
                            var response: JSONObject = jsonRes["response"] as JSONObject

                            var paidAmount = response["paidAmount"] as Int?
                            var fromTime = response["fromTime"] as String?
                            var toTime = response["toTime"] as String?
                            var appointmentStatusId = response["appointmentStatusId"] as Int?

                            var appointmentType = response["appointmentType"] as JSONObject?
                            var appointmentTypeId =
                                appointmentType!!["id"] as Int?

                            var appointmentTypeName = appointmentType["appointmentTypeName"] as String?

                            var physician = response["physician"] as JSONObject?
                            var firstName = physician?.get("firstName") as String?
                            // var middleName = physician!!["middleName"] as String?
                            var lastName = physician?.get("lastName") as String?
                            /*
                            var address = physician?.get("address") as String?
                            var clinicName = physician?.get("clinicName") as String?
                            var clinicAddress = physician?.get("clinicAddress") as String?
                            */
                            var speciality = physician?.get("speciality") as JSONObject?
                            var specialityId = speciality?.get("id") as Int?
                            var specialityName = speciality?.get("name") as String?

                            // date and time
                            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val appDate: Date = sdf.parse(fromTime ?: "")
                            val day = DateFormat.format("dd", appDate) as String
                            val month = DateFormat.format("MM", appDate) as String
                            val year = DateFormat.format("yyyy", appDate) as String
                            val hours = DateFormat.format("HH", appDate) as String
                            val minutes = DateFormat.format("mm", appDate) as String


//                        nameTextView.text = "$firstName $lastName"
//                        specialistTextView.text = specialityName!!
//                        dateTextView.text = "$year-$month-$day | $hours:$minutes"
//                        feeTextView.text = "$paidAmount"
//                        typeTextView.text = appointmentTypeName!!
//                        addressTextView.text = address!!


                            detailsView.loadAppointmentData("$firstName $lastName" ,
                                specialityName!!, "$paidAmount" ,
                                "$year-$month-$day | $hours:$minutes" ,appointmentTypeName!!)


                        }
                        */
                    } else {
                        Toast.makeText(context, "not success", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AppointmentResponse>?, t: Throwable?) {
                // hide progressbar
                detailsProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "onFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }


    private fun showCancelDialog(id: Int) {

        val builder = AlertDialog.Builder(context!!)

        // Set the alert dialog title
        builder.setTitle("Are You Sure ?")

        // Display a message on alert dialog
        builder.setMessage("Are you sure, you want to activate cancellation the appointment")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            cancelAppointment(id)
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun cancelAppointment(appId: Int) {
        // show progressbar
        //historyProgressBar.visibility = View.VISIBLE

        Log.d("onResponseTag", "AppointmentId = $appId")

        val map = HashMap<String, Any>()
        map["AppointmentId"] = appId

        val call: Call<ResponseBody> = APIClient.appointmentInterface.cancelAppointment(LoginActivity.getUserToken(), map)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                // hide progressbar
                //historyProgressBar.visibility = View.GONE
                Log.d("onResponseTag", "response  = $response")

                val stringResponse: String? = response!!.body()!!.string()

                Log.d("onResponseTag", "response string = $stringResponse")

                var jsonRes = JSONObject(stringResponse)
                var error: String? = jsonRes["error"] as String
                var success: Boolean? = jsonRes["success"] as Boolean

                if (success!!) {
                    Toast.makeText(context, "Appointment Canceled Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                // hide progressbar
                //historyProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "onFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

}