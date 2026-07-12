package com.example.unseenandstrong.ui.checkin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.delay

@Composable
fun DailyCheckInScreen(
    isFlareDay: Boolean = false,
    onSave: (painLevel: Int, spoonsLevel: Int, mood: String) -> Unit
) {
    var painLevel by remember { mutableStateOf(5f) }
    var spoonsLevel by remember { mutableStateOf(5f) }
    var mood by remember { mutableStateOf("") }
    var showSavedOverlay by remember { mutableStateOf(false) }
    var saveConfirmationToken by remember { mutableStateOf(0) }

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val contrastTextColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey

    LaunchedEffect(saveConfirmationToken) {
        if (saveConfirmationToken > 0) {
            showSavedOverlay = true
            delay(2200)
            showSavedOverlay = false
        }
    }

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
                        saveConfirmationToken += 1
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

        AnimatedVisibility(
            visible = showSavedOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlareDay) {
                        NightLavender.copy(alpha = 0.92f)
                    } else {
                        SoftCloudGrey
                    }
                ),
                modifier = Modifier.padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = "Saved. Thank you for checking in.",
                    color = contrastTextColor,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
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
