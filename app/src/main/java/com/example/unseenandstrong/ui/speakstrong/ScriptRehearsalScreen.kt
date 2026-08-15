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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun ScriptRehearsalScreen(
    script: ScriptEntity,
    selectedTone: SpeakStrongViewModel.Tone,
    isFlareDay: Boolean,
    onToneChanged: (SpeakStrongViewModel.Tone) -> Unit,
    onBackToHub: () -> Unit,
    onStartPreparation: (String) -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val scriptText = script.textFor(selectedTone)
    val sections = remember(scriptText) { splitScriptSections(scriptText) }
    var focusMode by rememberSaveable(scriptText, isFlareDay) { mutableStateOf(isFlareDay) }
    var sectionIndex by rememberSaveable(scriptText) { mutableIntStateOf(0) }
    var practiced by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TextButton(onClick = onBackToHub) {
                    Text("Back to Speak Strong", color = SoftBlushPink)
                }
            }
            item {
                Text(script.title, style = MaterialTheme.typography.headlineMedium, color = textColor)
            }
            item {
                Text(
                    script.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isFlareDay) SoftBlushPink else LavenderPurple
                )
            }
            item {
                Text(
                    if (isFlareDay) {
                        "One section at a time is enough."
                    } else {
                        "Choose the version that feels most usable today."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SpeakStrongViewModel.Tone.entries) { tone ->
                        FilterChip(
                            selected = tone == selectedTone,
                            onClick = {
                                onToneChanged(tone)
                                sectionIndex = 0
                                practiced = false
                            },
                            label = { Text(tone.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (focusMode) {
                            Text(
                                "Section ${sectionIndex + 1} of ${sections.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFlareDay) SoftBlushPink else DustyMauve
                            )
                            Text(
                                sections[sectionIndex],
                                style = MaterialTheme.typography.headlineSmall,
                                color = textColor
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { sectionIndex = (sectionIndex - 1).coerceAtLeast(0) },
                                    enabled = sectionIndex > 0,
                                    modifier = Modifier.weight(1f)
                                ) { Text("Previous") }
                                Button(
                                    onClick = {
                                        sectionIndex = (sectionIndex + 1).coerceAtMost(sections.lastIndex)
                                    },
                                    enabled = sectionIndex < sections.lastIndex,
                                    modifier = Modifier.weight(1f)
                                ) { Text("Next") }
                            }
                            TextButton(
                                onClick = { sectionIndex = 0 },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Start over") }
                        } else {
                            Text(
                                scriptText,
                                style = MaterialTheme.typography.headlineSmall,
                                color = textColor
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        focusMode = !focusMode
                        sectionIndex = 0
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = NightLavender
                    )
                ) {
                    Text(if (focusMode) "Show full script" else "Practice one section at a time")
                }
            }
            if (!isFlareDay) {
                item {
                    Button(
                        onClick = { practiced = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderPurple,
                            contentColor = NightLavender
                        )
                    ) { Text("I practiced this") }
                }
                if (practiced) {
                    item {
                        Text(
                            "Preparing is a form of taking care of yourself. The conversation does not have to go perfectly to matter.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { onStartPreparation(scriptText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPurple,
                        contentColor = NightLavender
                    )
                ) { Text("Prepare for the conversation") }
            }
        }
    }
}

internal fun splitScriptSections(text: String): List<String> {
    val sections = text.trim()
        .split(Regex("(?<=[.!?])\\s+"))
        .map { it.trim() }
        .filter { it.isNotBlank() }
    return sections.ifEmpty { listOf(text.trim()) }
}
