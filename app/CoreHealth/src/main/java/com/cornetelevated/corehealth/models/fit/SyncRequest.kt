package com.cornetelevated.corehealth.models.fit

class SyncRequest {

    var readings = FitReadings()
    var currentIndex = 0
    var currentType = 0

    private var mIsSyncing = false

    fun isSyncing(): Boolean {
        return mIsSyncing
    }

    fun startSync() {
        if (!mIsSyncing) {
            mIsSyncing = true
            readings = FitReadings()
            currentIndex = 0
            currentType = 0
        }
    }

    fun endSync() {
        mIsSyncing = false
    }

}