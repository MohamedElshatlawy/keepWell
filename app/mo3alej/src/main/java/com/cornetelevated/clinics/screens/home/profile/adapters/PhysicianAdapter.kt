package com.cornetelevated.clinics.screens.home.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cornetelevated.clinics.android.kotlin.R
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.ArrayList

class PhysicianAdapter : RecyclerView.Adapter<PhysicianAdapter.PhysicianAdapterViewHolder?>() {
    private var dataList: List<String>
    var context: Context? = null

    init {
        dataList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhysicianAdapterViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_physician, parent, false)
        val holder = PhysicianAdapterViewHolder(itemView)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: PhysicianAdapterViewHolder, position: Int) {
        val item = dataList[position]
        holder.physicianNameTextView.text = item
        holder.specialistTextView.text = item
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    fun setData(dataList: List<String>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    inner class PhysicianAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var physicianImageView: CircularImageView = itemView.findViewById(R.id.physicianImageView)
        var physicianNameTextView: TextView = itemView.findViewById(R.id.physicianNameTextView)
        var specialistTextView: TextView = itemView.findViewById(R.id.specialistTextView)
        private var specialistCardView: CardView = itemView.findViewById(R.id.specialistCardView)

        init {
            specialistCardView.setOnClickListener {
                Toast.makeText(context!! ,"item $adapterPosition is clicked" , Toast.LENGTH_SHORT).show()
            }

        }
    }

}
