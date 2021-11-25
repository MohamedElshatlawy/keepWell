package com.cornetelevated.clinics.screens.home.appointment

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.cornetelevated.corehealth.models.common.Slot

class AppointmentDateTimeAdapter: RecyclerView.Adapter<AppointmentDateTimeAdapter.BookingViewHolder?>() {

    private var dataList: List<Slot>

    init {
        dataList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.row_slot, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {

        val model = dataList[position]
        Log.d("timeData", "dataList[$position]: ${model.time}")
        holder.dateTextView.text = model.time.split("-")[0]
        // holder.dateTextView.text = model.time

        if (model.isSelected) {
            Log.d("positionData", " true")
            holder.dateTextView.setTextColor(Color.parseColor("#ffffff"))
            holder.dataCardView.setCardBackgroundColor(Color.parseColor("#3457a6"))
        } else {
            Log.d("positionData", " elseData")
            holder.dateTextView.setTextColor(Color.parseColor("#000000"))
            holder.dataCardView.setCardBackgroundColor(Color.parseColor("#ffffff"))
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(dataList: List<Slot>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        var dataCardView: CardView = itemView.findViewById(R.id.data_card_view)

        init {
            dataCardView.setOnClickListener {
                var itemIndex = 0
                while (itemIndex < dataList.size) {
                    dataList[itemIndex].isSelected = false
                    itemIndex++
                }
                dataList[adapterPosition].isSelected = true
                selectedSlot = dataList[adapterPosition].time
                notifyDataSetChanged()
            }
        }
    }

    companion object {
        var selectedSlot: String? = null
    }

}