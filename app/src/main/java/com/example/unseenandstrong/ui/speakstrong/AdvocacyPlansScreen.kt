package com.example.unseenandstrong.ui.speakstrong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdvocacyPlansScreen(
    sessions: List<AdvocacySessionEntity>,
    isFlareDay: Boolean,
    onBackToHub: () -> Unit,
    onOpenPreparation: (Long) -> Unit,
    onOpenReflection: (Long) -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TextButton(onClick = onBackToHub) {
                    Text("Back to Speak Strong", color = SoftBlushPink)
                }
            }
            item {
                Text("Saved Advocacy Plans", style = MaterialTheme.typography.headlineMedium, color = textColor)
            }
            item {
                Text(
                    "You can return to these when you have more energy.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
            if (sessions.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "No saved plans yet. Open a script and choose Prepare for the conversation when you are ready.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(sessions, key = { it.id }) { session ->
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
                            Text(session.scriptTitle, style = MaterialTheme.typography.titleLarge, color = textColor)
                            Text(
                                "${session.scriptCategory} • ${session.selectedTone.lowercase().replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFlareDay) SoftBlushPink else LavenderPurple
                            )
                            val target = listOf(session.personName, session.organization)
                                .filter { it.isNotBlank() }
                                .joinToString(" — ")
                            if (target.isNotBlank()) {
                                Text(target, style = MaterialTheme.typography.bodyMedium, color = textColor)
                            }
                            Text(
                                "Updated ${formatAdvocacyDate(session.updatedAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor.copy(alpha = 0.75f)
                            )
                            if (session.reflectionComplete) {
                                Text(
                                    "Reflection marked complete",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isFlareDay) SoftBlushPink else LavenderPurple
                                )
                            }
                            Button(
                                onClick = { onOpenPreparation(session.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftBlushPink,
                                    contentColor = DeepFogGrey
                                )
                            ) { Text("Review preparation") }
                            Button(
                                onClick = { onOpenReflection(session.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LavenderPurple,
                                    contentColor = PaleCloudWhite
                                )
                            ) {
                                Text(if (session.reflectionComplete) "Edit reflection" else "Add reflection")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvocacySelectionFallback(
    message: String,
    isFlareDay: Boolean,
    onBackToHub: () -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(message, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Button(
                onClick = onBackToHub,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = PaleCloudWhite
                )
            ) { Text("Back to Speak Strong") }
        }
    }
}

private fun formatAdvocacyDate(millis: Long): String =
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
