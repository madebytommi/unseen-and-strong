package com.example.unseenandstrong.ui.vault

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.WarmMistGrey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun VaultScreen(
    viewModel: VaultViewModel,
    isFlareDay: Boolean = false,
    documentUriToOpen: String? = null,
    onBackToHub: (() -> Unit)? = null,
    onDocumentOpenHandled: () -> Unit = {}
) {
    val documents by viewModel.documents.collectAsState()
    val context = LocalContext.current
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey

    var showSaveDialog by remember { mutableStateOf(false) }
    var documentPendingDeletion by remember { mutableStateOf<VaultDocumentEntity?>(null) }
    var selectedUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pendingMessage) {
        pendingMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            pendingMessage = null
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            when (persistVaultReadPermission(context.contentResolver, uri)) {
                VaultDocumentPersistence.PERSISTED -> {
                    selectedUri = uri.toString()
                    showSaveDialog = true
                }
                VaultDocumentPersistence.UNAVAILABLE -> {
                    pendingMessage =
                        "This source could not provide lasting read access. Nothing was saved. Try choosing the file from a device document provider."
                }
            }
        }
    }

    LaunchedEffect(documentUriToOpen) {
        val rawUri = documentUriToOpen ?: return@LaunchedEffect
        val result = openVaultDocument(context, rawUri)
        vaultDocumentOpenMessage(result)?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
        onDocumentOpenHandled()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    documentPickerLauncher.launch(arrayOf("image/*"))
                },
                containerColor = LavenderPurple,
                contentColor = NightLavender
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Add photo document"
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (onBackToHub != null) {
                        IconButton(onClick = onBackToHub) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Speak Strong",
                                tint = textColor
                            )
                        }
                    }
                    Text(
                        text = "Document Vault",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        modifier = Modifier.padding(start = if (onBackToHub != null) 8.dp else 8.dp)
                    )
                }

                if (documents.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No documents saved yet.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = textColor
                        )
                        Text(
                            text = "Tap the + button to add a medical, insurance, or work document.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(documents, key = { it.id }) { document ->
                            VaultDocumentCard(
                                document = document,
                                textColor = textColor,
                                backgroundColor = cardColor,
                                showSecondaryMetadata = !isFlareDay,
                                onOpen = {
                                    val result = openVaultDocument(context, document.fileUri)
                                    vaultDocumentOpenMessage(result)?.let { message ->
                                        pendingMessage = message
                                    }
                                },
                                onDelete = { documentPendingDeletion = document }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog && selectedUri != null) {
        SaveVaultDocumentDialog(
            textColor = textColor,
            isFlareDay = isFlareDay,
            onDismiss = {
                showSaveDialog = false
                selectedUri = null
            },
            onSave = { title, category ->
                viewModel.saveDocument(
                    title = title,
                    category = category,
                    fileUri = selectedUri.orEmpty()
                )
                showSaveDialog = false
                selectedUri = null
            }
        )
    }

    documentPendingDeletion?.let { document ->
        AlertDialog(
            onDismissRequest = { documentPendingDeletion = null },
            title = { Text("Delete document?", color = textColor) },
            text = {
                Text(
                    text = "Remove \"${document.title.ifBlank { "document" }}\" from your Vault? The original file will stay where it is.",
                    color = textColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        documentPendingDeletion = null
                        viewModel.deleteDocument(document)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { documentPendingDeletion = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = NightLavender
                    )
                ) {
                    Text("Cancel")
                }
            },
            containerColor = if (isFlareDay) NightLavender else PaleCloudWhite
        )
    }
}

@Composable
private fun VaultDocumentCard(
    document: VaultDocumentEntity,
    textColor: Color,
    backgroundColor: Color,
    showSecondaryMetadata: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClickLabel = "Open document", onClick = onOpen)
                ) {
                    Icon(
                        imageVector = categoryIcon(document.category),
                        contentDescription = null,
                        tint = lavenderTintForCategory(document.category)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = document.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = textColor
                        )
                        Text(
                            text = document.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = lavenderTintForCategory(document.category)
                        )
                    }
                }

                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Open ${document.title.ifBlank { "document" }}",
                        tint = textColor
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${document.title.ifBlank { "document" }}",
                        tint = textColor
                    )
                }
            }

            if (showSecondaryMetadata) {
                Text(
                    text = formatDateAdded(document.dateAdded),
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )

                Text(
                    text = "Stored locally • Tap the document to open it",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
private fun SaveVaultDocumentDialog(
    textColor: Color,
    isFlareDay: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, category: String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Insurance") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isFlareDay) NightLavender else PaleCloudWhite,
        title = {
            Text(
                text = "Save document",
                color = textColor,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LavenderPurple,
                        unfocusedBorderColor = WarmMistGrey,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Insurance", "Medical", "Work")) { option ->
                        VaultCategoryChip(
                            text = option,
                            selected = category == option,
                            textColor = textColor,
                            onClick = { category = option }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title.trim(), category) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPurple,
                    contentColor = NightLavender
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink,
                    contentColor = NightLavender
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VaultCategoryChip(
    text: String,
    selected: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            selectedContainerColor = LavenderPurple,
            selectedLabelColor = NightLavender,
            labelColor = textColor,
            containerColor = if (textColor == PaleCloudWhite) NightLavender else SoftCloudGrey
        )
    )
}

private fun formatDateAdded(dateAdded: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a")
    return Instant.ofEpochMilli(dateAdded)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun categoryIcon(category: String) = when (category.lowercase()) {
    "insurance" -> Icons.Default.Description
    "medical" -> Icons.Default.Favorite
    "work" -> Icons.Default.Work
    else -> Icons.Default.Folder
}

private fun lavenderTintForCategory(category: String): Color = when (category.lowercase()) {
    "insurance" -> LavenderPurple
    "medical" -> SoftBlushPink
    "work" -> DeepFogGrey
    else -> LavenderPurple
}
