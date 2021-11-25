package com.cornetelevated.corehealth

import android.app.Application

class App : Application() {
    private var isInCall = false
    fun inCall(): Boolean {
        return isInCall
    }

    fun setInCall(call: Boolean) {
        isInCall = call
    }
}
