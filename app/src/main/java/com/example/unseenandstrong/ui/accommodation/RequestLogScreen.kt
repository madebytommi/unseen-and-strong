package com.example.unseenandstrong.ui.accommodation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftBorderGray
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestLogScreen(
    isFlareDay: Boolean = false,
    viewModel: RequestLogViewModel,
    onBackToHub: () -> Unit = {}
) {
    val requests by viewModel.requests.collectAsState()
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val headerTextColor = if (isFlareDay) SoftCloudGrey else DeepFogGrey

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LavenderPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add request")
            }
        }
    ) { paddingValues ->
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
                    contentColor = DeepFogGrey
                )
            ) {
                Text("Back to Speak Strong")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Request Log",
                style = MaterialTheme.typography.headlineMedium,
                color = headerTextColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep a gentle record of your FMLA, ADA, and disability requests.",
                style = MaterialTheme.typography.bodyLarge,
                color = headerTextColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No requests logged yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = headerTextColor.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        RequestCard(request = request, isFlareDay = isFlareDay)
                    }
                }
            }
        }

        if (showAddDialog) {
            AddRequestDialog(
                onDismiss = { showAddDialog = false },
                onSave = { type, status, notes ->
                    viewModel.addRequest(type, status, notes)
                    showAddDialog = false
                },
                isFlareDay = isFlareDay
            )
        }
    }
}

@Composable
fun RequestCard(
    request: AccommodationRequestEntity,
    isFlareDay: Boolean
) {
    val cardColor = if (isFlareDay) DeepFogGrey else Color.White
    val textColor = if (isFlareDay) SoftCloudGrey else DeepFogGrey

    val statusColor = when (request.status) {
        "Approved" -> LavenderPurple
        "Needs Info" -> SoftBlushPink
        "Denied" -> DeepFogGrey
        else -> SoftBorderGray
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(2.dp, statusColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.requestType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = request.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val dateString = remember(request.submissionDate) {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(request.submissionDate))
            }
            Text(
                text = "Submitted: $dateString",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.8f)
            )
            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = request.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    isFlareDay: Boolean
) {
    var type by remember { mutableStateOf("FMLA") }
    var status by remember { mutableStateOf("Pending") }
    var notes by remember { mutableStateOf("") }

    val dialogBg = if (isFlareDay) DeepFogGrey else Color.White
    val textColor = if (isFlareDay) SoftCloudGrey else DeepFogGrey

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBg,
        title = {
            Text("Log Request", color = textColor, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = {
                        Text("Request Type (e.g. FMLA, ADA)", color = textColor)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlushPink,
                        unfocusedBorderColor = LavenderPurple,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = {
                        Text(
                            "Status (Pending, Approved, Needs Info)",
                            color = textColor
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlushPink,
                        unfocusedBorderColor = LavenderPurple,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlushPink,
                        unfocusedBorderColor = LavenderPurple,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(type, status, notes) },
                enabled = type.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LavenderPurple)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftBlushPink)
            }
        }
    )
}
