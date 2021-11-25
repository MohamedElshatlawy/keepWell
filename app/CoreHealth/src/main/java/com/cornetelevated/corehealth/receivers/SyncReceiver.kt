package com.cornetelevated.corehealth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cornetelevated.corehealth.services.SyncService

class SyncReceiver : BroadcastReceiver() {
    // Triggered by the Alarm periodically (starts the service to run task)
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, SyncService::class.java)
        i.putExtra("foo", "bar")
        context.startService(i)
    }

    companion object {
        const val REQUEST_CODE = 12345
    }
}
