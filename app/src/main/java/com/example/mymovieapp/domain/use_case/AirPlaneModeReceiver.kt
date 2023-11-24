package com.example.mymovieapp.domain.use_case

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver

@SuppressLint("RestrictedApi")
class AirPlaneModeReceiver(val callback: (Boolean) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isTurnedOn = Settings.Global.getInt(
                context.contentResolver, Settings.Global.AIRPLANE_MODE_ON
            ) != 0
            callback(isTurnedOn)
        }
    }
}