package com.example.unseenandstrong.ui.speakstrong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.script.ScriptEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey

@Composable
fun SpeakStrongScreen(
    viewModel: SpeakStrongViewModel,
    isFlareDay: Boolean = false,
    followUpRemindersEnabled: Boolean = false,
    followUpReminderMessage: String? = null,
    showNotificationSettingsAction: Boolean = false,
    onFollowUpRemindersChanged: (Boolean) -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onDraftAdaRequest: () -> Unit = {},
    onOpenResources: () -> Unit = {},
    onOpenBoundaryBuilder: () -> Unit = {},
    onOpenRequestLog: () -> Unit = {},
    onOpenBenefitsTracker: () -> Unit = {},
    onOpenStdLtdClaims: () -> Unit = {},
    onOpenAdvocacyPlans: () -> Unit = {},
    onOpenScript: (ScriptEntity) -> Unit = {}
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val selectedTone by viewModel.selectedTone.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val scripts by viewModel.scripts.collectAsState()
    var showAllAdvocacyTools by rememberSaveable(isFlareDay) {
        mutableStateOf(!isFlareDay)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Speak Strong", style = MaterialTheme.typography.headlineMedium, color = textColor)
            }
            item {
                Text(
                    "Choose the support that fits the conversation in front of you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlareDay) {
                            NightLavender.copy(alpha = 0.82f)
                        } else {
                            PaleCloudWhite
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Follow-up reminders",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = followUpRemindersEnabled,
                                onCheckedChange = onFollowUpRemindersChanged,
                                modifier = Modifier.semantics {
                                    stateDescription = if (followUpRemindersEnabled) "On" else "Off"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SoftBlushPink,
                                    checkedTrackColor = LavenderPurple,
                                    uncheckedThumbColor = SoftBlushPink,
                                    uncheckedTrackColor = DeepFogGrey.copy(alpha = 0.35f)
                                )
                            )
                        }
                        if (!isFlareDay) {
                            Text(
                                "Get a gentle local notification for advocacy follow-ups and deadlines you’re tracking.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                        followUpReminderMessage?.let { message ->
                            Text(
                                message,
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                        }
                        if (showNotificationSettingsAction) {
                            TextButton(
                                onClick = onOpenNotificationSettings,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    "Open notification settings",
                                    color = if (isFlareDay) SoftBlushPink else LavenderPurple
                                )
                            }
                        }
                    }
                }
            }
            if (showAllAdvocacyTools) {
                item { AdvocacyHubButton("Draft ADA Request", onDraftAdaRequest, true) }
                item { AdvocacyHubButton("Advocacy Resources", onOpenResources, false) }
                item { AdvocacyHubButton("Boundary Builder", onOpenBoundaryBuilder, true) }
                item { AdvocacyHubButton("Request Log", onOpenRequestLog, false) }
                item { AdvocacyHubButton("Disability Benefits Tracker", onOpenBenefitsTracker, true) }
                item { AdvocacyHubButton("STD/LTD Claims", onOpenStdLtdClaims, true) }
                item { AdvocacyHubButton("Saved Advocacy Plans", onOpenAdvocacyPlans, false) }
                if (isFlareDay) {
                    item {
                        TextButton(
                            onClick = { showAllAdvocacyTools = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show fewer advocacy tools", color = SoftBlushPink)
                        }
                    }
                }
            } else {
                item { AdvocacyHubButton("Saved Advocacy Plans", onOpenAdvocacyPlans, false) }
                item {
                    TextButton(
                        onClick = { showAllAdvocacyTools = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show all advocacy tools", color = SoftBlushPink)
                    }
                }
            }

            item {
                Text(
                    "Choose a tone",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SpeakStrongViewModel.Tone.entries) { tone ->
                        FilterChip(
                            selected = tone == selectedTone,
                            onClick = { viewModel.setTone(tone) },
                            label = {
                                Text(tone.name.lowercase().replaceFirstChar { it.uppercase() })
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }
            }
            item {
                Text(
                    "Choose a category",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SpeakStrongCatalog.categories, key = { it }) { category ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }
            }

            if (scripts.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "No scripts are available in this category yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(scripts, key = { it.id }) { script ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                script.category,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFlareDay) SoftBlushPink else DustyMauve
                            )
                            Text(script.title, style = MaterialTheme.typography.headlineSmall, color = textColor)
                            Text(
                                script.textFor(selectedTone),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                            Button(
                                onClick = {
                                    viewModel.selectScript(script)
                                    onOpenScript(script)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LavenderPurple,
                                    contentColor = NightLavender
                                )
                            ) {
                                Text("Practice this script")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvocacyHubButton(
    label: String,
    onClick: () -> Unit,
    emphasized: Boolean
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (emphasized) LavenderPurple else SoftBlushPink,
            contentColor = NightLavender
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}

internal fun ScriptEntity.textFor(tone: SpeakStrongViewModel.Tone): String = when (tone) {
    SpeakStrongViewModel.Tone.GENTLE -> gentleText
    SpeakStrongViewModel.Tone.DIRECT -> directText
    SpeakStrongViewModel.Tone.FIRM -> firmText
}
