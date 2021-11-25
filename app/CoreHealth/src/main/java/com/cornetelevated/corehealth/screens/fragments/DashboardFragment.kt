package com.cornetelevated.corehealth.screens.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.models.fit.DataReading
import com.cornetelevated.corehealth.screens.activities.AddReadingActivity
import com.cornetelevated.corehealth.screens.activities.HomeActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var bloodPressureChart: LineChart
    private lateinit var heartRateChart: LineChart
    private lateinit var glucoseChart: LineChart
    private lateinit var oxygenChart: LineChart
    private lateinit var stepsChart: LineChart
    private lateinit var weightChart: LineChart

    private lateinit var weightLabel: TextView
    private lateinit var bloodPressureLabel: TextView
    private lateinit var heartRateLabel: TextView
    private lateinit var glucoseLabel: TextView
    private lateinit var oxygenLabel: TextView
    private lateinit var stepsLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        bloodPressureChart = root.findViewById(R.id.bpChart)
        heartRateChart = root.findViewById(R.id.pulseChart)
        glucoseChart = root.findViewById(R.id.glucoseChart)
        oxygenChart = root.findViewById(R.id.oxygenChart)
        stepsChart = root.findViewById(R.id.stepsChart)
        weightChart = root.findViewById(R.id.weightChart)
        weightLabel = root.findViewById(R.id.weightLabel)
        bloodPressureLabel = root.findViewById(R.id.bpLabel)
        heartRateLabel = root.findViewById(R.id.pulseLabel)
        glucoseLabel = root.findViewById(R.id.glucoseLabel)
        oxygenLabel = root.findViewById(R.id.oxygenLabel)
        stepsLabel = root.findViewById(R.id.stepsLabel)
        initCharts()
        val addWeightButton = root.findViewById<ImageButton>(R.id.addWeightButton)
        val addStepsButton = root.findViewById<ImageButton>(R.id.addStepsButton)
        val addHRButton = root.findViewById<ImageButton>(R.id.addHRButton)
        val addBPButton = root.findViewById<ImageButton>(R.id.addBPButton)
        val addOxygenButton = root.findViewById<ImageButton>(R.id.addOxygenButton)
        val addGlucoseButton = root.findViewById<ImageButton>(R.id.addGlucoseButton)
        addWeightButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.Weight)
        }
        addStepsButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.Steps)
        }
        addHRButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.HeartRate)
        }
        addBPButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.BloodPressure)
        }
        addOxygenButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.Oxygen)
        }
        addGlucoseButton.setOnClickListener { v: View? ->
            showAddReading(AddReadingActivity.ReadingType.Glucose)
        }
        val weightCard: CardView = root.findViewById(R.id.weightCard)
        val stepsCard: CardView = root.findViewById(R.id.stepsCard)
        val bpCard: CardView = root.findViewById(R.id.bpCard)
        val pulseCard: CardView = root.findViewById(R.id.pulseCard)
        val oxygenCard: CardView = root.findViewById(R.id.oxyCard)
        val glucoseCard: CardView = root.findViewById(R.id.glucCard)
        weightCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.Weight)
        }
        stepsCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.Steps)
        }
        pulseCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.HeartRate)
        }
        bpCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.BloodPressure)
        }
        oxygenCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.Oxygen)
        }
        glucoseCard.setOnClickListener { v: View? ->
            showReadingHistory(AddReadingActivity.ReadingType.Glucose)
        }
        return root
    }

    private fun initCharts() {
        val description = Description()
        description.text = ""
        bloodPressureChart.description = description
        bloodPressureChart.setNoDataText("")
        bloodPressureChart.setDrawBorders(false)
        bloodPressureChart.axisLeft.setDrawLabels(false)
        bloodPressureChart.axisLeft.setDrawGridLines(false)
        bloodPressureChart.axisLeft.axisLineColor = Color.TRANSPARENT
        bloodPressureChart.axisRight.setDrawLabels(false)
        bloodPressureChart.axisRight.setDrawGridLines(false)
        bloodPressureChart.axisRight.axisLineColor = Color.TRANSPARENT
        bloodPressureChart.xAxis.setDrawLabels(false)
        bloodPressureChart.xAxis.setDrawGridLines(false)
        bloodPressureChart.xAxis.axisLineColor = Color.TRANSPARENT
        bloodPressureChart.legend.isEnabled = false
        bloodPressureChart.setTouchEnabled(false)
        bloodPressureChart.isFocusable = false
        bloodPressureChart.isDragEnabled = false
        bloodPressureChart.setScaleEnabled(false)
        heartRateChart.description = description
        heartRateChart.setNoDataText("")
        heartRateChart.setDrawBorders(false)
        heartRateChart.axisLeft.setDrawLabels(false)
        heartRateChart.axisLeft.setDrawGridLines(false)
        heartRateChart.axisLeft.axisLineColor = Color.TRANSPARENT
        heartRateChart.axisRight.setDrawLabels(false)
        heartRateChart.axisRight.setDrawGridLines(false)
        heartRateChart.axisRight.axisLineColor = Color.TRANSPARENT
        heartRateChart.xAxis.setDrawLabels(false)
        heartRateChart.xAxis.setDrawGridLines(false)
        heartRateChart.xAxis.axisLineColor = Color.TRANSPARENT
        heartRateChart.legend.isEnabled = false
        heartRateChart.setTouchEnabled(false)
        heartRateChart.isFocusable = false
        heartRateChart.isDragEnabled = false
        heartRateChart.setScaleEnabled(false)
        weightChart.description = description
        weightChart.setNoDataText("")
        weightChart.setDrawBorders(false)
        weightChart.axisLeft.setDrawLabels(false)
        weightChart.axisLeft.setDrawGridLines(false)
        weightChart.axisLeft.axisLineColor = Color.TRANSPARENT
        weightChart.axisRight.setDrawLabels(false)
        weightChart.axisRight.setDrawGridLines(false)
        weightChart.axisRight.axisLineColor = Color.TRANSPARENT
        weightChart.xAxis.setDrawLabels(false)
        weightChart.xAxis.setDrawGridLines(false)
        weightChart.xAxis.axisLineColor = Color.TRANSPARENT
        weightChart.legend.isEnabled = false
        weightChart.setTouchEnabled(false)
        weightChart.isFocusable = false
        weightChart.isDragEnabled = false
        weightChart.setScaleEnabled(false)
        stepsChart.description = description
        stepsChart.setNoDataText("")
        stepsChart.setDrawBorders(false)
        stepsChart.axisLeft.setDrawLabels(false)
        stepsChart.axisLeft.setDrawGridLines(false)
        stepsChart.axisLeft.axisLineColor = Color.TRANSPARENT
        stepsChart.axisRight.setDrawLabels(false)
        stepsChart.axisRight.setDrawGridLines(false)
        stepsChart.axisRight.axisLineColor = Color.TRANSPARENT
        stepsChart.xAxis.setDrawLabels(false)
        stepsChart.xAxis.setDrawGridLines(false)
        stepsChart.xAxis.axisLineColor = Color.TRANSPARENT
        stepsChart.legend.isEnabled = false
        stepsChart.setTouchEnabled(false)
        stepsChart.isFocusable = false
        stepsChart.isDragEnabled = false
        stepsChart.setScaleEnabled(false)
        oxygenChart.description = description
        oxygenChart.setNoDataText("")
        oxygenChart.setDrawBorders(false)
        oxygenChart.axisLeft.setDrawLabels(false)
        oxygenChart.axisLeft.setDrawGridLines(false)
        oxygenChart.axisLeft.axisLineColor = Color.TRANSPARENT
        oxygenChart.axisRight.setDrawLabels(false)
        oxygenChart.axisRight.setDrawGridLines(false)
        oxygenChart.axisRight.axisLineColor = Color.TRANSPARENT
        oxygenChart.xAxis.setDrawLabels(false)
        oxygenChart.xAxis.setDrawGridLines(false)
        oxygenChart.xAxis.axisLineColor = Color.TRANSPARENT
        oxygenChart.legend.isEnabled = false
        oxygenChart.setTouchEnabled(false)
        oxygenChart.isFocusable = false
        oxygenChart.isDragEnabled = false
        oxygenChart.setScaleEnabled(false)
        glucoseChart.description = description
        glucoseChart.setNoDataText("")
        glucoseChart.setDrawBorders(false)
        glucoseChart.axisLeft.setDrawLabels(false)
        glucoseChart.axisLeft.setDrawGridLines(false)
        glucoseChart.axisLeft.axisLineColor = Color.TRANSPARENT
        glucoseChart.axisRight.setDrawLabels(false)
        glucoseChart.axisRight.setDrawGridLines(false)
        glucoseChart.axisRight.axisLineColor = Color.TRANSPARENT
        glucoseChart.xAxis.setDrawLabels(false)
        glucoseChart.xAxis.setDrawGridLines(false)
        glucoseChart.xAxis.axisLineColor = Color.TRANSPARENT
        glucoseChart.legend.isEnabled = false
        glucoseChart.setTouchEnabled(false)
        glucoseChart.isFocusable = false
        glucoseChart.isDragEnabled = false
        glucoseChart.setScaleEnabled(false)
    }

    private fun showAddReading(type: AddReadingActivity.ReadingType) {
        activity?.applicationContext?.let {
            val addIntent = Intent(it, AddReadingActivity::class.java)
            addIntent.putExtra("ReadingType", type)
            startActivity(addIntent)
        }
    }

    private fun showReadingHistory(type: AddReadingActivity.ReadingType) {
        val activity: HomeActivity? = activity as HomeActivity?
        activity?.showReadingHistory(type)
    }

    fun updateScale(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            weightLabel.text = String.format("%.0f", readings[readings.size - 1].value * 2.20462)
            weightChart.data = lineData(readings, Color.rgb(204, 226, 250), Color.rgb(226, 239, 254), false)
            weightChart.invalidate()
        }
    }

    fun updateBloodPressure(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            bloodPressureLabel.text = String.format("%.0f/%.0f", readings[readings.size - 1].maxValue, readings[readings.size - 1].minValue)
            bloodPressureChart.data = lineData(readings, Color.RED, Color.BLACK, true)
            bloodPressureChart.invalidate()
        }
    }

    fun updateHeartRate(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            heartRateLabel.text = String.format("%.0f", readings[readings.size - 1].value)
            heartRateChart.data = lineData(readings, Color.RED, Color.WHITE, false)
            heartRateChart.invalidate()
        }
    }

    fun updateSteps(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            stepsLabel.text = String.format("%.0f", readings[readings.size - 1].value)
            stepsChart.data = lineData(readings, Color.rgb(255, 175, 68), Color.rgb(255, 224, 168), false)
            stepsChart.invalidate()
        }
    }

    fun updateGlucose(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            glucoseLabel.text = String.format("%.0f", readings[readings.size - 1].value)
            glucoseChart.data = lineData(readings, Color.rgb(204, 226, 250), Color.rgb(226, 239, 254), false)
            glucoseChart.invalidate()
        }
    }

    fun updateOxygen(readings: ArrayList<DataReading>) {
        if (readings.size > 0) {
            oxygenLabel.text = String.format("%.0f", readings[readings.size - 1].value * 100)
            oxygenChart.data = lineData(readings, Color.rgb(204, 226, 250), Color.rgb(226, 239, 254), false)
            oxygenChart.invalidate()
        }
    }

    private fun lineData(
        readings: ArrayList<DataReading>,
        lineColor: Int,
        fillColor: Int,
        multipleLines: Boolean
    ): LineData {
        return if (multipleLines) {
            val maxEntries: MutableList<Entry> = ArrayList()
            val minEntries: MutableList<Entry> = ArrayList()
            for (i in readings.indices) {
                maxEntries.add(Entry(i.toFloat(), readings[i].maxValue.toFloat()))
                minEntries.add(Entry(i.toFloat(), readings[i].minValue.toFloat()))
            }
            val maxDataSet = LineDataSet(maxEntries, "")
            maxDataSet.setDrawCircles(false)
            maxDataSet.setDrawFilled(true)
            maxDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            maxDataSet.setDrawFilled(false)
            maxDataSet.setDrawValues(false)
            maxDataSet.color = lineColor
            val minDataSet = LineDataSet(minEntries, "")
            minDataSet.setDrawCircles(false)
            minDataSet.setDrawFilled(true)
            minDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            minDataSet.setDrawFilled(false)
            minDataSet.setDrawValues(false)
            minDataSet.color = fillColor
            val dSets: MutableList<ILineDataSet> = ArrayList()
            dSets.add(maxDataSet)
            dSets.add(minDataSet)
            LineData(dSets)
        } else {
            val entries: MutableList<Entry> = ArrayList()
            for (i in readings.indices) {
                entries.add(Entry(i.toFloat(), readings[i].value.toFloat()))
            }
            val dataSet = LineDataSet(entries, "")
            dataSet.setDrawCircles(false)
            dataSet.setDrawFilled(true)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.setDrawFilled(true)
            dataSet.setDrawValues(false)
            dataSet.fillColor = fillColor
            dataSet.color = lineColor
            val dSets: MutableList<ILineDataSet> = ArrayList()
            dSets.add(dataSet)
            LineData(dSets)
        }
    }

}