package com.cornetelevated.clinics.screens.home.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.profile.adapters.HistoryAdapter
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.response.AppointmentsResponse
import com.cornetelevated.corehealth.screens.activities.LoginActivity
import com.cornetelevated.corehealth.screens.fragments.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class HistoryFragment: Fragment(), ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var spinner: ProgressBar
    var appointments: ArrayList<Appointment> = ArrayList()

    private var currentPage: Int = 0
    private var isLastPage = false
    private var pageSize = 10
    private var isLoading = false

    private  val TAG = "HistoryFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = root.findViewById(R.id.recyclerView)
        spinner = root.findViewById(R.id.spinner)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = HistoryAdapter(this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLastPage && !isLoading) {
                    Log.d(TAG, "onScrolled: currentPage = $currentPage")
                    Log.d(TAG, "onScrolled: isLastPage = $isLastPage")
                    Log.d(TAG, "onScrolled: isLoading = $isLoading")

                    currentPage++
                    adapter.addLoadingFooter()
                    getAppointmentsHistory(currentPage)
                }

            }
        })

        currentPage = 0
        getAppointmentsHistory(currentPage)
        return root
    }


    override fun onItemClick(positionId: Int, v: View) {
        when (v.id) {
            R.id.historyLayout -> {
                openDetailsFragment(positionId)
            }
        }
    }


    private fun getAppointmentsHistory(pageNumber: Int) {

        isLoading = true
        if (currentPage == 0) {
            Log.d(TAG, "getAppointmentsHistory :currentPage = 0 show progressbar ")
            // show progressbar
            spinner.visibility = View.VISIBLE
        }
        val map = HashMap<String, Any>()
        map["PageNumber"] = pageNumber
        map["PageSize"] = 10

        val call: Call<AppointmentsResponse> =
            APIClient.appointmentInterface.getAppHistory(LoginActivity.getUserToken(), map)

        call.enqueue(object : Callback<AppointmentsResponse> {

            override fun onResponse(
                call: Call<AppointmentsResponse>?,
                response: Response<AppointmentsResponse>?
            ) {

                isLoading = false

                Log.d(TAG, "getAppointmentsHistory: currentPage = $currentPage")
                Log.d(TAG, "getAppointmentsHistory: isLastPage = $isLastPage")
                Log.d(TAG, "getAppointmentsHistory: isLoading = $isLoading")

                if (currentPage == 0) {
                    // hide progressbar
                    spinner.visibility = View.GONE
                } else {
                    adapter.removeLoadingFooter()
                }

                Log.d(TAG, "onResponse: $response")
                Log.d(TAG, "success: ${response!!.body()!!.success!!}")

                if (response.body()!!.success!!) {

                    Log.d(TAG, "ListSize: ${response.body()!!.response!!.size}")

                    if (response.body()!!.response!!.size > 0) {

                        isLastPage = false
                        appointments = ArrayList()
                        appointments.addAll(response.body()!!.response!!)
                        //adding data to adapter list
                        adapter.setData(appointments)

                        if (response.body()!!.response!!.size < 10) {
                            isLastPage = true
                            Log.d(TAG, "isLastPage =: $isLastPage")
                        }


                    } else {
                        isLastPage = true
                        Log.d("onResponseTag", "isLastPage =: $isLastPage")
                    }


                } else {
                    Toast.makeText(
                        context,
                        response.body()!!.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AppointmentsResponse>?, t: Throwable?) {

                isLoading = false

                if (currentPage == 0) {
                    // hide progressbar
                    spinner.visibility = View.GONE
                } else {
                    adapter.removeLoadingFooter()
                }

                Log.d(TAG, "onFailure: " + t!!)

                Toast.makeText(
                    context,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun openDetailsFragment(objectId: Int) {
/*
        var appDetailsFragment = AppointmentDetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("AppointmentObject", historyData[objectId])

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