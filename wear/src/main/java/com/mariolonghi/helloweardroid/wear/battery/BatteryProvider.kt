package com.mariolonghi.helloweardroid.wear.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Watch-side wrapper for [Intent.ACTION_BATTERY_CHANGED].
 *
 * Same implementation as the phone module's BatteryProvider — kept duplicated
 * for now so each module is self-contained. When we have >1 duplicated class,
 * we'll extract a `:shared` Gradle module that both depend on. (Stage 3C/3D.)
 */
class BatteryProvider(private val context: Context) {

    val percentFlow: Flow<Int> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                intent ?: return
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    trySend(level * 100 / scale)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()
}
