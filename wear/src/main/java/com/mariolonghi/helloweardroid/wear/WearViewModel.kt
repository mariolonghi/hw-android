package com.mariolonghi.helloweardroid.wear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mariolonghi.helloweardroid.shared.PhoneState
import com.mariolonghi.helloweardroid.wear.battery.BatteryProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Watch-side state holder.
 *
 * Two flows:
 *  - [batteryPercent] — the watch's own battery (-1 = unknown).
 *  - [phoneState] — the phone's last-known state, or null if the watch
 *    hasn't yet received anything (phone offline, or phone module not yet
 *    installed/launched on the paired phone).
 */
class WearViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryProvider = BatteryProvider(application)
    private val phoneStateRepository = PhoneStateRepository(application)

    val batteryPercent: StateFlow<Int> = batteryProvider.percentFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = -1,
        )

    val phoneState: StateFlow<PhoneState?> = phoneStateRepository.phoneStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
