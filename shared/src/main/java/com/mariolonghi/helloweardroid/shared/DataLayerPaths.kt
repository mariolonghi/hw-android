package com.mariolonghi.helloweardroid.shared

/**
 * Single source of truth for Wear Data Layer paths and DataMap keys.
 *
 * Both the phone publisher and the watch subscriber import from here so
 * there is no chance of one side writing "phone_battery" and the other
 * reading "phoneBattery".
 *
 * Paths are URI-like and namespace the data. Keep them short — they're
 * exchanged over BLE and add up if you have many DataItems.
 */
object DataLayerPaths {
    /** DataItem path that holds the phone's full UI state. */
    const val PHONE_STATE = "/state/phone"

    /** DataMap keys inside the [PHONE_STATE] DataItem. */
    object Keys {
        const val TOGGLE = "toggle"
        const val TEXT = "text"
        const val BATTERY_PERCENT = "battery_percent"
    }
}
