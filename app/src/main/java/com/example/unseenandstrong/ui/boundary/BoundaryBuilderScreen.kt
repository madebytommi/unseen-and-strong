package com.example.unseenandstrong.ui.boundary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.DustyMauve
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.RoseGlow
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.launch

private enum class Firmness(val label: String) {
    GENTLE("Gentle"),
    DIRECT("Direct"),
    FIRM("Firm")
}

private data class Scenario(
    val name: String,
    val scripts: Map<Firmness, String>
)

@Composable
fun BoundaryBuilderScreen(
    isFlareDay: Boolean = false,
    onBackToHub: () -> Unit = {}
) {
    val scenarios = remember {
        listOf(
            Scenario(
                name = "Declining an invitation",
                scripts = mapOf(
                    Firmness.GENTLE to "I'd love to, but my body needs rest today.",
                    Firmness.DIRECT to "I won't be able to make it this time.",
                    Firmness.FIRM to "No, that doesn't work for me."
                )
            ),
            Scenario(
                name = "Ending a phone call early",
                scripts = mapOf(
                    Firmness.GENTLE to "I need to pause this call and rest now. Thank you for understanding.",
                    Firmness.DIRECT to "I need to end this call now and take care of my health.",
                    Firmness.FIRM to "I am ending this call now. We can revisit this another time."
                )
            ),
            Scenario(
                name = "Refusing unsolicited medical advice",
                scripts = mapOf(
                    Firmness.GENTLE to "Thank you for caring. I am following a plan with my care team.",
                    Firmness.DIRECT to "I am not looking for medical suggestions right now.",
                    Firmness.FIRM to "Please stop giving me medical advice."
                )
            ),
            Scenario(
                name = "Setting a limit at work",
                scripts = mapOf(
                    Firmness.GENTLE to "I want to do this well, and I need to keep my workload sustainable.",
                    Firmness.DIRECT to "I cannot take that on right now. My current workload is full.",
                    Firmness.FIRM to "I am not available for additional tasks right now."
                )
            )
        )
    }

    var selectedScenario by rememberSaveable { mutableStateOf(scenarios.first().name) }
    var selectedFirmness by rememberSaveable { mutableStateOf(Firmness.GENTLE) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val textColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val cardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey

    val activeScenario = scenarios.first { it.name == selectedScenario }
    val generatedScript = activeScenario.scripts[selectedFirmness].orEmpty()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        containerColor = backgroundColor,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = if (isFlareDay) NightLavender.copy(alpha = 0.92f) else RoseGlow,
                    contentColor = if (isFlareDay) PaleCloudWhite else NightLavender
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onBackToHub,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink,
                    contentColor = NightLavender
                )
            ) {
                Text("Back to Speak Strong")
            }

            Text(
                text = "It is okay to protect your energy.",
                style = MaterialTheme.typography.headlineSmall,
                color = if (isFlareDay) SoftBlushPink else DustyMauve
            )
            if (!isFlareDay) {
                Text(
                    text = "Pick a scenario and adjust your boundary tone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenarios.forEach { scenario ->
                    val selected = selectedScenario == scenario.name
                    FilterChip(
                        selected = selected,
                        onClick = { selectedScenario = scenario.name },
                        label = { Text(scenario.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isFlareDay) NightLavender else SoftCloudGrey,
                            labelColor = textColor,
                            selectedContainerColor = LavenderPurple,
                            selectedLabelColor = NightLavender
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Firmness.entries.forEach { firmness ->
                    val selected = selectedFirmness == firmness
                    FilterChip(
                        selected = selected,
                        onClick = { selectedFirmness = firmness },
                        label = { Text(text = firmness.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isFlareDay) NightLavender else SoftCloudGrey,
                            labelColor = textColor,
                            selectedContainerColor = LavenderPurple,
                            selectedLabelColor = NightLavender
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedScenario,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFlareDay) SoftBlushPink else DustyMauve
                    )
                    Text(
                        text = generatedScript,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "You practiced this boundary.",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlushPink,
                    contentColor = NightLavender
                )
            ) {
                Text("Practice")
            }
        }
    }
}
