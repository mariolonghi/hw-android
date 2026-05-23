package com.mariolonghi.helloweardroid.battery

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
 * Wraps Android's sticky [Intent.ACTION_BATTERY_CHANGED] broadcast as a cold
 * [Flow] emitting battery percentage in `0..100`.
 *
 * `ACTION_BATTERY_CHANGED` is a *sticky* broadcast — when we register a
 * receiver for it, Android immediately delivers the last-known battery state,
 * then redelivers every time the battery level/charging state changes. We
 * translate that into a Flow so the ViewModel can observe it idiomatically.
 *
 * The flow is collected once per [percentFlow] subscriber; multiple
 * subscribers each register their own receiver. The [PhoneViewModel] funnels
 * it through `stateIn(...)` so only one receiver runs while the UI is alive.
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
