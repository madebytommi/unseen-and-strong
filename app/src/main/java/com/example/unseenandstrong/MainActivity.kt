package com.example.unseenandstrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Tab
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.ui.checkin.CheckInViewModel
import com.example.unseenandstrong.ui.checkin.DailyCheckInScreen
import com.example.unseenandstrong.ui.comfort.ComfortBoxScreen
import com.example.unseenandstrong.ui.cycle.CycleTrackerScreen
import com.example.unseenandstrong.ui.cycle.CycleViewModel
import com.example.unseenandstrong.ui.insights.InsightsViewModel
import com.example.unseenandstrong.ui.interaction.InteractionScreen
import com.example.unseenandstrong.ui.interaction.InteractionViewModel
import com.example.unseenandstrong.ui.journal.JournalScreen
import com.example.unseenandstrong.ui.journal.JournalViewModel
import com.example.unseenandstrong.ui.medication.MedicationTrackerScreen
import com.example.unseenandstrong.ui.medication.MedicationViewModel
import com.example.unseenandstrong.ui.routine.RoutineScreen
import com.example.unseenandstrong.ui.routine.RoutineViewModel
import com.example.unseenandstrong.ui.speakstrong.SpeakStrongScreen
import com.example.unseenandstrong.ui.speakstrong.SpeakStrongViewModel
import com.example.unseenandstrong.ui.accommodation.AccommodationScreen
import com.example.unseenandstrong.ui.accommodation.AccommodationViewModel
import com.example.unseenandstrong.ui.boundary.BoundaryBuilderScreen
import com.example.unseenandstrong.ui.resource.ResourceScreen
import com.example.unseenandstrong.ui.resource.ResourceViewModel
import com.example.unseenandstrong.ui.vault.VaultScreen
import com.example.unseenandstrong.ui.vault.VaultViewModel
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.UnseenAndStrongTheme

class MainActivity : ComponentActivity() {

    private val database: UnseenDatabase by lazy {
        UnseenDatabase.getDatabase(applicationContext)
    }

    private val appViewModel: AppViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }

    private val checkInViewModel: CheckInViewModel by lazy {
        ViewModelProvider(
            this,
            CheckInViewModel.Factory(database.dailyCheckInDao())
        )[CheckInViewModel::class.java]
    }

    private val journalViewModel: JournalViewModel by lazy {
        ViewModelProvider(
            this,
            JournalViewModel.Factory(database.journalDao())
        )[JournalViewModel::class.java]
    }

    private val routineViewModel: RoutineViewModel by lazy {
        ViewModelProvider(
            this,
            RoutineViewModel.Factory(database.routineDao())
        )[RoutineViewModel::class.java]
    }

    private val speakStrongViewModel: SpeakStrongViewModel by lazy {
        ViewModelProvider(
            this,
            SpeakStrongViewModel.Factory(database.scriptDao())
        )[SpeakStrongViewModel::class.java]
    }

    private val accommodationViewModel: AccommodationViewModel by lazy {
        ViewModelProvider(this)[AccommodationViewModel::class.java]
    }

    private val resourceViewModel: ResourceViewModel by lazy {
        ViewModelProvider(this)[ResourceViewModel::class.java]
    }

    private val interactionViewModel: InteractionViewModel by lazy {
        ViewModelProvider(
            this,
            InteractionViewModel.Factory(database.interactionDao())
        )[InteractionViewModel::class.java]
    }

    private val vaultViewModel: VaultViewModel by lazy {
        ViewModelProvider(
            this,
            VaultViewModel.Factory(database.vaultDocumentDao())
        )[VaultViewModel::class.java]
    }

    private val medicationViewModel: MedicationViewModel by lazy {
        ViewModelProvider(
            this,
            MedicationViewModel.Factory(
                medicationDao = database.medicationDao(),
                medLogDao = database.medLogDao(),
                prnLogDao = database.prnLogDao(),
                reactionDao = database.reactionDao()
            )
        )[MedicationViewModel::class.java]
    }

    private val cycleViewModel: CycleViewModel by lazy {
        ViewModelProvider(
            this,
            CycleViewModel.Factory(
                cycleLogDao = database.cycleLogDao(),
                cycleSettingsDao = database.cycleSettingsDao()
            )
        )[CycleViewModel::class.java]
    }

    private val insightsViewModel: InsightsViewModel by lazy {
        ViewModelProvider(
            this,
            InsightsViewModel.Factory(
                dailyCheckInDao = database.dailyCheckInDao(),
                medLogDao = database.medLogDao(),
                cycleLogDao = database.cycleLogDao()
            )
        )[InsightsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var currentScreen by rememberSaveable { mutableStateOf(HomeScreen.CheckIn) }
            val isFlareDayActive by appViewModel.isFlareDayActive.collectAsState()
            val routineTasks by routineViewModel.tasks.collectAsState()
            val appBackground = if (isFlareDayActive) NightLavender else SoftCloudGrey

            UnseenAndStrongTheme(
                isFlareDay = isFlareDayActive,
                content = {
                    Scaffold(
                        containerColor = appBackground,
                        bottomBar = {
                            BottomNavigationBar(
                                currentScreen = currentScreen,
                                isFlareDay = isFlareDayActive,
                                onScreenSelected = { currentScreen = it }
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = appBackground
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                FlareDayModeToggle(
                                    isFlareDayActive = isFlareDayActive,
                                    onToggle = appViewModel::toggleFlareDayMode
                                )

                                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                                    when (screen) {
                                        HomeScreen.CheckIn -> DailyCheckInScreen(
                                            isFlareDay = isFlareDayActive,
                                            onSave = checkInViewModel::saveCheckIn
                                        )
                                        HomeScreen.ComfortBox -> ComfortBoxScreen(
                                            isFlareDay = isFlareDayActive
                                        )
                                        HomeScreen.Journal -> JournalScreen(
                                            isFlareDay = isFlareDayActive,
                                            entriesFlow = journalViewModel.entries,
                                            onSaveWin = journalViewModel::saveUnseenWin,
                                            onSaveEntry = journalViewModel::saveJournalEntry
                                        )
                                        HomeScreen.Routine -> RoutineScreen(
                                            tasks = routineTasks,
                                            onToggleTask = routineViewModel::toggleTask,
                                            onAddTask = routineViewModel::addTask,
                                            isFlareDay = isFlareDayActive
                                        )
                                        HomeScreen.Meds -> MedicationTrackerScreen(
                                            viewModel = medicationViewModel,
                                            isFlareDay = isFlareDayActive
                                        )
                                        HomeScreen.Cycle -> CycleTrackerScreen(
                                            viewModel = cycleViewModel,
                                            isFlareDay = isFlareDayActive
                                        )
                                        HomeScreen.SpeakStrong -> SpeakStrongScreen(
                                            viewModel = speakStrongViewModel,
                                            isFlareDay = isFlareDayActive,
                                            onDraftAdaRequest = {
                                                currentScreen = HomeScreen.Accommodation
                                            },
                                            onOpenResources = {
                                                currentScreen = HomeScreen.Resource
                                            },
                                            onOpenBoundaryBuilder = {
                                                currentScreen = HomeScreen.BoundaryBuilder
                                            }
                                        )
                                        HomeScreen.Accommodation -> AccommodationScreen(
                                            viewModel = accommodationViewModel,
                                            isFlareDay = isFlareDayActive,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                        HomeScreen.Resource -> ResourceScreen(
                                            viewModel = resourceViewModel,
                                            isFlareDay = isFlareDayActive,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                        HomeScreen.BoundaryBuilder -> BoundaryBuilderScreen(
                                            isFlareDay = isFlareDayActive,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                        HomeScreen.Log -> InteractionScreen(
                                            viewModel = interactionViewModel,
                                            isFlareDay = isFlareDayActive,
                                            onValidationCompleteNavigateBack = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                        HomeScreen.Vault -> VaultScreen(
                                            viewModel = vaultViewModel,
                                            isFlareDay = isFlareDayActive
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

private enum class HomeScreen {
    CheckIn,
    ComfortBox,
    Journal,
    Routine,
    Meds,
    Cycle,
    SpeakStrong,
    Accommodation,
    Resource,
    BoundaryBuilder,
    Log,
    Vault;

    val label: String
        get() = when (this) {
            CheckIn -> "Check-In"
            ComfortBox -> "Comfort"
            Journal -> "Journal"
            Routine -> "Routine"
            Meds -> "Meds"
            Cycle -> "Cycle"
            SpeakStrong -> "Speak Strong"
            Accommodation -> "Accommodation"
            Resource -> "Resources"
            BoundaryBuilder -> "Boundary Builder"
            Log -> "Log"
            Vault -> "Vault"
        }

    val icon: ImageVector
        get() = when (this) {
            CheckIn -> Icons.Default.CheckCircle
            ComfortBox -> Icons.Default.Favorite
            Journal -> Icons.Default.Edit
            Routine -> Icons.AutoMirrored.Filled.List
            Meds -> Icons.Default.Healing
            Cycle -> Icons.Default.Spa
            SpeakStrong -> Icons.Default.Edit
            Accommodation -> Icons.Default.Description
            Resource -> Icons.Default.Description
            BoundaryBuilder -> Icons.Default.Description
            Log -> Icons.AutoMirrored.Filled.Assignment
            Vault -> Icons.Default.Folder
        }
}

@Composable
private fun BottomNavigationBar(
    currentScreen: HomeScreen,
    isFlareDay: Boolean,
    onScreenSelected: (HomeScreen) -> Unit
) {
    val topLevelScreens = listOf(
        HomeScreen.CheckIn,
        HomeScreen.ComfortBox,
        HomeScreen.Journal,
        HomeScreen.Routine,
        HomeScreen.Meds,
        HomeScreen.Cycle,
        HomeScreen.SpeakStrong,
        HomeScreen.Log,
        HomeScreen.Vault
    )
    val selectedScreen = if (
        currentScreen == HomeScreen.Accommodation ||
        currentScreen == HomeScreen.Resource ||
        currentScreen == HomeScreen.BoundaryBuilder
    ) {
        HomeScreen.SpeakStrong
    } else currentScreen
    val selectedIndex = topLevelScreens.indexOf(selectedScreen).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = if (isFlareDay) NightLavender else SoftCloudGrey,
        contentColor = DeepFogGrey,
        divider = {},
        indicator = {},
        edgePadding = 0.dp
    ) {
        topLevelScreens.forEach { screen ->
            Tab(
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) },
                text = { Text(text = screen.label) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                selectedContentColor = if (isFlareDay) SoftBlushPink else LavenderPurple,
                unselectedContentColor = DeepFogGrey
            )
        }
    }
}

@Composable
private fun FlareDayModeToggle(
    isFlareDayActive: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Flare Day Mode",
            color = DeepFogGrey
        )
        Switch(
            checked = isFlareDayActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = LavenderPurple,
                uncheckedThumbColor = SoftBlushPink,
                checkedTrackColor = LavenderPurple.copy(alpha = 0.45f),
                uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.45f)
            )
        )
    }
}
