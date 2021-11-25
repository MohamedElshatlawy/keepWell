package com.cornetelevated.clinics.screens.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.profile.adapters.AppointmentsAdapter
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.AppointmentsResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class AppointmentsFragment: Fragment(), ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentsAdapter
    private lateinit var spinner: ProgressBar
    var appointments: ArrayList<Appointment> = ArrayList()

    private var currentPage: Int = 0
    private var isLastPage = false
    private var pageSize = 10
    private var isLoading = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_appointments, container, false)

        recyclerView = root.findViewById(R.id.recyclerView)
        spinner = root.findViewById(R.id.spinner)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = AppointmentsAdapter(this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLastPage && !isLoading) {
                    currentPage++
                    adapter.addLoadingFooter()
                    getSchedule(currentPage)
                }

            }
        })

        currentPage = 0
        getSchedule(currentPage)

        return root
    }

    override fun onItemClick(positionId: Int, v: View) {
        /*
        when (v.id) {
            R.id.editAppImageView -> {
                ConfirmFragment.APP_ID = scheduleDate[positionId].id!!
                ConfirmFragment.paidAmount = scheduleDate[positionId].paidAmount!!

                // need to be edit
                ConfirmFragment.doctorId = scheduleDate[positionId].id

                ConfirmFragment.doctorName =
                    scheduleDate[positionId].physician!!.firstName!! + " " +
                            scheduleDate[positionId].physician!!.lastName!!

                ConfirmFragment.doctorSpeciality =
                    scheduleDate[positionId].physician!!.speciality!!.name!!

                loadBookingFragment()
            }

            R.id.deleteAppImageView -> {
                showCancelDialog(positionId)
            }

            R.id.swipe -> {
                openDetailsFragment(positionId)
            }

        }
        */
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

    private fun getSchedule(pageNumber: Int) {

        isLoading = true

        if (currentPage == 0) {
            // show progressbar
            spinner.visibility = View.VISIBLE
        }

        val map = HashMap<String, Any>()
        map["PageNumber"] = pageNumber
        map["PageSize"] = pageSize

        val call: Call<AppointmentsResponse> = APIClient.appointmentInterface.getSchedule(LoginActivity.getUserToken(), map)

        call.enqueue(object : Callback<AppointmentsResponse> {
            override fun onResponse(call: Call<AppointmentsResponse>?, response: Response<AppointmentsResponse>?) {
                isLoading = false
                if (currentPage == 0) {
                    // hide progressbar
                    spinner.visibility = View.GONE
                } else {
                    adapter.removeLoadingFooter()
                }

                Log.d("onResponseTag", "onResponse: $response")
                Log.d("onResponseTag", "success: ${response!!.body()!!.success!!}")

                response.body()?.let {
                    if (it.success) {
                        it.response?.let {
                            if (it.size > 0) {
                                isLastPage = false
                                appointments = ArrayList()
                                appointments.addAll(it)
                                adapter.setData(appointments)

                                if (it.size < 10) {
                                    isLastPage = true
                                    Log.d("onResponseTag", "isLastPage =: $isLastPage")
                                } else {
                                    isLastPage = false
                                }
                            } else {
                                isLastPage = true
                                Log.d("onResponseTag", "isLastPage =: $isLastPage")
                            }
                        }
                    } else {
                        Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AppointmentsResponse>?, t: Throwable?) {
                isLoading = false
                if (currentPage == 0) {
                    spinner.visibility = View.GONE
                } else {
                    adapter.removeLoadingFooter()
                }
                Log.d("onResponseTag", "onFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun cancelAppointment(appId: Int) {
        // show progressbar
        spinner.visibility = View.VISIBLE

        Log.d("onResponseTag", "AppointmentId = $appId")

        val map = HashMap<String, Any>()
        map["AppointmentId"] = appointments[appId].id!!

        val call: Call<ResponseBody> = APIClient.appointmentInterface.cancelAppointment(LoginActivity.getUserToken(), map)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                spinner.visibility = View.GONE
                Log.d("onResponseTag", "response  = $response")

                val stringResponse: String? = response!!.body()!!.string()

                Log.d("onResponseTag", "response string = $stringResponse")

                val jsonRes = JSONObject(stringResponse)
                val error: String? = jsonRes["error"] as String
                val success: Boolean? = jsonRes["success"] as Boolean

                if (success!!) {
                    //remove from the view
                    adapter.deleteItem(appId)
                    Toast.makeText(context, "Appointment Canceled Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                // hide progressbar
                spinner.visibility = View.GONE

                Log.d("onResponseTag", "onFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    // open BookingFragment
    private fun loadBookingFragment() {
        /*
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            BookingFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()
         */
    }

    private fun openDetailsFragment(objectId: Int) {
/*
        var appDetailsFragment = AppointmentDetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("AppointmentObject", scheduleDate[objectId])

        appDetailsFragment.arguments = bundle
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            appDetailsFragment
        )
        transaction.addToBackStack(null)
        transaction.commit()

 */
    }

}