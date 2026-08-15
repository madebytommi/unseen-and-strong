package com.example.unseenandstrong.ui.resource

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey

@Composable
fun ResourceScreen(
    viewModel: ResourceViewModel,
    isFlareDay: Boolean = false,
    onBackToHub: () -> Unit = {}
) {
    val checklistItems by viewModel.checklistItems.collectAsState()
    val validationFacts by viewModel.validationFacts.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.84f) else SoftCloudGrey
    val factCardColor = if (isFlareDay) NightLavender.copy(alpha = 0.92f) else SoftBlushPink.copy(alpha = 0.16f)
    var showValidationFacts by rememberSaveable(isFlareDay) { mutableStateOf(!isFlareDay) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(
                    onClick = onBackToHub,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = NightLavender
                    )
                ) {
                    Text(text = "Back to Speak Strong")
                }
            }

            item {
                Text(
                    text = "Advocacy Resources",
                    style = MaterialTheme.typography.headlineSmall,
                    color = textColor
                )
            }

            item {
                Text(
                    text = "Am I Eligible?",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            }

            items(checklistItems, key = { it.title }) { item ->
                ExpandableChecklistCard(
                    item = item,
                    textColor = textColor,
                    cardColor = cardColor
                )
            }

            item {
                Text(
                    text = "Invisible, Not Imaginary",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
                Text(
                    text = "Your experience is valid, even when the outside world cannot see it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            if (showValidationFacts) {
                items(validationFacts, key = { it.title }) { fact ->
                    ValidationFactCard(
                        fact = fact,
                        textColor = textColor,
                        cardColor = factCardColor
                    )
                }
            } else {
                item {
                    Button(
                        onClick = { showValidationFacts = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftBlushPink,
                            contentColor = NightLavender
                        )
                    ) {
                        Text("Show validation support")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableChecklistCard(
    item: ChecklistItem,
    textColor: Color,
    cardColor: Color
) {
    var expanded by rememberSaveable(item.title) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" }
            .clickable(
                role = Role.Button,
                onClickLabel = if (expanded) "Collapse ${item.title}" else "Expand ${item.title}"
            ) { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = cardColor)
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
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = textColor
                )
            }

            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    item.points.forEach { point ->
                        Text(
                            text = "• $point",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationFactCard(
    fact: ValidationFact,
    textColor: Color,
    cardColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = fact.title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
            Text(
                text = fact.description,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}
