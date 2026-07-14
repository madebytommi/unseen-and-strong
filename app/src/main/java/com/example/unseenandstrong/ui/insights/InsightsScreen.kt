package com.example.unseenandstrong.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.data.local.checkin.DailyCheckInEntity
import com.example.unseenandstrong.data.local.cycle.CycleLogEntity
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private data class TrendDay(
    val label: String,
    val spoons: Int?,
    val pain: Int?
)

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    isFlareDay: Boolean = false
) {
    val checkIns by viewModel.last7DaysDailyCheckIns.collectAsState()
    val adherence by viewModel.medicationAdherencePercentage.collectAsState()
    val cycleLogs by viewModel.last7DaysCycleLogs.collectAsState()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey
    val trendDays = rememberTrendDays(checkIns)
    val mostRecentCyclePhase = cycleLogs.firstOrNull()?.phase

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Insights & Trends",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
            Text(
                text = "A gentle view of your patterns over the last 7 days.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Medication Adherence",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Text(
                        text = "You took ${adherence.toInt()}% of your scheduled meds. Be gentle with yourself.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }
            }

            if (mostRecentCyclePhase != null) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = "Current cycle phase: $mostRecentCyclePhase",
                            color = textColor
                        )
                    },
                    enabled = false,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isFlareDay) SoftBlushPink.copy(alpha = 0.22f) else LavenderPurple.copy(alpha = 0.18f),
                        labelColor = textColor,
                        disabledContainerColor = if (isFlareDay) SoftBlushPink.copy(alpha = 0.22f) else LavenderPurple.copy(alpha = 0.18f),
                        disabledLabelColor = textColor
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "7-Day Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Text(
                        text = "Spoons in lavender. Pain in blush.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.85f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        trendDays.forEach { day ->
                            TrendColumn(
                                day = day,
                                textColor = textColor,
                                isFlareDay = isFlareDay,
                                modifier = Modifier.fillMaxWidth(0.13f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LegendDot(color = LavenderPurple, label = "Spoons", textColor = textColor)
                        LegendDot(color = SoftBlushPink, label = "Pain", textColor = textColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendColumn(
    day: TrendDay,
    textColor: Color,
    isFlareDay: Boolean,
    modifier: Modifier = Modifier
) {
    val maxBarHeight = 110.dp
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = day.label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .height(maxBarHeight)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TrendBar(
                value = day.spoons,
                maxValue = 10,
                color = LavenderPurple,
                textColor = textColor,
                isFlareDay = isFlareDay
            )
            TrendBar(
                value = day.pain,
                maxValue = 10,
                color = SoftBlushPink,
                textColor = textColor,
                isFlareDay = isFlareDay
            )
        }
    }
}

@Composable
private fun TrendBar(
    value: Int?,
    maxValue: Int,
    color: Color,
    textColor: Color,
    isFlareDay: Boolean
) {
    val fraction = ((value ?: 0).coerceIn(0, maxValue).toFloat() / maxValue.toFloat()).coerceAtLeast(0.08f)
    val barColor = if (value == null) color.copy(alpha = 0.18f) else color.copy(alpha = if (isFlareDay) 0.86f else 0.78f)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.48f)
            .fillMaxHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((110 * fraction).dp)
                .clip(RoundedCornerShape(14.dp))
                .background(barColor)
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String, textColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

private fun rememberTrendDays(checkIns: List<DailyCheckInEntity>): List<TrendDay> {
    val formatter = DateTimeFormatter.ISO_DATE
    val displayFormatter = DateTimeFormatter.ofPattern("EEE")
    val dates = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()) }
    val byDate = checkIns.associateBy { it.date }

    return dates.map { date ->
        val entry = byDate[date.format(formatter)]
        TrendDay(
            label = date.format(displayFormatter),
            spoons = entry?.spoonsLevel,
            pain = entry?.painLevel
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightsScreenPreview() {
    // Preview intentionally omitted; screen is ViewModel-backed.
}
