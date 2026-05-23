package com.mariolonghi.helloweardroid.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.mariolonghi.helloweardroid.shared.PhoneState
import com.mariolonghi.helloweardroid.wear.R
import com.mariolonghi.helloweardroid.wear.WearViewModel
import com.mariolonghi.helloweardroid.wear.presentation.theme.HelloWearDroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WearApp() }
    }
}

@Composable
fun WearApp(viewModel: WearViewModel = viewModel()) {
    HelloWearDroidTheme {
        val watchBattery by viewModel.batteryPercent.collectAsStateWithLifecycle()
        val phoneState by viewModel.phoneState.collectAsStateWithLifecycle()

        AppScaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                StateColumn(phoneState = phoneState, watchBattery = watchBattery)
            }
        }
    }
}

@Composable
private fun StateColumn(phoneState: PhoneState?, watchBattery: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        SectionHeader(text = stringResource(R.string.section_phone))
        if (phoneState == null) {
            Text(
                text = stringResource(R.string.phone_offline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                text = stringResource(R.string.battery_value, phoneState.batteryPercent.coerceAtLeast(0)),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    if (phoneState.toggle) R.string.toggle_on else R.string.toggle_off
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (phoneState.text.isNotBlank()) {
                Text(
                    text = phoneState.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        SectionHeader(
            text = stringResource(R.string.section_watch),
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = if (watchBattery < 0) "…"
            else stringResource(R.string.battery_value, watchBattery),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    HelloWearDroidTheme {
        AppScaffold {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                StateColumn(
                    phoneState = PhoneState(toggle = true, text = "hello", batteryPercent = 42),
                    watchBattery = 87,
                )
            }
        }
    }
}
