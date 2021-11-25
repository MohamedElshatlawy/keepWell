package com.cornetelevated.corehealth.services

import android.app.IntentService
import android.content.Intent
import com.cornetelevated.corehealth.models.fit.FitManager


class SyncService : IntentService("SyncService") {
    override fun onHandleIntent(intent: Intent?) {
        FitManager.getInstance(applicationContext)?.fetchData(true)
    }
}
