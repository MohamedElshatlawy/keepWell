package com.cornetelevated.clinics.screens.home.appointment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.HomeActivity
import com.cornetelevated.corehealth.models.common.Slot
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.StringArrayResponse
import com.cornetelevated.corehealth.network.response.WorkingDaysResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDateTimeFragment: Fragment() {

    companion object {

        var doctorId: Int? = null
        var appId: Int? = null


        private var bookingDate: Date? = null
        private var appointmentTypeId: Int = 1

        fun getSelectedDateForConfirm(): String {
            return FitManager.dateTimeFormatExpanded.format(bookingDate ?: Date())
        }

        fun getSelectedDateAndTimeConfirm(): String {
            return (FitManager.dateFormat.format(
                bookingDate ?: Date()
            ) + " | " + (AppointmentDateTimeAdapter.selectedSlot?.split("-")?.get(0) ?: ""))
        }

        fun getSelectedDateTimeForSlots(): String {
            return FitManager.dateTimeFormat.format(bookingDate ?: Date())
        }

        fun getAppointmentTypeId(): Int {
            return appointmentTypeId
        }

        fun getAppointmentSlot(): String {
            return AppointmentDateTimeAdapter.selectedSlot ?: ""
        }

        fun getAppointmentType(): String {
            return when (appointmentTypeId) {
                1 -> "Physical Session"
                2 -> "Online Session"
                else -> ""
            }
        }

    }

    private var bookingTypeRadioGroup: RadioGroup? = null
    private lateinit var mPicker: DayScrollDatePicker
    private lateinit var availableShotsRecyclerView: RecyclerView
    private lateinit var adapter: AppointmentDateTimeAdapter
    private lateinit var bookingButton: Button
    private lateinit var slotsProgressBar: ProgressBar
    private lateinit var listData: ArrayList<Slot>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_date_time, container, false)

        bookingTypeRadioGroup = null
        AppointmentDateTimeAdapter.selectedSlot = null
        bookingDate = null
        appointmentTypeId = 0

        slotsProgressBar = view.findViewById(R.id.slotsProgressBar)
        bookingTypeRadioGroup = view.findViewById(R.id.bookingTypeRadioGroup)
        bookingButton = view.findViewById(R.id.bookingButton)
        availableShotsRecyclerView = view.findViewById(R.id.availableShotsRecyclerView)

        adapter = AppointmentDateTimeAdapter()
        availableShotsRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        availableShotsRecyclerView.adapter = adapter

        mPicker = view.findViewById(R.id.day_date_picker)
        getDates()

        mPicker.getSelectedDate { date ->
            if (date != null) {
                bookingDate = date
                getSlots(getSelectedDateTimeForSlots())
            }
        }

        bookingButton.setOnClickListener {

            // get selected radio button from radioGroup
            val selectedId: Int = bookingTypeRadioGroup!!.checkedRadioButtonId

            // find the radiobutton by returned id
            val radioSelectedButton = view.findViewById<RadioButton>(selectedId)

            if (radioSelectedButton == null) {
                Toast.makeText(activity, "Booking type is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            when (selectedId) {
                R.id.radioPhysical -> appointmentTypeId = 1
                R.id.radioOnline -> appointmentTypeId = 2
            }

            if (bookingDate == null) {
                Toast.makeText(activity, "Booking date is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listData.isEmpty()) {
                Toast.makeText(activity, "This Date is not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (AppointmentDateTimeAdapter.selectedSlot == null) {
                Toast.makeText(activity, "Booking time is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadPaymentFragment()

        }


        return view
    }


    // go to payment Fragment
    private fun loadPaymentFragment() {
        (activity as? HomeActivity)?.showFragment(ConfirmAppointmentFragment())
    }


    // getSlots method
    private fun getSlots(date: String) {

        // show progressbar
        slotsProgressBar.visibility = View.VISIBLE

        var map = HashMap<String, Any>()


        val call: Call<StringArrayResponse> = if (doctorId != null) {
            map = HashMap()
            map["FromTimeString"] = date
            map["AccountId"] = doctorId!!

            APIClient.appointmentInterface.getSlots(LoginActivity.getUserToken(), "GetSlots", map)
        } else {
            map = HashMap()
            map["FromTimeString"] = date
            map["id"] = appId ?: 0
            APIClient.appointmentInterface.getSlots(LoginActivity.getUserToken(), "GetSlots2", map)
        }

        call.enqueue(object : Callback<StringArrayResponse> {
            override fun onFailure(call: Call<StringArrayResponse>, t: Throwable) {
                // hide progressbar
                slotsProgressBar.visibility = View.GONE
                Log.d("onResponseTag", "ErrorOnFailure: $t")
                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<StringArrayResponse>,
                response: Response<StringArrayResponse>
            ) {
                // hide progressbar
                slotsProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        listData = ArrayList()
                        body.response.forEach { item ->
                            val bookingModel = Slot(item, false)
                            //adding data to adapter list
                            listData.add(bookingModel)
                        }
                        adapter.setData(listData)
                    } else {
                        Toast.makeText(context, body?.error ?: "", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "something went wrong , please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })

    }


    // getDates method
    private fun getDates() {
        // show progressbar
        slotsProgressBar.visibility = View.VISIBLE

        val call: Call<WorkingDaysResponse> = if (doctorId != null) {
            APIClient.appointmentInterface.getSlotsDate(LoginActivity.getUserToken(), "GetMaxMinSlots", doctorId ?: 0)
        } else {
            APIClient.appointmentInterface.getSlotsDate(LoginActivity.getUserToken(), "GetMaxMinSlots2", appId ?: 0)
        }

        call.enqueue(object : Callback<WorkingDaysResponse> {

            override fun onResponse(
                call: Call<WorkingDaysResponse>?,
                response: Response<WorkingDaysResponse>?
            ) {

                // hide progressbar
                slotsProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "onResponse: $response")
                //Log.d("onResponseTag", "onResponse: success =" + response!!.body()!!.success)
                response?.body()?.let {
                    if (it.success) {
                        val slotsDate = it.response
                        // set start date
                        val startingDate = slotsDate.slotStart

                        val startDate = FitManager.dateFormat.parse(startingDate) ?: Date()
                        val startDay = DateFormat.format("dd", startDate) as String // 18
                        val startMonthNumber = DateFormat.format("MM", startDate) as String // 05
                        val startYear = DateFormat.format("yyyy", startDate) as String // 2020

                        mPicker.setStartDate(
                            startDay.toInt(),
                            startMonthNumber.toInt(),
                            startYear.toInt()
                        )
                        Log.d(
                            "onResponseTag",
                            "setStartDate: $startDay -$startMonthNumber - $startYear"
                        )

                        // set end date
                        val endingDate = slotsDate.slotEnd
                        val endDate: Date = FitManager.dateFormat.parse(endingDate) ?: Date()
                        val endDay = DateFormat.format("dd", endDate) as String // 18
                        val endMonthNumber = DateFormat.format("MM", endDate) as String // 05
                        val endYear = DateFormat.format("yyyy", endDate) as String // 2020

                        mPicker.setEndDate(endDay.toInt(), endMonthNumber.toInt(), endYear.toInt())
                        Log.d("onResponseTag", "setStartDate: $endDay -$endMonthNumber - $endYear")
                    } else {
                        Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<WorkingDaysResponse>?, t: Throwable?) {
                // hide progressbar
                slotsProgressBar.visibility = View.GONE

                Log.d("onResponseTag", "ErrorOnFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


    }

}