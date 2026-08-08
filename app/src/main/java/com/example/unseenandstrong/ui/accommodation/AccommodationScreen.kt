package com.example.unseenandstrong.ui.accommodation

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.launch

@Composable
fun AccommodationScreen(
    viewModel: AccommodationViewModel,
    isFlareDay: Boolean = false,
    onBackToHub: () -> Unit = {}
) {
    val managerName by viewModel.managerName.collectAsState()
    val condition by viewModel.condition.collectAsState()
    val requestedAccommodation by viewModel.requestedAccommodation.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val generatedEmail by viewModel.generatedEmail.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SoftCloudGrey,
                    contentColor = textColor
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onBackToHub,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftCloudGrey,
                        contentColor = textColor
                    )
                ) {
                    Text(text = "Back to Speak Strong")
                }

                Text(
                    text = "Accommodation Request Generator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = textColor
                )

                FormField(
                    value = managerName,
                    onValueChange = viewModel::updateManagerName,
                    label = "Manager Name",
                    textColor = textColor,
                    accentColor = LavenderPurple
                )
                FormField(
                    value = condition,
                    onValueChange = viewModel::updateCondition,
                    label = "Condition",
                    textColor = textColor,
                    accentColor = LavenderPurple
                )
                FormField(
                    value = requestedAccommodation,
                    onValueChange = viewModel::updateRequestedAccommodation,
                    label = "Requested Accommodation",
                    textColor = textColor,
                    accentColor = LavenderPurple
                )
                FormField(
                    value = duration,
                    onValueChange = viewModel::updateDuration,
                    label = "Duration",
                    textColor = textColor,
                    accentColor = LavenderPurple
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
                            text = "Generated Email",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor
                        )
                        Text(
                            text = if (generatedEmail.isBlank()) {
                                "Your generated email will appear here as you fill out the form."
                            } else {
                                generatedEmail
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        Button(
                            onClick = {
                                if (generatedEmail.isNotBlank()) {
                                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                                    clipboardManager?.setPrimaryClip(
                                        ClipData.newPlainText("Accommodation Request", generatedEmail)
                                    )
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Copied!")
                                    }
                                }
                            },
                            enabled = generatedEmail.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LavenderPurple,
                                contentColor = textColor
                            )
                        ) {
                            Text(text = "Copy to Clipboard")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    textColor: androidx.compose.ui.graphics.Color,
    accentColor: androidx.compose.ui.graphics.Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = textColor) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = accentColor.copy(alpha = 0.5f),
            cursorColor = textColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun AccommodationScreenPreview() {
    val previewViewModel = AccommodationViewModel().apply {
        updateManagerName("Jordan")
        updateCondition("a chronic health condition")
        updateRequestedAccommodation("a flexible start time")
        updateDuration("the next 3 months")
    }

    AccommodationScreen(
        viewModel = previewViewModel,
        isFlareDay = false
    )
}

