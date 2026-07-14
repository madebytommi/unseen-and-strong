package com.example.unseenandstrong.ui.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.example.unseenandstrong.data.local.medication.MedicationEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.launch

@Composable
fun MedicationTrackerScreen(
    viewModel: MedicationViewModel,
    isFlareDay: Boolean = false
) {
    val activeDailyMedications by viewModel.activeDailyMedications.collectAsState()
    val activePRNMedications by viewModel.activePRNMedications.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey
    val allActiveMedications = activeDailyMedications + activePRNMedications

    var showAddDialog by remember { mutableStateOf(false) }
    var showPRNDialog by remember { mutableStateOf(false) }
    var showReactionDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var isPRN by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(showAddDialog) {
        if (!showAddDialog) {
            name = ""
            dosage = ""
            frequency = ""
            instructions = ""
            isPRN = false
            isActive = true
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SoftCloudGrey,
                    contentColor = DeepFogGrey
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LavenderPurple,
                contentColor = textColor
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Medication")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Medication Tracker",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
            Text(
                text = "A calm place to keep track of what supports you today.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Today's Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )

                    if (activeDailyMedications.isEmpty()) {
                        Text(
                            text = "No daily medications have been added yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            activeDailyMedications.forEach { medication ->
                                MedicationScheduleCard(
                                    medication = medication,
                                    textColor = textColor,
                                    cardColor = cardColor,
                                    onTaken = {
                                        viewModel.logDailyMedStatus(
                                            medId = medication.id,
                                            scheduledTime = System.currentTimeMillis(),
                                            actualTakenTime = System.currentTimeMillis(),
                                            status = "Taken"
                                        )
                                    },
                                    onSkipped = {
                                        viewModel.logDailyMedStatus(
                                            medId = medication.id,
                                            scheduledTime = System.currentTimeMillis(),
                                            actualTakenTime = null,
                                            status = "Skipped"
                                        )
                                    },
                                    onDelayed = {
                                        viewModel.logDailyMedStatus(
                                            medId = medication.id,
                                            scheduledTime = System.currentTimeMillis(),
                                            actualTakenTime = null,
                                            status = "Delayed"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (activePRNMedications.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "As Needed",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor
                        )
                        Text(
                            text = "${activePRNMedications.size} PRN medication${if (activePRNMedications.size == 1) "" else "s"} available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
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
                        text = "More Tracking",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showPRNDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LavenderPurple,
                                contentColor = textColor
                            )
                        ) {
                            Text("Log PRN")
                        }
                        Button(
                            onClick = { showReactionDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftBlushPink,
                                contentColor = textColor
                            )
                        ) {
                            Text("Log Reaction")
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = SoftCloudGrey,
                title = {
                    Text(
                        text = "Add New Medication",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            colors = outlinedColors()
                        )
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosage") },
                            colors = outlinedColors()
                        )
                        OutlinedTextField(
                            value = frequency,
                            onValueChange = { frequency = it },
                            label = { Text("Frequency") },
                            colors = outlinedColors()
                        )
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Instructions") },
                            colors = outlinedColors()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "As needed (PRN)",
                                color = DeepFogGrey,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = isPRN,
                                onCheckedChange = { isPRN = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = LavenderPurple,
                                    checkedTrackColor = LavenderPurple.copy(alpha = 0.45f),
                                    uncheckedThumbColor = SoftBlushPink,
                                    uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.45f)
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active",
                                color = DeepFogGrey,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = isActive,
                                onCheckedChange = { isActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = LavenderPurple,
                                    checkedTrackColor = LavenderPurple.copy(alpha = 0.45f),
                                    uncheckedThumbColor = SoftBlushPink,
                                    uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.45f)
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addMedication(
                                name = name,
                                dosage = dosage,
                                frequency = frequency,
                                instructions = instructions,
                                isPRN = isPRN,
                                isActive = isActive
                            )
                            showAddDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Saved. Your medication was added.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderPurple,
                            contentColor = textColor
                        )
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = SoftBlushPink)
                    }
                }
            )
        }

        if (showPRNDialog) {
            PRNLoggingDialog(
                prnMedications = activePRNMedications,
                textColor = textColor,
                onDismiss = { showPRNDialog = false },
                onSave = { medId, reason, reliefDurationHours, effectivenessRating ->
                    viewModel.logPRNUsage(
                        medId = medId,
                        timeTaken = System.currentTimeMillis(),
                        reason = reason,
                        reliefDurationHours = reliefDurationHours,
                        effectivenessRating = effectivenessRating
                    )
                    showPRNDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Saved. Your PRN note is tucked away.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }

        if (showReactionDialog) {
            ReactionLoggingDialog(
                medications = allActiveMedications,
                textColor = textColor,
                onDismiss = { showReactionDialog = false },
                onSave = { medId, description, severity ->
                    viewModel.logReaction(
                        medId = medId,
                        date = System.currentTimeMillis(),
                        description = description,
                        severity = severity
                    )
                    showReactionDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Saved. Thank you for noting that.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun MedicationScheduleCard(
    medication: MedicationEntity,
    textColor: Color,
    cardColor: Color,
    onTaken: () -> Unit,
    onSkipped: () -> Unit,
    onDelayed: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleSmall,
                color = textColor
            )
            Text(
                text = "${medication.dosage} • ${medication.frequency}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = medication.instructions,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.9f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTaken,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPurple,
                        contentColor = textColor
                    )
                ) {
                    Text("Taken", textAlign = TextAlign.Center)
                }
                Button(
                    onClick = onSkipped,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = textColor
                    )
                ) {
                    Text("Skipped", textAlign = TextAlign.Center)
                }
                Button(
                    onClick = onDelayed,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftCloudGrey,
                        contentColor = textColor
                    )
                ) {
                    Text("Snooze/Delay", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LavenderPurple,
    unfocusedBorderColor = DeepFogGrey.copy(alpha = 0.6f),
    focusedTextColor = DeepFogGrey,
    unfocusedTextColor = DeepFogGrey,
    focusedLabelColor = DeepFogGrey,
    unfocusedLabelColor = DeepFogGrey
)

@Composable
fun PRNLoggingDialog(
    prnMedications: List<MedicationEntity>,
    textColor: Color,
    onDismiss: () -> Unit,
    onSave: (medId: Long, reason: String, reliefDurationHours: Int, effectivenessRating: Int) -> Unit
) {
    var selectedMedicationId by remember {
        mutableStateOf(prnMedications.firstOrNull()?.id ?: 0L)
    }
    var reason by remember { mutableStateOf("") }
    var reliefDurationHours by remember { mutableStateOf("") }
    var effectivenessRating by remember { mutableStateOf(0) }

    LaunchedEffect(prnMedications) {
        if (selectedMedicationId == 0L || prnMedications.none { it.id == selectedMedicationId }) {
            selectedMedicationId = prnMedications.firstOrNull()?.id ?: 0L
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SoftCloudGrey,
        title = {
            Text(
                text = "Log PRN Use",
                color = DeepFogGrey,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (prnMedications.isEmpty()) {
                    Text(
                        text = "No PRN medications are available yet.",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Choose a medication",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prnMedications.forEach { medication ->
                            FilterChip(
                                selected = selectedMedicationId == medication.id,
                                onClick = { selectedMedicationId = medication.id },
                                label = { Text(medication.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = SoftCloudGrey,
                                    labelColor = DeepFogGrey,
                                    selectedContainerColor = LavenderPurple.copy(alpha = 0.45f),
                                    selectedLabelColor = DeepFogGrey
                                )
                            )
                        }
                    }
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason") },
                        colors = outlinedColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reliefDurationHours,
                        onValueChange = { reliefDurationHours = it.filter(Char::isDigit) },
                        label = { Text("Relief Duration (hours)") },
                        colors = outlinedColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Effectiveness Rating",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    RatingIconsRow(
                        rating = effectivenessRating,
                        onRatingSelected = { effectivenessRating = it },
                        selectedColor = LavenderPurple,
                        unselectedColor = DeepFogGrey.copy(alpha = 0.35f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val durationValue = reliefDurationHours.toIntOrNull() ?: 0
                    if (selectedMedicationId != 0L && reason.isNotBlank() && effectivenessRating in 1..5) {
                        onSave(
                            selectedMedicationId,
                            reason.trim(),
                            durationValue,
                            effectivenessRating
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = textColor
                ),
                enabled = prnMedications.isNotEmpty() && reason.isNotBlank() && effectivenessRating in 1..5
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftBlushPink)
            }
        }
    )
}

@Composable
fun ReactionLoggingDialog(
    medications: List<MedicationEntity>,
    textColor: Color,
    onDismiss: () -> Unit,
    onSave: (medId: Long, description: String, severity: Int) -> Unit
) {
    var selectedMedicationId by remember {
        mutableStateOf(medications.firstOrNull()?.id ?: 0L)
    }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(0) }

    LaunchedEffect(medications) {
        if (selectedMedicationId == 0L || medications.none { it.id == selectedMedicationId }) {
            selectedMedicationId = medications.firstOrNull()?.id ?: 0L
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SoftCloudGrey,
        title = {
            Text(
                text = "Log a Reaction",
                color = DeepFogGrey,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (medications.isEmpty()) {
                    Text(
                        text = "No active medications are available yet.",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Choose a medication",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        medications.forEach { medication ->
                            FilterChip(
                                selected = selectedMedicationId == medication.id,
                                onClick = { selectedMedicationId = medication.id },
                                label = { Text(medication.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = SoftCloudGrey,
                                    labelColor = DeepFogGrey,
                                    selectedContainerColor = LavenderPurple.copy(alpha = 0.45f),
                                    selectedLabelColor = DeepFogGrey
                                )
                            )
                        }
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        colors = outlinedColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Severity",
                        color = DeepFogGrey,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    RatingIconsRow(
                        rating = severity,
                        onRatingSelected = { severity = it },
                        selectedColor = SoftBlushPink,
                        unselectedColor = DeepFogGrey.copy(alpha = 0.35f)
                    )
                    if (severity >= 4) {
                        Text(
                            text = "Please remember to mention this to your doctor.",
                            color = DeepFogGrey,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedMedicationId != 0L && description.isNotBlank() && severity in 1..5) {
                        onSave(
                            selectedMedicationId,
                            description.trim(),
                            severity
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = textColor
                ),
                enabled = medications.isNotEmpty() && description.isNotBlank() && severity in 1..5
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftBlushPink)
            }
        }
    )
}

@Composable
private fun RatingIconsRow(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    selectedColor: Color,
    unselectedColor: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { index ->
            Button(
                onClick = { onRatingSelected(index) },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = if (rating >= index) selectedColor else unselectedColor
                ),
                modifier = Modifier
            ) {
                Icon(
                    imageVector = if (rating >= index) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Rating $index"
                )
            }
        }
    }
}
