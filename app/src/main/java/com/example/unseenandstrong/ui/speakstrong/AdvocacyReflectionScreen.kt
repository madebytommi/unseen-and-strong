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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.ui.benefits.DeadlineDateUtils
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.RoseGlow
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val conversationOptions = listOf("Yes", "No", "Not yet", "Prefer not to say")
private val goalResultOptions = listOf("Yes", "Partly", "Not yet", "Prefer not to say")

@Composable
fun AdvocacyReflectionScreen(
    session: AdvocacySessionEntity,
    isFlareDay: Boolean,
    onBackToPlans: () -> Unit,
    onSave: (AdvocacyReflectionInput, () -> Unit) -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    var conversationHappened by rememberSaveable(session.id) { mutableStateOf(session.conversationHappened) }
    var outcomeSummary by rememberSaveable(session.id) { mutableStateOf(session.outcomeSummary) }
    var emotionalReflection by rememberSaveable(session.id) { mutableStateOf(session.emotionalReflection) }
    var goalResult by rememberSaveable(session.id) { mutableStateOf(session.goalResult) }
    var needsFollowUp by rememberSaveable(session.id) {
        mutableStateOf(session.needsFollowUp || session.mayNeedFollowUp)
    }
    var followUpDate by rememberSaveable(session.id) { mutableStateOf(session.followUpDate) }
    var reflectionNote by rememberSaveable(session.id) { mutableStateOf(session.reflectionNote) }
    var reflectionComplete by rememberSaveable(session.id) { mutableStateOf(session.reflectionComplete) }
    var exportToInteractionLog by rememberSaveable(session.id) { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var savedMessage by rememberSaveable(session.id) { mutableStateOf(false) }
    val alreadyLinked = session.linkedInteractionId != null
    var showOptionalReflectionDetails by rememberSaveable(session.id, isFlareDay) {
        mutableStateOf(!isFlareDay || reflectionNote.isNotBlank() || alreadyLinked)
    }

    fun currentInput() = AdvocacyReflectionInput(
        conversationHappened = conversationHappened,
        outcomeSummary = outcomeSummary,
        emotionalReflection = emotionalReflection,
        goalResult = goalResult,
        needsFollowUp = needsFollowUp,
        followUpDate = if (needsFollowUp) followUpDate else null,
        reflectionNote = reflectionNote,
        reflectionComplete = reflectionComplete,
        exportToInteractionLog = exportToInteractionLog
    )

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TextButton(onClick = onBackToPlans) {
                    Text("Back to saved plans", color = SoftBlushPink)
                }
            }
            item {
                Text("After the conversation", style = MaterialTheme.typography.headlineMedium, color = textColor)
            }
            item {
                Text(
                    "The conversation does not have to go perfectly to matter. Record only what feels useful.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
            item {
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
                            "Preparation",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isFlareDay) SoftBlushPink else DustyMauve
                        )
                        Text(session.scriptTitle, style = MaterialTheme.typography.titleLarge, color = textColor)
                        if (session.desiredOutcome.isNotBlank()) {
                            Text("Needed: ${session.desiredOutcome}", color = textColor)
                        }
                        if (session.smallGoal.isNotBlank()) {
                            Text("Small goal: ${session.smallGoal}", color = textColor)
                        }
                    }
                }
            }
            item {
                Text("Did the conversation happen?", style = MaterialTheme.typography.titleMedium, color = textColor)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(conversationOptions) { option ->
                        FilterChip(
                            selected = conversationHappened == option,
                            onClick = { conversationHappened = option; savedMessage = false },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }
            }
            item {
                AdvocacyTextField(
                    outcomeSummary,
                    { outcomeSummary = it; savedMessage = false },
                    "What happened?",
                    textColor,
                    minLines = 3
                )
            }
            item {
                AdvocacyTextField(
                    emotionalReflection,
                    { emotionalReflection = it; savedMessage = false },
                    "How do I feel about the interaction?",
                    textColor,
                    minLines = 2
                )
            }
            item {
                Text("Was the original goal met?", style = MaterialTheme.typography.titleMedium, color = textColor)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(goalResultOptions) { option ->
                        FilterChip(
                            selected = goalResult == option,
                            onClick = { goalResult = option; savedMessage = false },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }
            }
            if (!isFlareDay) {
                item {
                    Text(
                        "Partly meeting your goal still gives you useful information.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Follow-up is needed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = needsFollowUp,
                        onCheckedChange = { needsFollowUp = it; savedMessage = false },
                        modifier = Modifier.semantics {
                            stateDescription = if (needsFollowUp) "On" else "Off"
                        }
                    )
                }
            }
            if (needsFollowUp) {
                item {
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftBlushPink,
                            contentColor = NightLavender
                        )
                    ) {
                        Text(if (followUpDate == null) "Choose follow-up date" else "Change follow-up date")
                    }
                }
                followUpDate?.let { date ->
                    item {
                        Text(
                            "Follow up: ${formatReflectionDate(date)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }
                    item {
                        TextButton(
                            onClick = { followUpDate = null; savedMessage = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Clear follow-up date", color = SoftBlushPink) }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "I consider this reflection complete",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = reflectionComplete,
                        onCheckedChange = { reflectionComplete = it; savedMessage = false },
                        modifier = Modifier.semantics {
                            stateDescription = if (reflectionComplete) "On" else "Off"
                        }
                    )
                }
            }
            if (!showOptionalReflectionDetails) {
                item {
                    TextButton(
                        onClick = { showOptionalReflectionDetails = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add optional reflection details", color = SoftBlushPink)
                    }
                }
            } else {
                item {
                    AdvocacyTextField(
                        reflectionNote,
                        { reflectionNote = it; savedMessage = false },
                        "Private notes (optional)",
                        textColor,
                        minLines = 3
                    )
                }
                item {
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Also save this to Interaction Log",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = alreadyLinked || exportToInteractionLog,
                                onCheckedChange = { exportToInteractionLog = it; savedMessage = false },
                                enabled = !alreadyLinked,
                                modifier = Modifier.semantics {
                                    stateDescription = if (alreadyLinked || exportToInteractionLog) "On" else "Off"
                                }
                            )
                        }
                        Text(
                            if (alreadyLinked) {
                                "This reflection is already linked. Saving will update the existing Interaction Log entry instead of creating another one."
                            } else {
                                "This is optional and remains off unless you choose it."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor
                        )
                    }
                }
            }
            }
            item {
                Button(
                    onClick = { onSave(currentInput()) { savedMessage = true } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPurple,
                        contentColor = NightLavender
                    )
                ) { Text("Save reflection") }
            }
            if (savedMessage) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RoseGlow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Reflection saved. Be gentle with yourself after a hard conversation.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NightLavender,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        AdvocacyDatePickerDialog(
            initialDateMillis = followUpDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                followUpDate = it
                showDatePicker = false
                savedMessage = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvocacyDatePickerDialog(
    initialDateMillis: Long?,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = DeadlineDateUtils.toPickerUtcMillis(initialDateMillis)
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        onDateSelected(DeadlineDateUtils.fromPickerUtcMillis(it))
                    }
                },
                enabled = state.selectedDateMillis != null
            ) { Text("Use date") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state, showModeToggle = false)
    }
}

private fun formatReflectionDate(millis: Long): String =
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
