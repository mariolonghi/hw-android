package com.mariolonghi.helloweardroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mariolonghi.helloweardroid.ui.theme.HelloWearDroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWearDroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PhoneScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneScreen(
    modifier: Modifier = Modifier,
    viewModel: PhoneViewModel = viewModel(),
) {
    // collectAsStateWithLifecycle pauses collection when the Activity is
    // STOPPED. Cheaper than collectAsState for long-lived flows.
    val toggle by viewModel.toggle.collectAsStateWithLifecycle()
    val text by viewModel.text.collectAsStateWithLifecycle()
    val batteryPercent by viewModel.batteryPercent.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.toggle_label),
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = toggle,
                onCheckedChange = viewModel::onToggleChange,
            )
        }

        OutlinedTextField(
            value = text,
            onValueChange = viewModel::onTextChange,
            label = { Text(stringResource(R.string.text_input_label)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = if (batteryPercent < 0) {
                stringResource(R.string.phone_battery_unknown)
            } else {
                stringResource(R.string.phone_battery_label, batteryPercent)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhoneScreenPreview() {
    HelloWearDroidTheme {
        PhoneScreen()
    }
}
