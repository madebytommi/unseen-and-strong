package com.example.unseenandstrong.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.routine.RoutineTaskEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.launch

@Composable
fun RoutineScreen(
    tasks: List<RoutineTaskEntity>,
    onToggleTask: (RoutineTaskEntity) -> Unit,
    onAddTask: (String) -> Unit = {},
    isFlareDay: Boolean = false
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val contrastTextColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey

    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
            if (!isFlareDay) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = LavenderPurple,
                    contentColor = PaleCloudWhite
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Goal")
                }
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
                text = "Gentle Routine Builder",
                style = MaterialTheme.typography.headlineSmall,
                color = contrastTextColor
            )
            Text(
                text = "Every little bit counts. No pressure.",
                style = MaterialTheme.typography.bodyLarge,
                color = contrastTextColor
            )

            if (isFlareDay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Flare Day Mode is active. Your only goal today is to rest and survive. Permission to do absolutely nothing is granted.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = contrastTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (tasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nothing yet. Even planning is a win.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = contrastTextColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        items(tasks, key = { it.id }) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = SoftCloudGrey.copy(alpha = 0.85f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { onToggleTask(task) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = LavenderPurple,
                                            checkmarkColor = DeepFogGrey,
                                            uncheckedColor = DeepFogGrey,
                                            disabledUncheckedColor = DeepFogGrey.copy(alpha = 0.4f)
                                        )
                                    )
                                    Text(
                                        text = task.taskName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DeepFogGrey,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    newTaskName = ""
                },
                containerColor = SoftCloudGrey,
                title = {
                    Text(
                        text = "Add a Tiny Goal",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepFogGrey
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newTaskName,
                        onValueChange = { newTaskName = it },
                        placeholder = { Text("e.g. Empty dishwasher") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftBlushPink,
                            unfocusedBorderColor = LavenderPurple,
                            focusedTextColor = DeepFogGrey,
                            unfocusedTextColor = DeepFogGrey
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskName.isNotBlank()) {
                                onAddTask(newTaskName.trim())
                                showAddDialog = false
                                newTaskName = ""
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Saved. Your tiny goal is ready.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPurple)
                    ) {
                        Text("Save", color = PaleCloudWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        newTaskName = ""
                    }) {
                        Text("Cancel", color = SoftBlushPink)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineScreenPreview() {
    RoutineScreen(
        tasks = listOf(
            RoutineTaskEntity(id = 1, taskName = "Drink water", isCompleted = false),
            RoutineTaskEntity(id = 2, taskName = "Take meds", isCompleted = true)
        ),
        onToggleTask = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun RoutineScreenFlareDayPreview() {
    RoutineScreen(
        tasks = emptyList(),
        onToggleTask = {},
        isFlareDay = true
    )
}
