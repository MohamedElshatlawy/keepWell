package com.cornetelevated.corehealth.screens.activities

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

open class Activity: AppCompatActivity() {

    var spinner: ProgressBar? = null

    fun hideKeyboard() {
        currentFocus?.let {
            it.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showProgress(actionButton: Button? = null) {
        hideKeyboard()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        spinner?.visibility = View.VISIBLE
        actionButton?.isEnabled = false
    }

    fun hideProgress(actionButton: Button? = null) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        spinner?.visibility = View.GONE
        actionButton?.isEnabled = true
    }
}