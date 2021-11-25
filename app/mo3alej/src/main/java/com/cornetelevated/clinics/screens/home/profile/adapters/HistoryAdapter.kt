package com.cornetelevated.clinics.screens.home.profile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.models.fit.FitManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList: ArrayList<Appointment> = ArrayList()
    var context: Context? = null
    private val VIEW_LOADING = 0
    private val VIEW_CONTENT = 1
    private var isLoading = false

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
                    inflater.inflate(R.layout.row_history, parent, false)
                viewHolder = HistoryViewHolder(viewHistory, itemClickListener)
            }
        }
        return viewHolder!!

    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = dataList[position]


        when (getItemViewType(position)) {
            VIEW_LOADING -> {
                val loadingVH = holder as LoadingViewHolder
            }
            VIEW_CONTENT -> {
                val historyViewHolder = holder as HistoryViewHolder

                if (position == 0) {
                    historyViewHolder.topSeparator.visibility = View.INVISIBLE
                }

                // main date
                val fromData: Date = FitManager.dateTimeFormat.parse(item.fromTime ?: "") ?: Date()
                val year = DateFormat.format("yyyy", fromData) as String // 2020
                val month = DateFormat.format("MM", fromData) as String // NOV
                val day = DateFormat.format("dd", fromData) as String // 22
                val hours = DateFormat.format("HH", fromData) as String
                val minutes = DateFormat.format("mm", fromData) as String

                val sourceDate = day + "\n" + month + "\n" + year

                Log.d("sourceDate", "onBindViewHolder: sourceDate = $sourceDate")

                val ss = SpannableString(sourceDate)
                ss.setSpan(RelativeSizeSpan(2f), 0, 2, 0) // set size
                ss.setSpan(ForegroundColorSpan(Color.parseColor("#42425b")), 0, 2, 0) // set color
                ss.setSpan(StyleSpan(Typeface.BOLD), 0, 2, 0) // set blod text

                ss.setSpan(RelativeSizeSpan(2f), 3, 6, 0) // set size
                ss.setSpan(ForegroundColorSpan(Color.parseColor("#B9BCBC")), 3, 6, 0) // set color

                ss.setSpan(RelativeSizeSpan(1f), 6, 10, 0) // set size
                ss.setSpan(ForegroundColorSpan(Color.parseColor("#B9BCBC")), 6, 10, 0) // set color

                historyViewHolder.mainDateTextView.text = ss

                // date
                //  historyViewHolder.dateTextView.text = "$year - $month - $day \n $hours : $minutes"
                historyViewHolder.dateTextView.text = "$hours:$minutes"

                historyViewHolder.nameTextView.text = item.physician?.firstName ?: "" + " " + item.physician?.lastName ?: ""

                historyViewHolder.specializationTextView.text = item.physician?.speciality?.name ?: ""

                when (item.appointmentTypeId) {
                    1 -> {
                        historyViewHolder.typeTextView.text = "Physical Session"
                    }
                    2 -> {
                        historyViewHolder.typeTextView.text = "Online Session"
                    }
                }

                // appointment status
                val toData: Date = FitManager.dateTimeFormat.parse(item.toTime ?: "") ?: Date()

                //  if fromData<= currentData <= toData { appointment status is In Progress }
                if (fromData.time <= System.currentTimeMillis() &&
                    System.currentTimeMillis() < toData.time
                ) {
                    holder.setAppointmentStatus("In Progress", R.drawable.ic_on_going)
                }
                //  if currentData > toData && appointmentStatusId !=2
                //  { appointment status is completed }
                else if (System.currentTimeMillis() > toData.time &&
                    item.appointmentStatusId != 2
                ) {
                    holder.setAppointmentStatus("Completed", R.drawable.ic_done_app)
                }
                //  if appointmentStatusId ==2 { appointment status is canceled }
                else if (item.appointmentStatusId == 2
                ) {
                    holder.setAppointmentStatus("Canceled", R.drawable.ic_cancel_app)
                }
                //  if appointmentStatusId ==1 { appointment status is Scheduled }
                else if (item.appointmentStatusId == 1
                ) {
                    holder.setAppointmentStatus("Scheduled", R.drawable.ic_on_going)
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

    inner class HistoryViewHolder(
        itemView: View, var itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        var historyLayout: LinearLayout = itemView.findViewById(R.id.historyLayout)
        var topSeparator: TextView = itemView.findViewById(R.id.topSeparator)
        var mainDateTextView: TextView = itemView.findViewById(R.id.mainDateTextView)
        var nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        var specializationTextView: TextView = itemView.findViewById(R.id.specializationTextView)
        var appointmentTextView: TextView = itemView.findViewById(R.id.appointmentTextView)
        var typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        var statusTextView: TextView = itemView.findViewById(R.id.statusTextView)

        init {
            historyLayout.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, historyLayout)
            }


        }

        fun setAppointmentStatus(status: String, icon: Int) {
            statusTextView.text = status
            statusTextView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }

    }


    //----- pagination -----
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadMoreProgress: ProgressBar = itemView.findViewById(R.id.loadMoreProgress)
    }


    fun addLoadingFooter() {
        isLoading = true
        addAppointment(Appointment())
    }

    private fun addAppointment(app: Appointment) {
        dataList.add(app)
        notifyItemInserted(dataList.size - 1)
    }

    fun setData(dataList: ArrayList<Appointment>) {
        for (result in dataList) {
            addAppointment(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position: Int = dataList.size - 1
        val result: Appointment? = getItem(position)
        if (result != null) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): Appointment? {
        return dataList[position]
    }


}