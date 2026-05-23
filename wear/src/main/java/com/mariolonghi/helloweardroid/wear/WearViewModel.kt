package com.mariolonghi.helloweardroid.wear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mariolonghi.helloweardroid.wear.battery.BatteryProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Watch-side state holder.
 *
 * Stage 3B: only own (watch) battery.
 * Stage 3C will add phone-side state received via the Wear Data Layer
 * (phone battery, toggle, text).
 */
class WearViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryProvider = BatteryProvider(application)

    val batteryPercent: StateFlow<Int> = batteryProvider.percentFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = -1,
        )
}
