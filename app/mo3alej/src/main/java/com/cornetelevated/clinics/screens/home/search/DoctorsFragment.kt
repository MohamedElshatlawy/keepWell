package com.cornetelevated.clinics.screens.home.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.HomeActivity
import com.cornetelevated.clinics.screens.home.appointment.AppointmentDateTimeFragment
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Doctor
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.SearchPhysycianRequest
import com.cornetelevated.corehealth.network.response.DoctorResponse
import com.cornetelevated.corehealth.screens.fragments.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorsFragment: Fragment(), ItemClickListener {

    companion object {
        var doctorName: String = ""
        var doctorId: Int = 11
        var doctorAddress: String = ""
        var doctorAmount: Int = 300
        var doctorSpeciality: String = ""
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var adapter: DoctorsAdapter
    private var searchText: String = ""
    private var specialitySelectedId: Int = 0
    private var currentPage: Int = 1
    private var isLastPage = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_doctors, container, false)

        doctorList = ArrayList()

        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = DoctorsAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLastPage && !isLoading) {
                    currentPage++
                    adapter.addLoadingFooter()
                    getDoctorsList(currentPage)
                }

            }
        })

        // get data from intent
        val dataBundle = this.arguments

        dataBundle?.let {
            searchText = it.getString("searchText", "")!!
            specialitySelectedId = it.getInt("specialitySelectedId", 0)
            doctorList = it.getParcelableArrayList<Doctor>("DoctorDataList")!!
            adapter.setData(doctorList)
            if (doctorList.size < 10) {
                isLastPage = true
            }
        }

        return root
    }

    private fun getDoctorsList(page: Int) {

        val model = SearchPhysycianRequest(searchText, specialitySelectedId, page)
        val call: Call<DoctorResponse> = APIClient.searchInterface.searchPhysicians(model.body())
        call.enqueue(object : Callback<DoctorResponse> {
            override fun onResponse(
                call: Call<DoctorResponse>?, response: Response<DoctorResponse>?
            ) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                if (it.total > 0) {
                                    doctorList.addAll(it.response)
                                    adapter.setData(doctorList)
                                } else {
                                    Toast.makeText(
                                        activity!!,
                                        "No available Doctors",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity!!,
                                    it.error ?: "",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            activity!!,
                            "can't get specialities now , please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

            override fun onFailure(call: Call<DoctorResponse>?, t: Throwable?) {
                Log.d("mTimerRunning", "onFailure: ${t!!.message}")
                Toast.makeText(
                    activity!!,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    override fun onItemClick(positionId: Int, v: View) {
        when (v.id) {
            R.id.doctorCardView -> {
                val item = doctorList[positionId]
                doctorName = "${item.firstName ?: ""} ${item.lastName ?: ""}"
                doctorId = item.clinicsInformation?.get(0)?.id ?: 0
                doctorAddress = item.clinicsInformation?.get(0)?.address ?: ""
                doctorAmount = item.clinicsInformation?.get(0)?.physicianFeesAmount ?: 0
                doctorSpeciality = item.speciality?.name ?: ""

                AppointmentDateTimeFragment.doctorId = item.id
                AppointmentDateTimeFragment.appId = null

                (activity as? HomeActivity)?.showFragment(AppointmentDateTimeFragment())
            }
        }
    }


}