package com.example.unseenandstrong.ui.speakstrong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.WarmMistGrey

@Composable
fun AdvocacyPreparationScreen(
    session: AdvocacySessionEntity,
    isFlareDay: Boolean,
    onBackToPlans: () -> Unit,
    onSave: (AdvocacyPreparationInput, () -> Unit) -> Unit,
    onContinueToReflection: () -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    var personName by rememberSaveable(session.id) { mutableStateOf(session.personName) }
    var organization by rememberSaveable(session.id) { mutableStateOf(session.organization) }
    var desiredOutcome by rememberSaveable(session.id) { mutableStateOf(session.desiredOutcome) }
    var smallGoal by rememberSaveable(session.id) { mutableStateOf(session.smallGoal) }
    var preparationNote by rememberSaveable(session.id) { mutableStateOf(session.preparationNote) }
    var mayNeedFollowUp by rememberSaveable(session.id) { mutableStateOf(session.mayNeedFollowUp) }
    var savedMessage by rememberSaveable(session.id) { mutableStateOf(false) }
    var showOptionalDetails by rememberSaveable(session.id, isFlareDay) {
        mutableStateOf(
            !isFlareDay || personName.isNotBlank() || organization.isNotBlank() ||
                preparationNote.isNotBlank()
        )
    }

    fun currentInput() = AdvocacyPreparationInput(
        personName = personName,
        organization = organization,
        desiredOutcome = desiredOutcome,
        smallGoal = smallGoal,
        preparationNote = preparationNote,
        mayNeedFollowUp = mayNeedFollowUp
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
                Text("Before the conversation", style = MaterialTheme.typography.headlineMedium, color = textColor)
            }
            item {
                Text(
                    "Preparing is a form of taking care of yourself. Fill in only what feels useful.",
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
                        Text(session.scriptTitle, style = MaterialTheme.typography.titleLarge, color = textColor)
                        Text(
                            "${session.scriptCategory} • ${session.selectedTone.lowercase().replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isFlareDay) SoftBlushPink else DustyMauve
                        )
                        Text(session.scriptTextSnapshot, style = MaterialTheme.typography.bodyLarge, color = textColor)
                    }
                }
            }
            if (showOptionalDetails) {
                item {
                    AdvocacyTextField(personName, { personName = it; savedMessage = false }, "Person (optional)", textColor)
                }
                item {
                    AdvocacyTextField(organization, { organization = it; savedMessage = false }, "Organization (optional)", textColor)
                }
            }
            item {
                AdvocacyTextField(
                    desiredOutcome,
                    { desiredOutcome = it; savedMessage = false },
                    "What do I need from this conversation?",
                    textColor
                )
            }
            item {
                AdvocacyTextField(
                    smallGoal,
                    { smallGoal = it; savedMessage = false },
                    "My smallest useful goal",
                    textColor
                )
            }
            if (showOptionalDetails) {
                item {
                    AdvocacyTextField(
                        preparationNote,
                        { preparationNote = it; savedMessage = false },
                        "What do I want to remember? (optional)",
                        textColor,
                        minLines = 3
                    )
                }
            } else {
                item {
                    TextButton(
                        onClick = { showOptionalDetails = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add optional details", color = SoftBlushPink)
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
                        "I may need to follow up",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = mayNeedFollowUp,
                        onCheckedChange = { mayNeedFollowUp = it; savedMessage = false },
                        modifier = Modifier.semantics {
                            stateDescription = if (mayNeedFollowUp) "On" else "Off"
                        }
                    )
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
                ) { Text("Save preparation") }
            }
            if (savedMessage) {
                item {
                    Text(
                        "Saved. You can return to this when you have more energy.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }
            }
            item {
                Button(
                    onClick = { onSave(currentInput()) { onContinueToReflection() } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = NightLavender
                    )
                ) { Text("Continue to after-conversation reflection") }
            }
        }
    }
}

@Composable
internal fun AdvocacyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    textColor: Color,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SoftBlushPink,
            unfocusedBorderColor = WarmMistGrey,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
