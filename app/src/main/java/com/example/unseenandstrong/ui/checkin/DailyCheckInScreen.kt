package com.example.unseenandstrong.ui.checkin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun DailyCheckInScreen(
    isFlareDay: Boolean = false,
    onSave: (painLevel: Int, spoonsLevel: Int, mood: String) -> Unit
) {
    var painLevel by remember { mutableStateOf(5f) }
    var spoonsLevel by remember { mutableStateOf(5f) }
    var mood by remember { mutableStateOf("") }

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val contrastTextColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                    Text(
                        text = if (isFlareDay) "Gentle Check-in" else "Daily Check-in",
                        style = MaterialTheme.typography.headlineSmall,
                        color = contrastTextColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isFlareDay) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = LavenderPurple.copy(alpha = 0.28f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        ) {
                            Text(
                                text = "Only the basics today. Notice pain, notice energy, save it, and let that be enough.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = contrastTextColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Pain Level",
                        style = MaterialTheme.typography.bodyLarge,
                        color = contrastTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = painLevel,
                        onValueChange = { painLevel = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = SoftBlushPink,
                            activeTrackColor = LavenderPurple,
                            inactiveTrackColor = contrastTextColor.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "${painLevel.toInt()}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contrastTextColor,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Energy (Spoons)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = contrastTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Slider(
                        value = spoonsLevel,
                        onValueChange = { spoonsLevel = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = LavenderPurple,
                            activeTrackColor = SoftBlushPink,
                            inactiveTrackColor = contrastTextColor.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "${spoonsLevel.toInt()}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contrastTextColor,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    if (!isFlareDay) {
                        Text(
                            text = "Mood",
                            style = MaterialTheme.typography.bodyLarge,
                            color = contrastTextColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = mood,
                            onValueChange = { mood = it },
                            label = { Text("How are you feeling?", color = contrastTextColor) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LavenderPurple,
                                unfocusedBorderColor = SoftBlushPink,
                                cursorColor = contrastTextColor,
                                focusedTextColor = contrastTextColor,
                                unfocusedTextColor = contrastTextColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val savedMood = if (isFlareDay && mood.isBlank()) {
                                "Flare day check-in"
                            } else {
                                mood
                            }
                            onSave(painLevel.toInt(), spoonsLevel.toInt(), savedMood)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (isFlareDay) {
                                        "Saved. Rest counts."
                                    } else {
                                        "Day saved. Thank you for checking in."
                                    },
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftBlushPink,
                            contentColor = DeepFogGrey
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isFlareDay) "Save gentle check-in" else "Save my day",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SoftCloudGrey,
                    contentColor = DeepFogGrey
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyCheckInScreenPreview() {
    DailyCheckInScreen(onSave = { _, _, _ -> })
}

@Preview(showBackground = true)
@Composable
fun DailyCheckInScreenFlareDayPreview() {
    DailyCheckInScreen(isFlareDay = true, onSave = { _, _, _ -> })
}
