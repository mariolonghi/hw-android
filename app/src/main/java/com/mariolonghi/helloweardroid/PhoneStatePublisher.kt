package com.mariolonghi.helloweardroid

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.mariolonghi.helloweardroid.shared.DataLayerPaths
import com.mariolonghi.helloweardroid.shared.PhoneState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await

/**
 * Publishes the phone's UI state to the Wear Data Layer.
 *
 * Combines the three input flows into a single [PhoneState] and writes a
 * DataItem at [DataLayerPaths.PHONE_STATE] whenever the state changes. The
 * write is debounced (300 ms) so a rapid text-input burst doesn't generate
 * one DataItem write per keystroke.
 *
 * Resilience: if the watch is offline, Play Services queues the latest
 * write and delivers it when the watch reconnects. We don't retry.
 */
class PhoneStatePublisher(context: Context) {

    private val dataClient = Wearable.getDataClient(context)

    @OptIn(FlowPreview::class) // debounce is still marked preview in some kotlinx-coroutines builds
    fun start(
        scope: CoroutineScope,
        toggle: Flow<Boolean>,
        text: Flow<String>,
        batteryPercent: Flow<Int>,
    ) {
        combine(toggle, text, batteryPercent) { t, x, b ->
            PhoneState(toggle = t, text = x, batteryPercent = b)
        }
            .distinctUntilChanged()
            .debounce(300)
            .onEach(::publish)
            .launchIn(scope)
    }

    private suspend fun publish(state: PhoneState) {
        try {
            val request = PutDataMapRequest.create(DataLayerPaths.PHONE_STATE)
                .apply { dataMap.putAll(state.toDataMap()) }
                .asPutDataRequest()
                .setUrgent() // bypass Play Services' BLE batching for low latency
            dataClient.putDataItem(request).await()
            Log.d(TAG, "Published PhoneState: $state")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to publish PhoneState", t)
        }
    }

    companion object {
        private const val TAG = "PhoneStatePublisher"
    }
}
