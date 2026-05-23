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
 * Also owns a [PhoneStatePublisher] that mirrors the state to the watch via
 * the Wear Data Layer. The publisher is started in `init` with
 * [viewModelScope] so it lives exactly as long as the ViewModel does.
 *
 * Subclasses [AndroidViewModel] because both [BatteryProvider] and
 * [PhoneStatePublisher] need the Application context.
 */
class PhoneViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryProvider = BatteryProvider(application)
    private val publisher = PhoneStatePublisher(application)

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

    init {
        publisher.start(
            scope = viewModelScope,
            toggle = toggle,
            text = text,
            batteryPercent = batteryPercent,
        )
    }

    fun onToggleChange(value: Boolean) { _toggle.value = value }
    fun onTextChange(value: String) { _text.value = value }
}
