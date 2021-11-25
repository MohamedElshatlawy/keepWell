package com.cornetelevated.clinics.screens.extensions

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cornetelevated.clinics.android.kotlin.R

fun TextView.deselect(context: Context) {
    this.setTextColor(ContextCompat.getColor(context, R.color.textColor))
    this.background = ContextCompat.getDrawable(context, R.drawable.bg_button_unselected)
}

fun TextView.select(context: Context) {
    this.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
    this.background = ContextCompat.getDrawable(context, R.drawable.bg_button_solid)
}