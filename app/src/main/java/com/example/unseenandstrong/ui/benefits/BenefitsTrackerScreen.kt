package com.example.unseenandstrong.ui.benefits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.ui.theme.ButterflyGlow
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.WarmMistGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BenefitsTrackerScreen(
    isFlareDay: Boolean = false,
    viewModel: BenefitsTrackerViewModel,
    onBackToHub: () -> Unit = {}
) {
    val stages by viewModel.stages.collectAsState()
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val headerTextColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    var selectedStage by remember { mutableStateOf<BenefitsStageEntity?>(null) }

    val approachingDeadlineStage = remember(stages) {
        stages.firstOrNull { stage ->
            val deadline = stage.deadlineDate
            deadline != null &&
                stage.status != "Completed" &&
                DeadlineDateUtils.daysUntil(deadline) in 0..7
        }
    }

    Scaffold(containerColor = backgroundColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = onBackToHub,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink,
                    contentColor = NightLavender
                )
            ) {
                Text("Back to Speak Strong")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "SSDI Benefits Tracker",
                style = MaterialTheme.typography.headlineMedium,
                color = headerTextColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!isFlareDay) {
                Text(
                    text = "Navigating this process takes time. Track your journey softly below.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = headerTextColor
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            approachingDeadlineStage?.let { stage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftBlushPink
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Gentle reminder: You have paperwork due soon for '${stage.stageName}'. Take it one step at a time.",
                        modifier = Modifier.padding(16.dp),
                        color = NightLavender,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (stages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your benefits stages will appear here when they are available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = headerTextColor
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(stages, key = { _, stage -> stage.id }) { index, stage ->
                        JourneyStageItem(
                            stage = stage,
                            isLast = index == stages.lastIndex,
                            isFlareDay = isFlareDay,
                            onClick = { selectedStage = stage }
                        )
                    }
                }
            }

            selectedStage?.let { stage ->
                EditStageDialog(
                    stage = stage,
                    isFlareDay = isFlareDay,
                    onDismiss = { selectedStage = null },
                    onSave = { updatedStage ->
                        viewModel.updateStage(updatedStage)
                        selectedStage = null
                    }
                )
            }
        }
    }
}

@Composable
fun JourneyStageItem(
    stage: BenefitsStageEntity,
    isLast: Boolean,
    isFlareDay: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val statusColor = when (stage.status) {
        "Completed" -> ButterflyGlow
        "Active" -> DustyMauve
        else -> WarmMistGrey
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (stage.status == "Completed") {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = NightLavender,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .width(2.dp)
                ) {
                    drawLine(
                        color = statusColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp, start = 8.dp)
                .clickable(onClickLabel = "Edit ${stage.stageName}") { onClick() },
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stage.stageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${stage.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.8f)
                )
                stage.deadlineDate?.let { deadline ->
                    Text(
                        text = "Deadline: ${formatDeadlineDate(deadline)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (stage.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stage.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStageDialog(
    stage: BenefitsStageEntity,
    isFlareDay: Boolean,
    onDismiss: () -> Unit,
    onSave: (BenefitsStageEntity) -> Unit
) {
    val dialogBg = if (isFlareDay) NightLavender else PaleCloudWhite
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    var status by rememberSaveable(stage.id) { mutableStateOf(stage.status) }
    var notes by rememberSaveable(stage.id) { mutableStateOf(stage.notes) }
    var deadlineDate by remember(stage.id, stage.deadlineDate) {
        mutableStateOf(stage.deadlineDate)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNotes by rememberSaveable(stage.id, isFlareDay) {
        mutableStateOf(!isFlareDay || stage.notes.isNotBlank())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        title = {
            Text(stage.stageName, color = textColor, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Update Status", color = textColor)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Pending", "Active", "Completed")) { option ->
                        FilterChip(
                            selected = status == option,
                            onClick = { status = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = NightLavender
                            )
                        )
                    }
                }

                Text("Deadline", color = textColor)
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PaleCloudWhite,
                        contentColor = NightLavender
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (deadlineDate == null) "Choose deadline" else "Change deadline")
                }

                deadlineDate?.let { selectedDeadline ->
                    Text(
                        text = "Selected: ${formatDeadlineDate(selectedDeadline)}",
                        color = textColor
                    )
                    TextButton(
                        onClick = { deadlineDate = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear deadline", color = SoftBlushPink)
                    }
                }

                if (showNotes) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftBlushPink,
                            unfocusedBorderColor = WarmMistGrey,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TextButton(
                        onClick = { showNotes = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add optional notes", color = SoftBlushPink)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        stage.copy(
                            status = status,
                            notes = notes,
                            deadlineDate = deadlineDate
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = NightLavender
                )
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

    if (showDatePicker) {
        DeadlineDatePickerDialog(
            initialDeadlineMillis = deadlineDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDeadline ->
                deadlineDate = selectedDeadline
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeadlineDatePickerDialog(
    initialDeadlineMillis: Long?,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DeadlineDateUtils.toPickerUtcMillis(initialDeadlineMillis)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                        onDateSelected(
                            DeadlineDateUtils.fromPickerUtcMillis(selectedUtcMillis)
                        )
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Use date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}

private fun formatDeadlineDate(millis: Long): String =
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
