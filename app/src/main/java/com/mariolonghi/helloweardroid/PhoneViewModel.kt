package com.mariolonghi.helloweardroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mariolonghi.helloweardroid.battery.BatteryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Holds the phone-side UI state for the proof-of-concept screen.
 *
 * Three observable bits of state:
 *  - [toggle] — Switch on/off
 *  - [text] — TextField contents
 *  - [batteryPercent] — live phone battery from [BatteryProvider]
 *
 * Subclasses [AndroidViewModel] because [BatteryProvider] needs the
 * Application context to register a system broadcast receiver. The
 * Application reference is held by the framework for the ViewModel's
 * lifetime — it is safe to retain (no Activity leak).
 *
 * `batteryPercent` is converted from a cold Flow to a hot StateFlow via
 * [stateIn]. `WhileSubscribed(5_000)` means the underlying broadcast
 * receiver only runs while the UI is observing, plus a 5s grace period
 * to survive configuration changes (rotation, theme change) without
 * tearing down and re-registering.
 */
class PhoneViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryProvider = BatteryProvider(application)

    private val _toggle = MutableStateFlow(false)
    val toggle: StateFlow<Boolean> = _toggle.asStateFlow()

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    /** -1 sentinel = "not yet measured". UI shows a placeholder for that case. */
    val batteryPercent: StateFlow<Int> = batteryProvider.percentFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = -1,
        )

    fun onToggleChange(value: Boolean) { _toggle.value = value }
    fun onTextChange(value: String) { _text.value = value }
}
