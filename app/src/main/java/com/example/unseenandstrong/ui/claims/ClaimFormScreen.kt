package com.example.unseenandstrong.ui.claims

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.ui.benefits.DeadlineDateUtils
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimFormScreen(
    claim: DisabilityClaimEntity?,
    isFlareDay: Boolean,
    onSave: (DisabilityClaimEntity, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LavenderPurple,
        unfocusedBorderColor = DeepFogGrey,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = LavenderPurple
    )

    var claimType by remember { mutableStateOf(claim?.claimType ?: "STD") }
    var employerName by remember { mutableStateOf(claim?.employerName ?: "") }
    var administratorName by remember { mutableStateOf(claim?.administratorName ?: "") }
    var claimNumber by remember { mutableStateOf(claim?.claimNumber ?: "") }
    var status by remember { mutableStateOf(claim?.status ?: "Preparing") }
    
    var filedDate by remember { mutableStateOf(claim?.filedDate) }
    var leaveStartDate by remember { mutableStateOf(claim?.leaveStartDate) }
    var leaveEndDate by remember { mutableStateOf(claim?.leaveEndDate) }
    var benefitStartDate by remember { mutableStateOf(claim?.benefitStartDate) }
    var benefitEndDate by remember { mutableStateOf(claim?.benefitEndDate) }
    var decisionDate by remember { mutableStateOf(claim?.decisionDate) }
    var appealDeadline by remember { mutableStateOf(claim?.appealDeadline) }
    
    var nextAction by remember { mutableStateOf(claim?.nextAction ?: "") }
    var nextActionDueDate by remember { mutableStateOf(claim?.nextActionDueDate) }
    
    var notes by remember { mutableStateOf(claim?.notes ?: "") }
    var enableRequestLog by remember { mutableStateOf(claim?.linkedRequestId != null) }

    val statusOptions = listOf(
        "Preparing", "Submitted", "Waiting", "More information needed",
        "Approved", "Denied", "Appeal in progress", "Closed"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel", tint = textColor)
                }
                Text(
                    if (claim == null) "New Claim" else "Edit Claim",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Basic Information", style = MaterialTheme.typography.titleMedium, color = textColor)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        FilterChip(
                            selected = claimType == "STD",
                            onClick = { claimType = "STD" },
                            label = { Text("STD") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = PaleCloudWhite
                            )
                        )
                        FilterChip(
                            selected = claimType == "LTD",
                            onClick = { claimType = "LTD" },
                            label = { Text("LTD") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPurple,
                                selectedLabelColor = PaleCloudWhite
                            )
                        )
                    }
                }
                
                item {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = colors,
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        status = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = employerName,
                        onValueChange = { employerName = it },
                        label = { Text("Employer") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = administratorName,
                        onValueChange = { administratorName = it },
                        label = { Text("Administrator (Insurance)") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = claimNumber,
                        onValueChange = { claimNumber = it },
                        label = { Text("Claim Number") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Claim and Leave Dates", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    DatePickerField("Filed Date", filedDate, isFlareDay, onDateSelected = { filedDate = it })
                    DatePickerField("Leave Start", leaveStartDate, isFlareDay, onDateSelected = { leaveStartDate = it })
                    DatePickerField("Leave End", leaveEndDate, isFlareDay, onDateSelected = { leaveEndDate = it })
                    DatePickerField("Benefit Start", benefitStartDate, isFlareDay, onDateSelected = { benefitStartDate = it })
                    DatePickerField("Benefit End", benefitEndDate, isFlareDay, onDateSelected = { benefitEndDate = it })
                    DatePickerField("Decision Date", decisionDate, isFlareDay, onDateSelected = { decisionDate = it })
                    DatePickerField("Appeal Deadline", appealDeadline, isFlareDay, onDateSelected = { appealDeadline = it })
                }

                item {
                    Text("Next Step", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    OutlinedTextField(
                        value = nextAction,
                        onValueChange = { nextAction = it },
                        label = { Text("Next Action") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    DatePickerField("Next Action Due Date", nextActionDueDate, isFlareDay, onDateSelected = { nextActionDueDate = it })
                }
                
                item {
                    Text("Notes", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.padding(top = 16.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Private Notes") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    Text("Request Log Integration", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.padding(top = 16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sync with Request Log", color = textColor)
                            Text(
                                "Creates or updates an entry in the Request Log.",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                        }
                        Switch(
                            checked = enableRequestLog,
                            onCheckedChange = { enableRequestLog = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LavenderPurple,
                                uncheckedThumbColor = SoftBlushPink,
                                checkedTrackColor = LavenderPurple.copy(alpha = 0.45f),
                                uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.45f)
                            )
                        )
                    }
                    if (claim?.linkedRequestId != null) {
                        Text(
                            "This claim is currently linked to the Request Log.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LavenderPurple,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            val entity = claim?.copy(
                                claimType = claimType,
                                employerName = employerName,
                                administratorName = administratorName,
                                claimNumber = claimNumber,
                                status = status,
                                filedDate = filedDate,
                                leaveStartDate = leaveStartDate,
                                leaveEndDate = leaveEndDate,
                                benefitStartDate = benefitStartDate,
                                benefitEndDate = benefitEndDate,
                                decisionDate = decisionDate,
                                appealDeadline = appealDeadline,
                                nextAction = nextAction,
                                nextActionDueDate = nextActionDueDate,
                                notes = notes,
                                updatedAt = System.currentTimeMillis()
                            ) ?: DisabilityClaimEntity(
                                claimType = claimType,
                                employerName = employerName,
                                administratorName = administratorName,
                                claimNumber = claimNumber,
                                status = status,
                                filedDate = filedDate,
                                leaveStartDate = leaveStartDate,
                                leaveEndDate = leaveEndDate,
                                benefitStartDate = benefitStartDate,
                                benefitEndDate = benefitEndDate,
                                decisionDate = decisionDate,
                                appealDeadline = appealDeadline,
                                nextAction = nextAction,
                                nextActionDueDate = nextActionDueDate,
                                notes = notes
                            )
                            onSave(entity, enableRequestLog)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderPurple,
                            contentColor = PaleCloudWhite
                        )
                    ) {
                        Text("Save Claim")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedMillis: Long?,
    isFlareDay: Boolean,
    onDateSelected: (Long?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LavenderPurple,
        unfocusedBorderColor = DeepFogGrey,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor,
        disabledBorderColor = DeepFogGrey,
        disabledLabelColor = textColor
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DeadlineDateUtils.toPickerUtcMillis(selectedMillis)
    )

    OutlinedTextField(
        value = if (selectedMillis != null) DeadlineDateUtils.formatMillisAsDate(selectedMillis) else "Unset",
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = false,
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { showDialog = true },
        trailingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = textColor)
        }
    )
    
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utc ->
                        onDateSelected(DeadlineDateUtils.fromPickerUtcMillis(utc))
                    }
                    showDialog = false
                }) {
                    Text("OK", color = LavenderPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDateSelected(null)
                    showDialog = false
                }) {
                    Text("Clear", color = LavenderPurple)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
