package com.example.unseenandstrong.ui.claims

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimTaskEntity
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import com.example.unseenandstrong.ui.benefits.DeadlineDateUtils
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.WarmMistGrey

@Composable
fun ClaimDetailScreen(
    viewModel: DisabilityClaimViewModel,
    isFlareDay: Boolean,
    onBackToClaims: () -> Unit,
    onEditClaim: (Long) -> Unit,
    onLinkInteraction: () -> Unit,
    onLinkDocument: () -> Unit,
    onOpenInteraction: (Long) -> Unit,
    onOpenDocument: (Long) -> Unit
) {
    val claim by viewModel.selectedClaim.collectAsState()
    val tasks by viewModel.claimTasks.collectAsState()
    val interactions by viewModel.linkedInteractions.collectAsState()
    val documents by viewModel.linkedDocuments.collectAsState()

    val allInteractions by viewModel.allInteractions.collectAsState()
    val allDocuments by viewModel.allDocuments.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<DisabilityClaimTaskEntity?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var showLinkInteractionDialog by remember { mutableStateOf(false) }
    var showLinkDocumentDialog by remember { mutableStateOf(false) }

    if (claim == null) {
        // Handle gracefully
        return
    }

    val currentClaim = claim!!
    var showSecondarySections by rememberSaveable(currentClaim.id, isFlareDay) {
        mutableStateOf(!isFlareDay)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(onClick = onBackToClaims) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to claims",
                        tint = textColor
                    )
                }
                Text(
                    "Claim Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                IconButton(onClick = { onEditClaim(currentClaim.id) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit claim",
                        tint = if (isFlareDay) SoftBlushPink else DustyMauve
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${currentClaim.claimType} Claim",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isFlareDay) SoftBlushPink else LavenderPurple
                            )
                            Text("Status: ${currentClaim.status}", color = textColor)
                            if (currentClaim.employerName.isNotBlank()) Text("Employer: ${currentClaim.employerName}", color = textColor)
                            if (currentClaim.administratorName.isNotBlank()) Text("Administrator: ${currentClaim.administratorName}", color = textColor)
                            if (currentClaim.claimNumber.isNotBlank()) Text("Claim #: ${currentClaim.claimNumber}", color = textColor)
                        }
                    }
                }

                item {
                    val nearestDate = getNearestIncompleteDate(currentClaim, tasks)
                    if (nearestDate != null) {
                        val daysUntil = DeadlineDateUtils.daysUntil(nearestDate)
                        if (daysUntil in 0..7) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SoftBlushPink),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = NightLavender)
                                    Text(
                                        "An important date is coming up on ${DeadlineDateUtils.formatMillisAsDate(nearestDate)}.",
                                        color = NightLavender,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    val dates = listOfNotNull(
                        currentClaim.filedDate?.let { "Filed: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.leaveStartDate?.let { "Leave Start: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.leaveEndDate?.let { "Leave End: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.benefitStartDate?.let { "Benefit Start: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.benefitEndDate?.let { "Benefit End: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.decisionDate?.let { "Decision: ${DeadlineDateUtils.formatMillisAsDate(it)}" },
                        currentClaim.appealDeadline?.let { "Appeal Deadline: ${DeadlineDateUtils.formatMillisAsDate(it)}" }
                    )
                    
                    if (dates.isNotEmpty()) {
                        Text("Important Dates", style = MaterialTheme.typography.titleMedium, color = textColor)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                dates.forEach { d ->
                                    Text(d, color = textColor, modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                if (currentClaim.nextAction.isNotBlank() || currentClaim.nextActionDueDate != null) {
                    item {
                        Text("Next Action", style = MaterialTheme.typography.titleMedium, color = textColor)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (currentClaim.nextAction.isNotBlank()) Text(currentClaim.nextAction, color = textColor)
                                currentClaim.nextActionDueDate?.let { d ->
                                    Text("Due: ${DeadlineDateUtils.formatMillisAsDate(d)}", color = textColor, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Checklist", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            taskToEdit = null
                            showTaskDialog = true
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add task",
                                tint = if (isFlareDay) SoftBlushPink else DustyMauve
                            )
                        }
                    }
                }

                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        isFlareDay = isFlareDay,
                        onToggle = { viewModel.toggleTaskComplete(task) },
                        onEdit = {
                            taskToEdit = task
                            showTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }

                if (!showSecondarySections) {
                    item {
                        TextButton(
                            onClick = { showSecondarySections = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show linked items and notes", color = SoftBlushPink)
                        }
                    }
                } else {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Linked Interactions", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.weight(1f))
                        IconButton(onClick = { showLinkInteractionDialog = true }) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = "Link interaction",
                                tint = if (isFlareDay) SoftBlushPink else DustyMauve
                            )
                        }
                    }
                }
                
                if (interactions.isEmpty()) {
                    item { Text("No interactions linked.", color = textColor, style = MaterialTheme.typography.bodyMedium) }
                } else {
                    items(interactions, key = { it.id }) { interaction ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(
                                    onClickLabel = "Open linked interaction",
                                    onClick = { onOpenInteraction(interaction.id) }
                                )
                        ) {
                            Text(
                                interaction.personName.ifBlank { "Unknown" } + " - " + interaction.category,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.unlinkInteraction(interaction.id) }) {
                                Icon(
                                    Icons.Default.LinkOff,
                                    contentDescription = "Unlink interaction with ${interaction.personName.ifBlank { "unknown person" }}",
                                    tint = textColor
                                )
                            }
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Linked Documents", style = MaterialTheme.typography.titleMedium, color = textColor, modifier = Modifier.weight(1f))
                        IconButton(onClick = { showLinkDocumentDialog = true }) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = "Link document",
                                tint = if (isFlareDay) SoftBlushPink else DustyMauve
                            )
                        }
                    }
                }
                
                if (documents.isEmpty()) {
                    item { Text("No documents linked.", color = textColor, style = MaterialTheme.typography.bodyMedium) }
                } else {
                    items(documents, key = { it.id }) { doc ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(
                                    onClickLabel = "Open linked document",
                                    onClick = { onOpenDocument(doc.id) }
                                )
                        ) {
                            Text(
                                doc.title.ifBlank { "Document" } + " (${doc.category})",
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.unlinkDocument(doc.id) }) {
                                Icon(
                                    Icons.Default.LinkOff,
                                    contentDescription = "Unlink ${doc.title.ifBlank { "document" }}",
                                    tint = textColor
                                )
                            }
                        }
                    }
                }

                if (currentClaim.notes.isNotBlank()) {
                    item {
                        Text("Notes", style = MaterialTheme.typography.titleMedium, color = textColor)
                        Text(currentClaim.notes, color = textColor, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                item {
                    Button(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = 16.dp)
                    ) {
                        Text("Delete Claim")
                    }
                }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Claim") },
            text = { Text("Are you sure? This will remove claim-specific tasks and links. Original Interaction Log entries, Request Log records, and Vault documents will remain unless separately deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteClaim(currentClaim) {
                        onBackToClaims()
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = LavenderPurple)
                }
            }
        )
    }

    if (showTaskDialog) {
        ClaimTaskFormDialog(
            claimId = currentClaim.id,
            existingTask = taskToEdit,
            isFlareDay = isFlareDay,
            onDismiss = { showTaskDialog = false },
            onSave = { task ->
                viewModel.saveTask(task)
                showTaskDialog = false
            }
        )
    }

    if (showLinkInteractionDialog) {
        val linkedIds = interactions.map { it.id }.toSet()
        val available = allInteractions.filter { it.id !in linkedIds }
        AlertDialog(
            onDismissRequest = { showLinkInteractionDialog = false },
            title = { Text("Link Interaction", color = textColor) },
            text = {
                if (available.isEmpty()) {
                    Text("No interactions available to link.", color = textColor)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(available, key = { it.id }) { interaction ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.linkInteraction(interaction.id)
                                        showLinkInteractionDialog = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = PaleCloudWhite)
                            ) {
                                Text(
                                    text = interaction.personName.ifBlank { "Unknown" } + " - " + interaction.category,
                                    modifier = Modifier.padding(16.dp),
                                    color = NightLavender
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLinkInteractionDialog = false }) {
                    Text("Close", color = LavenderPurple)
                }
            }
        )
    }

    if (showLinkDocumentDialog) {
        val linkedIds = documents.map { it.id }.toSet()
        val available = allDocuments.filter { it.id !in linkedIds }.sortedBy { 
            when (it.category) {
                "Work", "Insurance", "Medical" -> 0
                else -> 1
            }
        }
        AlertDialog(
            onDismissRequest = { showLinkDocumentDialog = false },
            title = { Text("Link Document", color = textColor) },
            text = {
                if (available.isEmpty()) {
                    Text("No documents available to link.", color = textColor)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(available, key = { it.id }) { doc ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.linkDocument(doc.id)
                                        showLinkDocumentDialog = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = PaleCloudWhite)
                            ) {
                                Text(
                                    text = doc.title.ifBlank { "Document" } + " (${doc.category})",
                                    modifier = Modifier.padding(16.dp),
                                    color = NightLavender
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLinkDocumentDialog = false }) {
                    Text("Close", color = LavenderPurple)
                }
            }
        )
    }
}

fun getNearestIncompleteDate(claim: DisabilityClaimEntity, tasks: List<DisabilityClaimTaskEntity>): Long? {
    val dates = mutableListOf<Long>()
    claim.nextActionDueDate?.let { dates.add(it) }
    claim.appealDeadline?.let { dates.add(it) }
    claim.benefitEndDate?.let { dates.add(it) }
    claim.benefitStartDate?.let { dates.add(it) }
    claim.leaveEndDate?.let { dates.add(it) }
    claim.leaveStartDate?.let { dates.add(it) }

    tasks.forEach {
        if (it.status != "Complete" && it.status != "Not needed" && it.dueDate != null) {
            dates.add(it.dueDate)
        }
    }
    
    val now = System.currentTimeMillis()
    return dates.filter { it >= now - 86400000 }.minOrNull() // ignore deeply past dates if needed, or just min
}

@Composable
fun TaskRow(
    task: DisabilityClaimTaskEntity,
    isFlareDay: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val isComplete = task.status == "Complete"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isComplete,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = LavenderPurple,
                uncheckedColor = if (isFlareDay) WarmMistGrey else DeepFogGrey
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .clickable(onClickLabel = "Edit ${task.title}", onClick = onEdit)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (isComplete) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = textColor
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFlareDay) SoftBlushPink else DeepFogGrey
                )
                if (task.dueDate != null && !isComplete) {
                    Text(
                        text = "Due: ${DeadlineDateUtils.formatMillisAsDate(task.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete ${task.title}",
                tint = textColor
            )
        }
    }
}
