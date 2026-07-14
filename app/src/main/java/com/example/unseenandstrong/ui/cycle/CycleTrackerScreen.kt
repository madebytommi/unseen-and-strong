package com.example.unseenandstrong.ui.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.example.unseenandstrong.data.local.cycle.CycleLogEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey

private val cyclePhases = listOf(
    "Rest & Restore",
    "Emerging",
    "Bloom",
    "Retreat"
)

private val flowIntensities = listOf("Light", "Medium", "Heavy", "None")
private val trackingModes = listOf("Standard", "TTC", "Avoidance")

@Composable
fun CycleTrackerScreen(
    viewModel: CycleViewModel,
    isFlareDay: Boolean = false
) {
    val trackingMode by viewModel.trackingMode.collectAsState()
    val mostRecentCycleLog by viewModel.mostRecentCycleLog.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey
    val activeChipColor = LavenderPurple.copy(alpha = 0.45f)
    val activeAccentColor = if (isFlareDay) SoftBlushPink.copy(alpha = 0.78f) else LavenderPurple

    var selectedPhase by remember { mutableStateOf(cyclePhases.first()) }
    var selectedFlowIntensity by remember { mutableStateOf(flowIntensities.first()) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(mostRecentCycleLog) {
        mostRecentCycleLog?.let { log ->
            selectedPhase = log.phase
            selectedFlowIntensity = log.flowIntensity
        }
    }

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cycle Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        color = textColor
                    )
                    Text(
                        text = "A gentle place to notice patterns without pressure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Cycle settings",
                        tint = textColor
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Current Phase",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Text(
                        text = mostRecentCycleLog?.phase ?: "No phase logged yet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = activeAccentColor
                    )
                    Text(
                        text = mostRecentCycleLog?.let { "Flow: ${it.flowIntensity}" } ?: "Flow: not logged yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Text(
                        text = trackingMode,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.85f)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Log Today",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )

                    Text(
                        text = "Choose the phase that feels closest today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cyclePhases.forEach { phase ->
                            FilterChip(
                                selected = selectedPhase == phase,
                                onClick = { selectedPhase = phase },
                                label = {
                                    Text(
                                        text = phase,
                                        color = textColor
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = SoftCloudGrey,
                                    labelColor = textColor,
                                    selectedContainerColor = activeChipColor,
                                    selectedLabelColor = textColor
                                )
                            )
                        }
                    }

                    Text(
                        text = "Flow Intensity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        flowIntensities.forEach { intensity ->
                            FilterChip(
                                selected = selectedFlowIntensity == intensity,
                                onClick = { selectedFlowIntensity = intensity },
                                label = {
                                    Text(
                                        text = intensity,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = SoftCloudGrey,
                                    labelColor = textColor,
                                    selectedContainerColor = activeAccentColor.copy(alpha = 0.35f),
                                    selectedLabelColor = textColor
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.logTodayPhase(
                                phase = selectedPhase,
                                flowIntensity = selectedFlowIntensity
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeAccentColor,
                            contentColor = textColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Today")
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = SoftCloudGrey,
            title = {
                Text(
                    text = "Tracking Mode",
                    color = DeepFogGrey,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    trackingModes.forEach { mode ->
                        FilterChip(
                            selected = trackingMode == mode,
                            onClick = {
                                viewModel.updateTrackingMode(mode)
                            },
                            label = { Text(mode, color = DeepFogGrey) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SoftCloudGrey,
                                labelColor = DeepFogGrey,
                                selectedContainerColor = SoftBlushPink.copy(alpha = 0.5f),
                                selectedLabelColor = DeepFogGrey
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Done", color = LavenderPurple)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CycleTrackerScreenPreview() {
    // Preview omitted for ViewModel-backed screen.
}
