package com.mariolonghi.helloweardroid.wear

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.mariolonghi.helloweardroid.shared.DataLayerPaths
import com.mariolonghi.helloweardroid.shared.PhoneState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Watch-side subscriber for the phone's [PhoneState] DataItem.
 *
 * Emits:
 *  - the most recently-stored DataItem once on subscription (so we don't
 *    wait for a phone change to render);
 *  - every subsequent change observed via [DataClient.OnDataChangedListener].
 *
 * Log lines are tagged [TAG] — `adb -s <wear> logcat -d -s PhoneStateRepository`
 * to see what the flow is doing.
 */
class PhoneStateRepository(context: Context) {

    private val dataClient = Wearable.getDataClient(context)

    val phoneStateFlow: Flow<PhoneState> = callbackFlow {
        Log.d(TAG, "callbackFlow active — subscribing to DataClient")
        val listener = DataClient.OnDataChangedListener { events ->
            Log.d(TAG, "Listener fired with ${events.count} event(s)")
            events.forEach { event ->
                Log.d(TAG, "  event type=${event.type} path=${event.dataItem.uri.path}")
                if (event.type != DataEvent.TYPE_CHANGED) return@forEach
                val item = event.dataItem
                if (item.uri.path != DataLayerPaths.PHONE_STATE) return@forEach
                val map = DataMapItem.fromDataItem(item).dataMap
                val state = PhoneState.fromDataMap(map)
                Log.d(TAG, "  emitting from listener: $state")
                trySend(state)
            }
            events.release()
        }
        dataClient.addListener(listener)
        Log.d(TAG, "Listener registered")

        // Replay the latest known value so we render immediately on app start,
        // not just when the phone next changes something.
        try {
            val items = dataClient.dataItems.await()
            Log.d(TAG, "Initial fetch returned ${items.count} item(s)")
            items.forEach { item ->
                Log.d(TAG, "  item path=${item.uri.path}")
                if (item.uri.path == DataLayerPaths.PHONE_STATE) {
                    val map = DataMapItem.fromDataItem(item).dataMap
                    val state = PhoneState.fromDataMap(map)
                    Log.d(TAG, "  initial emit: $state")
                    trySend(state)
                }
            }
            items.release()
        } catch (t: Throwable) {
            Log.w(TAG, "Initial DataItem fetch failed", t)
            // Not fatal — listener will pick up the next change.
        }

        awaitClose {
            Log.d(TAG, "awaitClose — removing listener")
            dataClient.removeListener(listener)
        }
    }

    companion object {
        private const val TAG = "PhoneStateRepository"
    }
}
