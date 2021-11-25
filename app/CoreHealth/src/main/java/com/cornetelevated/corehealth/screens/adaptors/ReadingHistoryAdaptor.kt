package com.cornetelevated.corehealth.screens.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.models.fit.DataReading
import com.cornetelevated.corehealth.models.fit.FitManager
import com.cornetelevated.corehealth.screens.activities.AddReadingActivity
import java.util.*

class ReadingHistoryAdaptor : RecyclerView.Adapter<ReadingHistoryAdaptor.ReadingHistoryViewHolder>() {
    var readingType: AddReadingActivity.ReadingType = AddReadingActivity.ReadingType.Weight
    var dataList: List<DataReading>
    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingHistoryViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.row_reading_history, parent, false)
        val holder = ReadingHistoryViewHolder(itemView)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ReadingHistoryViewHolder, position: Int) {
        val item: DataReading = dataList[position]
        holder.dateLabel.text = FitManager.dateFormatUS.format(item.date)
        when (readingType) {
            AddReadingActivity.ReadingType.Steps -> holder.valueLabel.hint = item.value.toInt().toString() + " steps"
            AddReadingActivity.ReadingType.Oxygen -> holder.valueLabel.hint = (item.value * 100).toInt().toString() + " %"
            AddReadingActivity.ReadingType.Weight -> holder.valueLabel.hint = (item.value / 0.453592).toInt().toString() + " lbs"
            AddReadingActivity.ReadingType.Glucose -> holder.valueLabel.hint = item.value.toInt().toString() + " mg/dL"
            AddReadingActivity.ReadingType.HeartRate -> holder.valueLabel.hint = item.value.toInt().toString() + " BPM"
            AddReadingActivity.ReadingType.BloodPressure -> holder.valueLabel.hint = item.maxValue.toInt().toString() + "/" + item.minValue.toInt().toString() + " mmHg"
        }
        holder.valueLabel.text = ""
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(dataList: List<DataReading>, type: AddReadingActivity.ReadingType) {
        readingType = type
        this.dataList = dataList
        notifyDataSetChanged()
    }

    inner class ReadingHistoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var valueLabel: TextView = itemView.findViewById(R.id.valueLabel)
        var dateLabel: TextView = itemView.findViewById(R.id.dateLabel)
    }

    init {
        dataList = ArrayList()
    }
}