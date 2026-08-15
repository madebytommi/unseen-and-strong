package com.example.unseenandstrong.ui.interaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.ui.theme.ButterflyGlow
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.RoseGlow
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.WarmMistGrey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay

@Composable
fun InteractionScreen(
    viewModel: InteractionViewModel,
    isFlareDay: Boolean = false,
    onValidationCompleteNavigateBack: () -> Unit = {}
) {
    val interactions by viewModel.interactions.collectAsState()
    val showValidationOverlay by viewModel.showValidationOverlay.collectAsState()
    val currentValidationMessage by viewModel.currentValidationMessage.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey
    var showFollowUpOnly by rememberSaveable { mutableStateOf(false) }
    val visibleInteractions = if (showFollowUpOnly) {
        interactions.filter { it.needsFollowUp }
    } else {
        interactions
    }

    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showValidationOverlay) {
        if (showValidationOverlay) {
            delay(3500)
            viewModel.dismissValidationOverlay()
            onValidationCompleteNavigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LavenderPurple,
                contentColor = NightLavender
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add interaction"
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
                    .padding(horizontal = 24.dp)
            ) {
                FilterChip(
                    selected = showFollowUpOnly,
                    onClick = { showFollowUpOnly = !showFollowUpOnly },
                    label = {
                        Text(
                            text = "Follow-up",
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )
                    },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        containerColor = if (isFlareDay) NightLavender else SoftCloudGrey,
                        labelColor = textColor,
                        selectedContainerColor = ButterflyGlow,
                        selectedLabelColor = NightLavender
                    ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )

                if (visibleInteractions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (showFollowUpOnly) {
                                "No follow-ups to review right now."
                            } else {
                                "No interactions logged yet."
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = textColor
                        )
                    }
                } else {
                    InteractionTimelineList(
                        interactions = visibleInteractions,
                        isFlareDay = isFlareDay,
                        cardColor = cardColor,
                        textColor = textColor
                    )
                }

                AnimatedVisibility(
                    visible = showValidationOverlay,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isFlareDay) NightLavender.copy(alpha = 0.68f) else SoftCloudGrey.copy(alpha = 0.72f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = RoseGlow
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "You showed up for yourself",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = NightLavender
                                )
                                Text(
                                    text = currentValidationMessage,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NightLavender
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddInteractionDialog(
            textColor = textColor,
            isFlareDay = isFlareDay,
            onDismiss = { showAddDialog = false },
            onSave = { category, personName, organization, needsFollowUp, followUpDate, notes ->
                viewModel.saveInteraction(
                    category = category,
                    personName = personName,
                    organization = organization,
                    needsFollowUp = needsFollowUp,
                    followUpDate = followUpDate,
                    notes = notes,
                    onSaved = {
                        showAddDialog = false
                    }
                )
            }
        )
    }
}

@Composable
private fun InteractionTimelineList(
    interactions: List<InteractionEntity>,
    isFlareDay: Boolean,
    cardColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    val groupedInteractions = interactions.groupBy { interaction ->
        formatMonthYear(interaction.timestamp)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        groupedInteractions.forEach { (monthHeader, monthItems) ->
            item(key = "header_$monthHeader") {
                Text(
                    text = monthHeader,
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            itemsIndexed(monthItems, key = { _, interaction -> interaction.id }) { index, interaction ->
                InteractionTimelineItem(
                    interaction = interaction,
                    isFirst = index == 0,
                    isLast = index == monthItems.lastIndex,
                    backgroundColor = cardColor,
                    textColor = textColor,
                    nodeColor = if (index % 2 == 0) LavenderPurple else SoftBlushPink,
                    lineColor = DeepFogGrey.copy(alpha = if (isFlareDay) 0.36f else 0.26f)
                )
            }
        }
    }
}

@Composable
private fun InteractionTimelineItem(
    interaction: InteractionEntity,
    isFirst: Boolean,
    isLast: Boolean,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    nodeColor: androidx.compose.ui.graphics.Color,
    lineColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 6.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width(28.dp)
                .fillMaxHeight()
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val nodeRadius = 6.dp.toPx()
            val lineWidth = 2.dp.toPx()

            if (!isFirst) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, centerY - nodeRadius),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }

            if (!isLast) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, centerY + nodeRadius),
                    end = Offset(centerX, size.height),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = nodeColor,
                radius = nodeRadius,
                center = Offset(centerX, centerY)
            )
        }

        InteractionCard(
            interaction = interaction,
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }
}

@Composable
private fun InteractionCard(
    interaction: InteractionEntity,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    val isDueForFollowUp = interaction.needsFollowUp &&
        interaction.followUpDate?.let(::isFollowUpDue) == true

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isDueForFollowUp) {
            BorderStroke(width = 1.dp, color = ButterflyGlow.copy(alpha = 0.9f))
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = interaction.category,
                style = MaterialTheme.typography.labelMedium,
                color = if (textColor == PaleCloudWhite) SoftBlushPink else DeepFogGrey
            )
            Text(
                text = interaction.personName,
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
            if (interaction.organization.isNotBlank()) {
                Text(
                    text = interaction.organization,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            if (interaction.notes.isNotBlank()) {
                Text(
                    text = interaction.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            if (isDueForFollowUp) {
                Text(
                    text = "Ready for follow-up when you have the spoons.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (textColor == PaleCloudWhite) SoftBlushPink else DeepFogGrey
                )
            }
            interaction.followUpDate?.let { followUpDate ->
                Text(
                    text = "Follow up by: ${formatDate(followUpDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (textColor == PaleCloudWhite) SoftBlushPink else DeepFogGrey
                )
            }
            Text(
                text = formatTimestamp(interaction.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AddInteractionDialog(
    textColor: androidx.compose.ui.graphics.Color,
    isFlareDay: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        category: String,
        personName: String,
        organization: String,
        needsFollowUp: Boolean,
        followUpDate: Long?,
        notes: String
    ) -> Unit
) {
    var category by rememberSaveable { mutableStateOf("") }
    var personName by rememberSaveable { mutableStateOf("") }
    var organization by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var needsFollowUp by rememberSaveable { mutableStateOf(false) }
    var selectedFollowUpOption by rememberSaveable { mutableStateOf<FollowUpOption?>(null) }
    var showOptionalDetails by rememberSaveable(isFlareDay) { mutableStateOf(!isFlareDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isFlareDay) NightLavender else PaleCloudWhite,
        title = {
            Text(
                text = "Add Interaction",
                color = textColor,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LavenderPurple,
                        unfocusedBorderColor = WarmMistGrey,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Person", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlushPink,
                        unfocusedBorderColor = WarmMistGrey,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showOptionalDetails) {
                    OutlinedTextField(
                        value = organization,
                        onValueChange = { organization = it },
                        label = { Text("Organization (optional)", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderPurple,
                            unfocusedBorderColor = WarmMistGrey,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            cursorColor = textColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)", color = textColor) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftBlushPink,
                            unfocusedBorderColor = WarmMistGrey,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            cursorColor = textColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TextButton(
                        onClick = { showOptionalDetails = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add optional details", color = SoftBlushPink)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remind me to follow up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Switch(
                        checked = needsFollowUp,
                        onCheckedChange = { checked ->
                            needsFollowUp = checked
                            selectedFollowUpOption = if (checked) {
                                selectedFollowUpOption ?: FollowUpOption.ONE_WEEK
                            } else {
                                null
                            }
                        },
                        modifier = Modifier.semantics {
                            stateDescription = if (needsFollowUp) "On" else "Off"
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LavenderPurple,
                            uncheckedThumbColor = SoftBlushPink,
                            checkedTrackColor = LavenderPurple.copy(alpha = 0.4f),
                            uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.4f)
                        )
                    )
                }

                if (needsFollowUp) {
                    Text(
                        text = "Choose a gentle follow-up window",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.85f)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FollowUpOption.entries) { option ->
                            val isSelected = selectedFollowUpOption == option
                            Button(
                                onClick = { selectedFollowUpOption = option },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) {
                                        LavenderPurple
                                    } else {
                                        SoftBlushPink.copy(alpha = 0.55f)
                                    },
                                    contentColor = NightLavender
                                )
                            ) {
                                Text(text = option.label)
                            }
                        }
                    }

                    selectedFollowUpOption?.let { option ->
                        Text(
                            text = "Follow-up target: ${formatDate(option.toEpochMillis())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val followUpDate = if (needsFollowUp) {
                            (selectedFollowUpOption ?: FollowUpOption.ONE_WEEK).toEpochMillis()
                        } else {
                            null
                        }
                        onSave(category, personName, organization, needsFollowUp, followUpDate, notes)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPurple,
                        contentColor = NightLavender
                    )
                ) {
                    Text("Save")
                }
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

private enum class FollowUpOption(val label: String, private val amount: Long, private val unit: ChronoUnit) {
    ONE_WEEK("In 1 week", 1, ChronoUnit.WEEKS),
    TWO_WEEKS("In 2 weeks", 2, ChronoUnit.WEEKS),
    ONE_MONTH("In 1 month", 1, ChronoUnit.MONTHS);

    fun toEpochMillis(): Long {
        return LocalDate.now()
            .plus(amount, unit)
            .atTime(12, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}

private fun isFollowUpDue(followUpDate: Long): Boolean {
    val endOfToday = LocalDate.now()
        .plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() - 1

    return followUpDate <= endOfToday
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

private fun formatMonthYear(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}
