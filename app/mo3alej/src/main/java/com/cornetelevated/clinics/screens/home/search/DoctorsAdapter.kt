package com.cornetelevated.clinics.screens.home.search

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Doctor
import com.mikhaellopez.circularimageview.CircularImageView

class DoctorsAdapter(val itemClickListener: ItemClickListener) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList: ArrayList<Doctor> = ArrayList()
    private val VIEW_LOADING = 0
    private val VIEW_CONTENT = 1
    private var isLoading = false
    private var imageByteArray: ByteArray? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_LOADING -> {
                val viewLoading: View =
                    inflater.inflate(R.layout.row_progress, parent, false)
                viewHolder = LoadingViewHolder(viewLoading)
            }
            VIEW_CONTENT -> {
                val viewHistory: View =
                    inflater.inflate(R.layout.row_doctor, parent, false)
                viewHolder = DoctorViewHolder(viewHistory, itemClickListener)
            }
        }
        return viewHolder!!

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = dataList[position]

        when (getItemViewType(position)) {
            VIEW_LOADING -> {
                val loadingVH = holder as LoadingViewHolder
            }
            VIEW_CONTENT -> {
                val doctorHolder = holder as DoctorViewHolder
                doctorHolder.doctorNameTextView.text = "${item.firstName} ${item.lastName}"
                doctorHolder.specialityTextView.text = item.speciality?.name ?: ""
                doctorHolder.clinicTextView.text = item.clinicsInformation?.get(0)?.clinicName ?: ""
                doctorHolder.addressTextView.text = item.clinicsInformation?.get(0)?.address ?: ""
                doctorHolder.amountTextView.text = "${item.clinicsInformation?.get(0)?.physicianFeesAmount ?: "0"} L.E."

                doctorHolder.doctorProfileImageView

                try {
                    imageByteArray = Base64.decode(
                        item.user?.profilePicture?.replace(
                            "data:image/jpg;base64,",
                            ""
                        )?.replace("data:image/png;base64,", ""), Base64.DEFAULT
                    )
                    val decodedByte =
                        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray!!.size)
                    doctorHolder.doctorProfileImageView.setImageBitmap(decodedByte)
                } catch (e: Exception) {
                    Log.i("", e.message ?: "")
                }

            }
        }


    }


    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size - 1 && isLoading) VIEW_LOADING else VIEW_CONTENT
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DoctorViewHolder(
        itemView: View, var itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        var doctorCardView: CardView = itemView.findViewById(R.id.doctorCardView)
        var doctorProfileImageView: CircularImageView =
            itemView.findViewById(R.id.doctorProfileImageView)
        var clinicTextView: TextView = itemView.findViewById(R.id.clinicTextView)
        var doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        var specialityTextView: TextView = itemView.findViewById(R.id.specialityTextView)
        var videoImageView: ImageView = itemView.findViewById(R.id.videoImageView)
        var physicalImageView: ImageView = itemView.findViewById(R.id.physicalImageView)
        //var distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        var amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        var addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        var timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        var doctorBookingLayout: LinearLayout = itemView.findViewById(R.id.doctorBookingLayout)


        init {
            doctorCardView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, doctorCardView)
            }


        }

    }


    //----- pagination -----
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadMoreProgress: ProgressBar = itemView.findViewById(R.id.loadMoreProgress)
    }


    fun addLoadingFooter() {
        isLoading = true
        addDoctor(Doctor())
    }

    private fun addDoctor(data: Doctor) {
        dataList.add(data)
        notifyItemInserted(dataList.size - 1)
    }

    fun setData(dataList: ArrayList<Doctor>) {
        for (result in dataList) {
            addDoctor(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position: Int = dataList.size - 1
        val result: Doctor? = getItem(position)
        if (result != null) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): Doctor? {
        return dataList[position]
    }


}