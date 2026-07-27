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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun StdLtdClaimsListScreen(
    viewModel: DisabilityClaimViewModel,
    isFlareDay: Boolean,
    onBackToHub: () -> Unit,
    onOpenClaim: (Long) -> Unit,
    onAddClaim: () -> Unit
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite
    val claims by viewModel.claims.collectAsState()
    val currentFilter by viewModel.claimFilter.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(onClick = onBackToHub) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Speak Strong",
                        tint = textColor
                    )
                }
                Text(
                    "STD/LTD Claims",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "An organizational space for short-term and long-term disability claims. This does not calculate eligibility or replace official portals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClaimFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = currentFilter == filter,
                                onClick = { viewModel.setFilter(filter) },
                                label = { Text(filter.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderPurple,
                                    selectedLabelColor = PaleCloudWhite
                                )
                            )
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = onAddClaim,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderPurple,
                            contentColor = PaleCloudWhite
                        )
                    ) {
                        Text("Add New Claim")
                    }
                }

                if (claims.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) {
                            Text(
                                "No disability claims saved yet. You can start with only the claim type and add the rest later.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(claims, key = { it.id }) { claim ->
                        ClaimCard(
                            claim = claim,
                            isFlareDay = isFlareDay,
                            onClick = { onOpenClaim(claim.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClaimCard(
    claim: DisabilityClaimEntity,
    isFlareDay: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else PaleCloudWhite

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = claim.claimType,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isFlareDay) SoftBlushPink else LavenderPurple
                )
                Text(
                    text = claim.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
            
            val org = listOf(claim.employerName, claim.administratorName).filter { it.isNotBlank() }.joinToString(" / ")
            if (org.isNotBlank()) {
                Text(
                    text = org,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            if (claim.claimNumber.isNotBlank()) {
                Text(
                    text = "Claim #: ${claim.claimNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
            
            val nearestDate = getNearestDeadline(claim)
            if (nearestDate != null) {
                Text(
                    text = "Nearest deadline: ${DeadlineDateUtils.formatMillisAsDate(nearestDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
            
            if (claim.nextAction.isNotBlank()) {
                Text(
                    text = "Next: ${claim.nextAction}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

fun getNearestDeadline(claim: DisabilityClaimEntity): Long? {
    val dates = listOfNotNull(
        claim.nextActionDueDate,
        claim.appealDeadline,
        claim.benefitEndDate,
        claim.benefitStartDate,
        claim.leaveEndDate,
        claim.leaveStartDate
    )
    return dates.minOrNull()
}
