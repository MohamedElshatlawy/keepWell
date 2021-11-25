package com.cornetelevated.corehealth.screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

open class Fragment : Fragment() {
    var isLoaded = false
    var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}