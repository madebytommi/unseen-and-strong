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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import com.example.unseenandstrong.ui.accommodation.AccommodationScreen
import com.example.unseenandstrong.ui.accommodation.AccommodationViewModel
import com.example.unseenandstrong.ui.accommodation.RequestLogScreen
import com.example.unseenandstrong.ui.accommodation.RequestLogViewModel
import com.example.unseenandstrong.ui.benefits.BenefitsTrackerScreen
import com.example.unseenandstrong.ui.benefits.BenefitsTrackerViewModel
import com.example.unseenandstrong.ui.boundary.BoundaryBuilderScreen
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
import com.example.unseenandstrong.ui.resource.ResourceScreen
import com.example.unseenandstrong.ui.resource.ResourceViewModel
import com.example.unseenandstrong.ui.routine.RoutineScreen
import com.example.unseenandstrong.ui.routine.RoutineViewModel
import com.example.unseenandstrong.ui.speakstrong.AdvocacyPlansScreen
import com.example.unseenandstrong.ui.speakstrong.AdvocacyPreparationScreen
import com.example.unseenandstrong.ui.speakstrong.AdvocacyReflectionScreen
import com.example.unseenandstrong.ui.speakstrong.AdvocacySelectionFallback
import com.example.unseenandstrong.ui.speakstrong.AdvocacySupportViewModel
import com.example.unseenandstrong.ui.speakstrong.ScriptRehearsalScreen
import com.example.unseenandstrong.ui.speakstrong.SpeakStrongScreen
import com.example.unseenandstrong.ui.speakstrong.SpeakStrongViewModel
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.UnseenAndStrongTheme
import com.example.unseenandstrong.ui.vault.VaultScreen
import com.example.unseenandstrong.ui.vault.VaultViewModel

class MainActivity : ComponentActivity() {
    private val database by lazy { UnseenDatabase.getDatabase(applicationContext) }
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }
    private val checkInViewModel by lazy {
        ViewModelProvider(
            this,
            CheckInViewModel.Factory(database.dailyCheckInDao())
        )[CheckInViewModel::class.java]
    }
    private val journalViewModel by lazy {
        ViewModelProvider(
            this,
            JournalViewModel.Factory(database.journalDao())
        )[JournalViewModel::class.java]
    }
    private val routineViewModel by lazy {
        ViewModelProvider(
            this,
            RoutineViewModel.Factory(database.routineDao())
        )[RoutineViewModel::class.java]
    }
    private val speakStrongViewModel by lazy {
        ViewModelProvider(
            this,
            SpeakStrongViewModel.Factory(database.scriptDao())
        )[SpeakStrongViewModel::class.java]
    }
    private val advocacySupportViewModel by lazy {
        ViewModelProvider(
            this,
            AdvocacySupportViewModel.Factory(database)
        )[AdvocacySupportViewModel::class.java]
    }
    private val accommodationViewModel by lazy {
        ViewModelProvider(this)[AccommodationViewModel::class.java]
    }
    private val requestLogViewModel by lazy {
        ViewModelProvider(this)[RequestLogViewModel::class.java]
    }
    private val benefitsTrackerViewModel by lazy {
        ViewModelProvider(this)[BenefitsTrackerViewModel::class.java]
    }
    private val resourceViewModel by lazy {
        ViewModelProvider(this)[ResourceViewModel::class.java]
    }
    private val interactionViewModel by lazy {
        ViewModelProvider(
            this,
            InteractionViewModel.Factory(database.interactionDao())
        )[InteractionViewModel::class.java]
    }
    private val vaultViewModel by lazy {
        ViewModelProvider(
            this,
            VaultViewModel.Factory(database.vaultDocumentDao())
        )[VaultViewModel::class.java]
    }
    private val medicationViewModel by lazy {
        ViewModelProvider(
            this,
            MedicationViewModel.Factory(
                database.medicationDao(),
                database.medLogDao(),
                database.prnLogDao(),
                database.reactionDao()
            )
        )[MedicationViewModel::class.java]
    }
    private val cycleViewModel by lazy {
        ViewModelProvider(
            this,
            CycleViewModel.Factory(
                database.cycleLogDao(),
                database.cycleSettingsDao()
            )
        )[CycleViewModel::class.java]
    }
    private val insightsViewModel by lazy {
        ViewModelProvider(
            this,
            InsightsViewModel.Factory(
                database.dailyCheckInDao(),
                database.medLogDao(),
                database.cycleLogDao()
            )
        )[InsightsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by rememberSaveable { mutableStateOf(HomeScreen.CheckIn) }
            val isFlareDay by appViewModel.isFlareDayActive.collectAsState()
            val routineTasks by routineViewModel.tasks.collectAsState()
            val selectedTone by speakStrongViewModel.selectedTone.collectAsState()
            val selectedScript by speakStrongViewModel.selectedScript.collectAsState()
            val advocacySessions by advocacySupportViewModel.sessions.collectAsState()
            val selectedSession by advocacySupportViewModel.selectedSession.collectAsState()
            val background = if (isFlareDay) NightLavender else SoftCloudGrey

            UnseenAndStrongTheme(isFlareDay = isFlareDay) {
                Scaffold(
                    containerColor = background,
                    bottomBar = {
                        BottomNavigationBar(
                            currentScreen = currentScreen,
                            isFlareDay = isFlareDay,
                            onScreenSelected = { currentScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = background
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            FlareDayModeToggle(
                                isFlareDayActive = isFlareDay,
                                onToggle = appViewModel::toggleFlareDayMode
                            )

                            when (currentScreen) {
                                HomeScreen.CheckIn -> DailyCheckInScreen(
                                    isFlareDay = isFlareDay,
                                    onSave = checkInViewModel::saveCheckIn
                                )
                                HomeScreen.ComfortBox -> ComfortBoxScreen(
                                    isFlareDay = isFlareDay
                                )
                                HomeScreen.Journal -> JournalScreen(
                                    isFlareDay = isFlareDay,
                                    entriesFlow = journalViewModel.entries,
                                    onSaveWin = journalViewModel::saveUnseenWin,
                                    onSaveEntry = journalViewModel::saveJournalEntry
                                )
                                HomeScreen.Routine -> RoutineScreen(
                                    tasks = routineTasks,
                                    onToggleTask = routineViewModel::toggleTask,
                                    onAddTask = routineViewModel::addTask,
                                    isFlareDay = isFlareDay
                                )
                                HomeScreen.Meds -> MedicationTrackerScreen(
                                    viewModel = medicationViewModel,
                                    isFlareDay = isFlareDay
                                )
                                HomeScreen.Cycle -> CycleTrackerScreen(
                                    viewModel = cycleViewModel,
                                    isFlareDay = isFlareDay
                                )
                                HomeScreen.SpeakStrong -> SpeakStrongScreen(
                                    viewModel = speakStrongViewModel,
                                    isFlareDay = isFlareDay,
                                    onDraftAdaRequest = {
                                        currentScreen = HomeScreen.Accommodation
                                    },
                                    onOpenResources = {
                                        currentScreen = HomeScreen.Resource
                                    },
                                    onOpenBoundaryBuilder = {
                                        currentScreen = HomeScreen.BoundaryBuilder
                                    },
                                    onOpenRequestLog = {
                                        currentScreen = HomeScreen.RequestLog
                                    },
                                    onOpenBenefitsTracker = {
                                        currentScreen = HomeScreen.BenefitsTracker
                                    },
                                    onOpenAdvocacyPlans = {
                                        currentScreen = HomeScreen.AdvocacyPlans
                                    },
                                    onOpenScript = {
                                        currentScreen = HomeScreen.Rehearsal
                                    }
                                )
                                HomeScreen.Rehearsal -> {
                                    val script = selectedScript
                                    if (script == null) {
                                        AdvocacySelectionFallback(
                                            message = "Choose a script before opening rehearsal.",
                                            isFlareDay = isFlareDay,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                    } else {
                                        ScriptRehearsalScreen(
                                            script = script,
                                            selectedTone = selectedTone,
                                            isFlareDay = isFlareDay,
                                            onToneChanged = speakStrongViewModel::setTone,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            },
                                            onStartPreparation = { scriptText ->
                                                advocacySupportViewModel.beginPreparation(
                                                    script = script,
                                                    tone = selectedTone,
                                                    scriptText = scriptText,
                                                    onCreated = {
                                                        currentScreen = HomeScreen.AdvocacyPreparation
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                                HomeScreen.AdvocacyPlans -> AdvocacyPlansScreen(
                                    sessions = advocacySessions,
                                    isFlareDay = isFlareDay,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    },
                                    onOpenPreparation = { id ->
                                        advocacySupportViewModel.selectSession(id)
                                        currentScreen = HomeScreen.AdvocacyPreparation
                                    },
                                    onOpenReflection = { id ->
                                        advocacySupportViewModel.selectSession(id)
                                        currentScreen = HomeScreen.AdvocacyReflection
                                    }
                                )
                                HomeScreen.AdvocacyPreparation -> {
                                    val session = selectedSession
                                    if (session == null) {
                                        AdvocacySelectionFallback(
                                            message = "Loading your saved preparation.",
                                            isFlareDay = isFlareDay,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                    } else {
                                        AdvocacyPreparationScreen(
                                            session = session,
                                            isFlareDay = isFlareDay,
                                            onBackToPlans = {
                                                currentScreen = HomeScreen.AdvocacyPlans
                                            },
                                            onSave = { input, onSaved ->
                                                advocacySupportViewModel.savePreparation(
                                                    session,
                                                    input,
                                                    onSaved
                                                )
                                            },
                                            onContinueToReflection = {
                                                currentScreen = HomeScreen.AdvocacyReflection
                                            }
                                        )
                                    }
                                }
                                HomeScreen.AdvocacyReflection -> {
                                    val session = selectedSession
                                    if (session == null) {
                                        AdvocacySelectionFallback(
                                            message = "Choose a saved plan before adding a reflection.",
                                            isFlareDay = isFlareDay,
                                            onBackToHub = {
                                                currentScreen = HomeScreen.SpeakStrong
                                            }
                                        )
                                    } else {
                                        AdvocacyReflectionScreen(
                                            session = session,
                                            isFlareDay = isFlareDay,
                                            onBackToPlans = {
                                                currentScreen = HomeScreen.AdvocacyPlans
                                            },
                                            onSave = { input, onSaved ->
                                                advocacySupportViewModel.saveReflection(
                                                    session,
                                                    input,
                                                    onSaved
                                                )
                                            }
                                        )
                                    }
                                }
                                HomeScreen.Accommodation -> AccommodationScreen(
                                    viewModel = accommodationViewModel,
                                    isFlareDay = isFlareDay,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.Resource -> ResourceScreen(
                                    viewModel = resourceViewModel,
                                    isFlareDay = isFlareDay,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.BoundaryBuilder -> BoundaryBuilderScreen(
                                    isFlareDay = isFlareDay,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.RequestLog -> RequestLogScreen(
                                    isFlareDay = isFlareDay,
                                    viewModel = requestLogViewModel,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.BenefitsTracker -> BenefitsTrackerScreen(
                                    isFlareDay = isFlareDay,
                                    viewModel = benefitsTrackerViewModel,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.Log -> InteractionScreen(
                                    viewModel = interactionViewModel,
                                    isFlareDay = isFlareDay,
                                    onValidationCompleteNavigateBack = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    }
                                )
                                HomeScreen.Vault -> VaultScreen(
                                    viewModel = vaultViewModel,
                                    isFlareDay = isFlareDay
                                )
                            }
                        }
                    }
                }
            }
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
    Rehearsal,
    AdvocacyPlans,
    AdvocacyPreparation,
    AdvocacyReflection,
    Accommodation,
    Resource,
    BoundaryBuilder,
    RequestLog,
    BenefitsTracker,
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
            Rehearsal -> "Rehearsal"
            AdvocacyPlans -> "Advocacy Plans"
            AdvocacyPreparation -> "Preparation"
            AdvocacyReflection -> "Reflection"
            Accommodation -> "Accommodation"
            Resource -> "Resources"
            BoundaryBuilder -> "Boundary Builder"
            RequestLog -> "Request Log"
            BenefitsTracker -> "Benefits Tracker"
            Log -> "Log"
            Vault -> "Vault"
        }

    val icon: ImageVector
        get() = when (this) {
            CheckIn -> Icons.Default.CheckCircle
            ComfortBox -> Icons.Default.Favorite
            Journal, SpeakStrong, Rehearsal -> Icons.Default.Edit
            Routine -> Icons.AutoMirrored.Filled.List
            Meds -> Icons.Default.Healing
            Cycle -> Icons.Default.Spa
            AdvocacyPlans, RequestLog, Log -> Icons.AutoMirrored.Filled.Assignment
            AdvocacyPreparation,
            AdvocacyReflection,
            Accommodation,
            Resource,
            BoundaryBuilder,
            BenefitsTracker -> Icons.Default.Description
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
    val selectedScreen = when (currentScreen) {
        HomeScreen.Rehearsal,
        HomeScreen.AdvocacyPlans,
        HomeScreen.AdvocacyPreparation,
        HomeScreen.AdvocacyReflection,
        HomeScreen.Accommodation,
        HomeScreen.Resource,
        HomeScreen.BoundaryBuilder,
        HomeScreen.RequestLog,
        HomeScreen.BenefitsTracker -> HomeScreen.SpeakStrong
        else -> currentScreen
    }
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
                text = { Text(screen.label) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                selectedContentColor = if (isFlareDay) {
                    SoftBlushPink
                } else {
                    LavenderPurple
                },
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
        Text("Flare Day Mode", color = DeepFogGrey)
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
