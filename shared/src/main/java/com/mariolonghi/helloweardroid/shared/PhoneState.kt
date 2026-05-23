package com.mariolonghi.helloweardroid.shared

import com.google.android.gms.wearable.DataMap

/**
 * The phone's full UI state, as observed by the watch.
 *
 * Wire-format-stable: changing any field name or type is a breaking change
 * to the Data Layer protocol — both modules must be redeployed together.
 *
 * @property toggle the phone Switch on/off
 * @property text the phone TextField contents
 * @property batteryPercent phone battery in `0..100`; `-1` = not yet measured
 */
data class PhoneState(
    val toggle: Boolean,
    val text: String,
    val batteryPercent: Int,
) {
    fun toDataMap(): DataMap = DataMap().apply {
        putBoolean(DataLayerPaths.Keys.TOGGLE, toggle)
        putString(DataLayerPaths.Keys.TEXT, text)
        putInt(DataLayerPaths.Keys.BATTERY_PERCENT, batteryPercent)
    }

    companion object {
        fun fromDataMap(map: DataMap): PhoneState = PhoneState(
            toggle = map.getBoolean(DataLayerPaths.Keys.TOGGLE, false),
            text = map.getString(DataLayerPaths.Keys.TEXT, ""),
            batteryPercent = map.getInt(DataLayerPaths.Keys.BATTERY_PERCENT, -1),
        )
    }
}
