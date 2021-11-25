package com.cornetelevated.clinics.screens.home.search

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.home.HomeActivity
import com.cornetelevated.corehealth.models.common.Doctor
import com.cornetelevated.corehealth.models.common.Speciality
import com.cornetelevated.corehealth.network.APIClient
import com.cornetelevated.corehealth.network.request.SearchPhysycianRequest
import com.cornetelevated.corehealth.network.response.DoctorResponse
import com.cornetelevated.corehealth.network.response.SpecialityResponse
import com.cornetelevated.corehealth.screens.fragments.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment: Fragment() {

    private lateinit var doctorEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var specialitySpinner: Spinner
    private lateinit var onlineButton: Button
    private lateinit var physicalButton: Button
    private lateinit var searchButton: Button
    private lateinit var spinner: ProgressBar

    private lateinit var specialityDataList: ArrayList<Speciality>
    private lateinit var specialityStringDataList: ArrayList<String>
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>

    private var specialitySelectedId = 0
    private var searchText: String = ""
    private var showBookingType: String = ""
    private var isDoctor: Boolean = false
    private var isSpeciality: Boolean = false
    private lateinit var doctorList: ArrayList<Doctor>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_search, container, false)

        doctorEditText = root.findViewById(R.id.doctorEditText)
        locationEditText = root.findViewById(R.id.locationEditText)
        onlineButton = root.findViewById(R.id.onlineButton)
        physicalButton = root.findViewById(R.id.physicalButton)
        searchButton = root.findViewById(R.id.actionButton)
        spinner = root.findViewById(R.id.spinner)

        onlineButton.setOnClickListener { onlineTapped() }
        physicalButton.setOnClickListener { physicalTapped() }

        doctorEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    isDoctor = true
                    searchText = s.toString()
                } else {
                    isDoctor = false
                    searchText = ""
                }
                searchButton.isEnabled = (isDoctor || isSpeciality)
            }
        })

        searchButton.setOnClickListener {
            if (isDoctor || isSpeciality) {
                getDoctorsList()
            }
        }

        specialitySpinner = root.findViewById(R.id.specialitySpinner)

        specialitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (specialityDataList[position].id != 0) {
                    specialitySelectedId = specialityDataList[position].id!!
                    isSpeciality = true
                    searchButton.isEnabled = (isSpeciality || isDoctor)
                } else {
                    specialitySelectedId = 0
                    isSpeciality = false
                    searchButton.isEnabled = (isSpeciality || isDoctor)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        onlineTapped()
        getSpecialityList()

        return root
    }

    private fun onlineTapped() {
        if (showBookingType != "onlineButton") {
            showBookingType = "onlineButton"
            context?.let {
                onlineButton.background = ContextCompat.getDrawable(it, R.drawable.bg_segment_left_seleted)
                onlineButton.setTextColor(ContextCompat.getColor(it, R.color.colorPrimaryButtonText))
                physicalButton.background = ContextCompat.getDrawable(it, R.drawable.bg_segment_right)
                physicalButton.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
            }
            locationEditText.visibility = View.GONE
        }
    }

    private fun physicalTapped() {
        if (showBookingType != "physicalButton") {
            showBookingType = "physicalButton"
            context?.let {
                onlineButton.background = ContextCompat.getDrawable(it, R.drawable.bg_segment_left)
                onlineButton.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
                physicalButton.background = ContextCompat.getDrawable(it, R.drawable.bg_segment_right_selected)
                physicalButton.setTextColor(ContextCompat.getColor(it, R.color.colorPrimaryButtonText))
            }
            locationEditText.visibility = View.GONE
        }
    }

    private fun getSpecialityList() {
        spinner.visibility = View.VISIBLE

        val call: Call<SpecialityResponse> = APIClient.searchInterface.getAllSpecialists()
        call.enqueue(object : Callback<SpecialityResponse> {
            override fun onResponse(
                call: Call<SpecialityResponse>?, response: Response<SpecialityResponse>?
            ) {
                spinner.visibility = View.GONE
                response?.let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                specialityDataList = ArrayList()
                                specialityStringDataList = ArrayList()

                                specialityDataList.add(Speciality(0, "Speciality"))
                                specialityDataList.addAll(response.body()!!.response!!)

                                for (model in specialityDataList) {
                                    specialityStringDataList.add(model.name ?: "")
                                }
                                spinnerArrayAdapter = ArrayAdapter(
                                    activity!!,
                                    R.layout.spinner_item, R.id.spinnerTextView, specialityStringDataList
                                )
                                specialitySpinner.adapter = spinnerArrayAdapter
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

            override fun onFailure(call: Call<SpecialityResponse>?, t: Throwable?) {

                spinner.visibility = View.GONE

                Log.d("mTimerRunning", "onFailure: ${t!!.message}")
                Toast.makeText(
                    activity!!,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun getDoctorsList() {
        spinner.visibility = View.VISIBLE
        val model = SearchPhysycianRequest(searchText, specialitySelectedId, 1)
        val call: Call<DoctorResponse> = APIClient.searchInterface.searchPhysicians(model.body())
        call.enqueue(object : Callback<DoctorResponse> {
            override fun onResponse(
                call: Call<DoctorResponse>?, response: Response<DoctorResponse>?
            ) {
                spinner.visibility = View.GONE
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                if (it.total > 0) {
                                    doctorList = ArrayList()
                                    doctorList.addAll(it.response)
                                    openDoctorsFragment()
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

                spinner.visibility = View.GONE

                Log.d("mTimerRunning", "onFailure: ${t!!.message}")
                Toast.makeText(
                    activity!!,
                    "something went wrong , please try again",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun openDoctorsFragment() {
        val doctorFragment = DoctorsFragment()
        val bundle = Bundle()
        bundle.putString("searchText", searchText)
        bundle.putInt("specialitySelectedId", specialitySelectedId)
        bundle.putParcelableArrayList("DoctorDataList", doctorList)
        doctorFragment.arguments = bundle
        (activity as? HomeActivity)?.showFragment(doctorFragment)
    }
}