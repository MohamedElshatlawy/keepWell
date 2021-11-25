package com.cornetelevated.clinics.screens.home.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.clinics.screens.extensions.deselect
import com.cornetelevated.clinics.screens.extensions.select
import com.cornetelevated.clinics.screens.home.profile.adapters.PhysicianAdapter
import com.cornetelevated.clinics.screens.home.profile.presenters.ProfilePresenter
import com.cornetelevated.corehealth.screens.fragments.Fragment
import com.mikhaellopez.circularimageview.CircularImageView

class ProfileFragment: Fragment() {
    private lateinit var physicianRecyclerView: RecyclerView
    private lateinit var physicianAdapter: PhysicianAdapter
    private lateinit var userCardView: CardView
    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: CircularImageView
    lateinit var profileProgressBar: ProgressBar
    private var imageByteArray: ByteArray? = null
    var isDataAvailable: Boolean = false
    var presenter: ProfilePresenter = ProfilePresenter(this)

    private lateinit var appointmentButton: TextView
    private lateinit var prescriptionButton: TextView
    private lateinit var historyButton: TextView
    private lateinit var reviewButton: TextView
    private var selectedButton: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_profile, container, false)
        physicianRecyclerView = root.findViewById(R.id.physicianRecyclerView)
        userCardView = root.findViewById(R.id.userCardView)
        profileImageView = root.findViewById(R.id.profileImageView)
        usernameTextView = root.findViewById(R.id.usernameTextView)
        profileProgressBar = root.findViewById(R.id.profileProgressBar)

        appointmentButton = root.findViewById(R.id.appointmentButton)
        prescriptionButton = root.findViewById(R.id.prescriptionButton)
        historyButton = root.findViewById(R.id.historyButton)
        reviewButton = root.findViewById(R.id.reviewButton)

        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        physicianRecyclerView.layoutManager = layoutManager

        isDataAvailable =false
        imageByteArray = null

        appointmentButton.setOnClickListener { loadAppointments() }
        historyButton.setOnClickListener { loadHistory() }
        reviewButton.setOnClickListener { loadReviews() }
        prescriptionButton.setOnClickListener { loadPrescriptions() }

        userCardView.setOnClickListener {
            if (isDataAvailable) {
                goToProfileDetailsFragment()
            }
        }

        presenter.interactor.fetchProfileData()
        loadPhysicians()
        loadAppointments()

        return root
    }

    private fun loadPhysicians() {
        physicianAdapter = PhysicianAdapter()
        physicianRecyclerView.adapter = physicianAdapter
        val dataList = ArrayList<String>()
        dataList.add("physician1")
        dataList.add("physician2")
        dataList.add("physician3")
        dataList.add("physician4")
        dataList.add("physician5")
        physicianAdapter.setData(dataList)
    }

    private fun loadAppointments() {
        if (selectedButton != appointmentButton) {
            select(appointmentButton)
            loadFragment(AppointmentsFragment())
        }
    }

    private fun loadHistory() {
        if (selectedButton != historyButton) {
            select(historyButton)
            loadFragment(HistoryFragment())
        }
    }

    private fun loadReviews() {
        if (selectedButton != reviewButton) {
            select(reviewButton)
            //loadFragment(ScheduleAppFragment())
        }
    }

    private fun loadPrescriptions() {
        if (selectedButton != prescriptionButton) {
            select(prescriptionButton)
            //loadFragment(ScheduleAppFragment())
        }
    }

    // go to goToProfileDetails Fragment
    private fun goToProfileDetailsFragment() {
/*
        val profileDetailsFragment = ProfileDetailsFragment()
        val dataBundle: Bundle? = Bundle()
        dataBundle!!.putString("profileUserName", presenter.interactor.profile?.username)
        dataBundle!!.putString("profileEmail", presenter.interactor.profile?.email)
        dataBundle!!.putString("profileMobile", presenter.interactor.profile?.mobile)
        dataBundle!!.putString("profileAddress", "user address")

        if (imageByteArray != null) {
            dataBundle!!.putByteArray("profileImage", imageByteArray!!)
        }

        profileDetailsFragment.arguments = dataBundle

        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, profileDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
*/
    }

    fun updateUI() {
        usernameTextView.text = presenter.interactor.profile?.username ?: ""

        try {
            imageByteArray = Base64.decode(
                presenter.interactor.profile?.profilePictureData?.replace(
                    "data:image/jpg;base64,",
                    ""
                )?.replace("data:image/png;base64,", ""), Base64.DEFAULT
            )
            val decodedByte =
                BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray!!.size)
            profileImageView.setImageBitmap(decodedByte)
        } catch (e: Exception) {
            Log.i("", e.message ?: "")
        }
    }

    private fun deselectAll(context: Context) {
        appointmentButton.deselect(context)
        prescriptionButton.deselect(context)
        historyButton.deselect(context)
        reviewButton.deselect(context)
    }

    private fun select(button: TextView) {
        context?.let {
            selectedButton = button
            deselectAll(it)
            button.select(it)
        }
    }

    // load Fragment
    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.dataContainer, fragment)
        transaction.commit()
    }

    //first time to add Fragment
    private fun addFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.add(R.id.dataContainer, fragment)
        transaction.commit()
    }

}