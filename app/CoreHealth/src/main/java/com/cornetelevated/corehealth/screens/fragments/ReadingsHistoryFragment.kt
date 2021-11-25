package com.cornetelevated.corehealth.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.corehealth.R
import com.cornetelevated.corehealth.models.fit.DataReading
import com.cornetelevated.corehealth.screens.activities.AddReadingActivity
import com.cornetelevated.corehealth.screens.adaptors.ReadingHistoryAdaptor
import com.cornetelevated.corehealth.screens.fragments.Fragment
import java.util.*

class ReadingsHistoryFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: ReadingHistoryAdaptor
    var readingType: AddReadingActivity.ReadingType = AddReadingActivity.ReadingType.Weight

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        return root
    }

    fun updateData(data: ArrayList<DataReading>) {
        adapter = ReadingHistoryAdaptor()
        adapter.setData(data, readingType)
        recyclerView?.adapter = adapter
    }
}