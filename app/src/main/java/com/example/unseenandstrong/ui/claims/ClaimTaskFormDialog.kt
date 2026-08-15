package com.example.unseenandstrong.ui.claims

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.claims.DisabilityClaimTaskEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.WarmMistGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimTaskFormDialog(
    claimId: Long,
    existingTask: DisabilityClaimTaskEntity?,
    isFlareDay: Boolean,
    onDismiss: () -> Unit,
    onSave: (DisabilityClaimTaskEntity) -> Unit
) {
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LavenderPurple,
        unfocusedBorderColor = WarmMistGrey,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = LavenderPurple
    )

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var category by remember { mutableStateOf(existingTask?.category ?: "Form") }
    var status by remember { mutableStateOf(existingTask?.status ?: "Not started") }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate) }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var showOptionalDetails by rememberSaveable(existingTask?.id, isFlareDay) {
        mutableStateOf(!isFlareDay || dueDate != null || notes.isNotBlank())
    }

    val categories = listOf("Form", "Medical documentation", "Employer documentation", "Insurance or administrator", "Phone call", "Appeal", "Other")
    val statuses = listOf("Not started", "In progress", "Submitted", "Complete", "Not needed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingTask == null) "Add Task" else "Edit Task", color = textColor) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )

                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        colors = colors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                var statExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = statExpanded,
                    onExpandedChange = { statExpanded = !statExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statExpanded) },
                        colors = colors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statExpanded,
                        onDismissRequest = { statExpanded = false }
                    ) {
                        statuses.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    status = selectionOption
                                    statExpanded = false
                                }
                            )
                        }
                    }
                }

                if (showOptionalDetails) {
                    DatePickerField("Due Date", dueDate, isFlareDay, onDateSelected = { dueDate = it })
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                } else {
                    TextButton(
                        onClick = { showOptionalDetails = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add due date or notes", color = SoftBlushPink)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val completedDate = if (status == "Complete" && existingTask?.status != "Complete") {
                        System.currentTimeMillis()
                    } else if (status == "Complete") {
                        existingTask?.completedDate ?: System.currentTimeMillis()
                    } else {
                        existingTask?.completedDate
                    }
                    
                    val task = existingTask?.copy(
                        title = title,
                        category = category,
                        status = status,
                        dueDate = dueDate,
                        notes = notes,
                        completedDate = completedDate,
                        updatedAt = System.currentTimeMillis()
                    ) ?: DisabilityClaimTaskEntity(
                        claimId = claimId,
                        title = title,
                        category = category,
                        status = status,
                        dueDate = dueDate,
                        notes = notes,
                        completedDate = completedDate
                    )
                    onSave(task)
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save", color = LavenderPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
