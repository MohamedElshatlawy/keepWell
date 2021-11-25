package com.cornetelevated.clinics.screens.home.profile.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.ItemClickListener
import com.cornetelevated.corehealth.models.common.Appointment
import com.cornetelevated.corehealth.models.fit.FitManager
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import com.daimajia.swipe.interfaces.SwipeAdapterInterface
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface
import com.daimajia.swipe.util.Attributes
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AppointmentsAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwipeItemMangerInterface, SwipeAdapterInterface {

    private val VIEW_LOADING = 0
    private val VIEW_CONTENT = 1
    private var isLoading = false

    var context: Context? = null
    private var dataList: ArrayList<Appointment> = ArrayList()

    var swipeLayout: SwipeLayout? = null
    private var mItemManger : SwipeItemRecyclerMangerImpl = SwipeItemRecyclerMangerImpl(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_LOADING -> {
                val viewLoading: View = inflater.inflate(R.layout.row_progress, parent, false)
                viewHolder = LoadingViewHolder(viewLoading)
            }
            VIEW_CONTENT -> {
                val viewSchedule: View =
                    inflater.inflate(R.layout.row_appointment, parent, false)
                viewHolder = ScheduleViewHolder(viewSchedule, itemClickListener)
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
                val scheduleViewHolder = holder as ScheduleViewHolder
                mItemManger.bindView(scheduleViewHolder.itemView, position)
                scheduleViewHolder.physicianName.text = item.physician?.firstName ?: "" + " " + item.physician?.lastName ?: ""
                scheduleViewHolder.physicianSpecialist.text = item.physician?.speciality?.name ?: ""
                // date and time
                val fromData: Date = FitManager.dateTimeFormat.parse(item.fromTime ?: "") ?: Date()
                val day = DateFormat.format("dd", fromData) as String
                val month = DateFormat.format("MM", fromData) as String
                val year = DateFormat.format("yyyy", fromData) as String
                val hours = DateFormat.format("HH", fromData) as String
                val minutes = DateFormat.format("mm", fromData) as String
                scheduleViewHolder.dateTextView.text = "$year-$month-$day | $hours:$minutes"

                when (item.appointmentTypeId) {
                    1 -> {
                        scheduleViewHolder.typeTextView.text = "Physical Session"
                    }
                    2 -> {
                        scheduleViewHolder.typeTextView.text = "Online Session"
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

    inner class ScheduleViewHolder(
        itemView: View, var itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        var scheduleCardView: CardView = itemView.findViewById(R.id.scheduleCardView)
        var physicianName: TextView = itemView.findViewById(R.id.physicianName)
        var physicianSpecialist: TextView = itemView.findViewById(R.id.physicianSpecialist)
        var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        var typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        var statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        var editAppImageView: ImageView = itemView.findViewById(R.id.editAppImageView)
        var deleteAppImageView: ImageView = itemView.findViewById(R.id.deleteAppImageView)

        init {

            swipeLayout = itemView.findViewById(R.id.swipe)

//            mItemManger.bindView(itemView, adapterPosition)

            swipeLayout!!.addSwipeListener(object : SwipeLayout.SwipeListener {
                override fun onClose(layout: SwipeLayout) {}
                override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
                override fun onStartOpen(layout: SwipeLayout) {}
                override fun onOpen(layout: SwipeLayout) {}
                override fun onStartClose(layout: SwipeLayout) {}
                override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
            })

            swipeLayout!!.surfaceView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, swipeLayout!!)
            }

            editAppImageView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, editAppImageView)
            }

            deleteAppImageView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, deleteAppImageView)
            }


        }

        fun setAppointmentStatus(status: String, icon: Int) {
            statusTextView.text = status
            statusTextView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }
    }

    fun deleteItem(itemPosition: Int) {
        mItemManger.removeShownLayouts(swipeLayout!!)
        dataList!!.removeAt(itemPosition)
        notifyItemRemoved(itemPosition)
        notifyItemRangeChanged(itemPosition, dataList!!.size)
        mItemManger.closeAllItems()
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


    // library methods

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mItemManger.closeAllItems()
    }

    override fun getOpenItems(): List<Int> {
        return mItemManger.openItems
    }

    override fun getOpenLayouts(): List<SwipeLayout> {
        return mItemManger.openLayouts
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }

    override fun getMode(): Attributes.Mode {
        return mItemManger.mode
    }

    override fun setMode(mode: Attributes.Mode) {
        mItemManger.mode = mode
    }

}