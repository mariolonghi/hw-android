package com.mariolonghi.helloweardroid.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
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
        val batteryPercent by viewModel.batteryPercent.collectAsStateWithLifecycle()

        AppScaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.watch_battery_static_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = if (batteryPercent < 0) "…" else stringResource(
                            R.string.watch_battery_value,
                            batteryPercent,
                        ),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    // Previews of the composable structure go through the wrapper below.
    // Real verification is on the emulator (a Preview can't drive a ViewModel
    // backed by a system broadcast receiver).
    HelloWearDroidTheme {
        AppScaffold {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Watch battery", style = MaterialTheme.typography.bodyMedium)
                    Text("87%", style = MaterialTheme.typography.displayMedium)
                }
            }
        }
    }
}
