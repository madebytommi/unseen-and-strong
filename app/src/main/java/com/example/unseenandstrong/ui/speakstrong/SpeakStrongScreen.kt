package com.example.unseenandstrong.ui.speakstrong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey

@Composable
fun SpeakStrongScreen(
    viewModel: SpeakStrongViewModel,
    isFlareDay: Boolean = false,
    onDraftAdaRequest: () -> Unit = {},
    onOpenResources: () -> Unit = {},
    onOpenBoundaryBuilder: () -> Unit = {},
    onOpenRequestLog: () -> Unit = {},
    onOpenBenefitsTracker: () -> Unit = {}
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey

    val selectedTone by viewModel.selectedTone.collectAsState()
    val scripts by viewModel.scripts.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDraftAdaRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Draft ADA Request")
            }

            Button(
                onClick = onOpenResources,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink,
                    contentColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Advocacy Resources")
            }

            Button(
                onClick = onOpenBoundaryBuilder,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple.copy(alpha = 0.9f),
                    contentColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Boundary Builder")
            }

            Button(
                onClick = onOpenRequestLog,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink.copy(alpha = 0.9f),
                    contentColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Request Log")
            }

            Button(
                onClick = onOpenBenefitsTracker,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple.copy(alpha = 0.82f),
                    contentColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Disability Benefits Tracker")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpeakStrongViewModel.Tone.entries.forEach { tone ->
                    val isSelected = selectedTone == tone
                    Button(
                        onClick = { viewModel.setTone(tone) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                LavenderPurple
                            } else {
                                SoftBlushPink
                            },
                            contentColor = textColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tone.name
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(scripts, key = { it.id }) { script ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlareDay) {
                                NightLavender.copy(alpha = 0.8f)
                            } else {
                                SoftCloudGrey.copy(alpha = 0.8f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = script.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = textColor
                            )
                            val scriptText = when (selectedTone) {
                                SpeakStrongViewModel.Tone.GENTLE -> script.gentleText
                                SpeakStrongViewModel.Tone.DIRECT -> script.directText
                                SpeakStrongViewModel.Tone.FIRM -> script.firmText
                            }
                            Text(
                                text = scriptText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
