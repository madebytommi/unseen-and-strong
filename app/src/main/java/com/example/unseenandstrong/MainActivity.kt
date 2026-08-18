package com.example.unseenandstrong

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.reminder.FollowUpNotificationSupport
import com.example.unseenandstrong.reminder.FollowUpReminderPreferences
import com.example.unseenandstrong.reminder.LocalFollowUpReminderCoordinator
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
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import com.example.unseenandstrong.ui.theme.UnseenAndStrongTheme
import com.example.unseenandstrong.ui.vault.VaultScreen
import com.example.unseenandstrong.ui.vault.VaultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val database by lazy { UnseenDatabase.getDatabase(applicationContext) }
    private val followUpReminderPreferences by lazy {
        FollowUpReminderPreferences(applicationContext)
    }
    private val followUpReminderCoordinator by lazy {
        LocalFollowUpReminderCoordinator(applicationContext)
    }
    private var followUpRemindersEnabled by mutableStateOf(false)
    private var followUpReminderMessage by mutableStateOf<String?>(null)
    private var showNotificationSettingsAction by mutableStateOf(false)
    private var notificationPermissionRequestInFlight = false
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionRequestInFlight = false
        if (granted) {
            enableFollowUpReminders()
        } else {
            disableFollowUpReminders(
                message = "Follow-up reminders are still off. You can allow notifications in Android settings whenever you’re ready.",
                showSettingsAction = true
            )
        }
    }
    private val appViewModel by lazy {
        ViewModelProvider(
            this,
            AppViewModel.Factory(FlareDayPreferences(applicationContext))
        )[AppViewModel::class.java]
    }
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
            AdvocacySupportViewModel.Factory(database, followUpReminderCoordinator)
        )[AdvocacySupportViewModel::class.java]
    }
    private val accommodationViewModel by lazy {
        ViewModelProvider(this)[AccommodationViewModel::class.java]
    }
    private val requestLogViewModel by lazy {
        ViewModelProvider(this)[RequestLogViewModel::class.java]
    }
    private val benefitsTrackerViewModel by lazy {
        ViewModelProvider(
            this,
            BenefitsTrackerViewModel.Factory(application, followUpReminderCoordinator)
        )[BenefitsTrackerViewModel::class.java]
    }
    private val resourceViewModel by lazy {
        ViewModelProvider(this)[ResourceViewModel::class.java]
    }
    private val interactionViewModel by lazy {
        ViewModelProvider(
            this,
            InteractionViewModel.Factory(
                database.interactionDao(),
                followUpReminderCoordinator
            )
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
    
    private val disabilityClaimViewModel by lazy {
        ViewModelProvider(
            this,
            com.example.unseenandstrong.ui.claims.DisabilityClaimViewModel.Factory(
                application,
                database.interactionDao(),
                database.vaultDocumentDao(),
                followUpReminderCoordinator
            )
        )[com.example.unseenandstrong.ui.claims.DisabilityClaimViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreFollowUpReminderState()
        enableEdgeToEdge()
        setContent {
            var currentScreen by rememberSaveable(stateSaver = HomeScreenSaver) {
                mutableStateOf(HomeScreen.CheckIn)
            }
            var selectedClaimId by rememberSaveable { mutableStateOf<Long?>(null) }
            val isFlareDay by appViewModel.isFlareDayActive.collectAsState()
            val routineTasks by routineViewModel.tasks.collectAsState()
            val selectedTone by speakStrongViewModel.selectedTone.collectAsState()
            val selectedScript by speakStrongViewModel.selectedScript.collectAsState()
            val advocacySessions by advocacySupportViewModel.sessions.collectAsState()
            val selectedSession by advocacySupportViewModel.selectedSession.collectAsState()
            val background = if (isFlareDay) NightLavender else SoftCloudGrey

            LaunchedEffect(currentScreen, selectedClaimId) {
                if (currentScreen == HomeScreen.ClaimDetail || currentScreen == HomeScreen.ClaimForm) {
                    disabilityClaimViewModel.selectClaim(selectedClaimId)
                }
            }

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
                                    followUpRemindersEnabled = followUpRemindersEnabled,
                                    followUpReminderMessage = followUpReminderMessage,
                                    showNotificationSettingsAction = showNotificationSettingsAction,
                                    onFollowUpRemindersChanged = ::handleFollowUpReminderToggle,
                                    onOpenNotificationSettings = ::openNotificationSettings,
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
                                    onOpenStdLtdClaims = {
                                        currentScreen = HomeScreen.StdLtdClaimsList
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
                                HomeScreen.StdLtdClaimsList -> com.example.unseenandstrong.ui.claims.StdLtdClaimsListScreen(
                                    viewModel = disabilityClaimViewModel,
                                    isFlareDay = isFlareDay,
                                    onBackToHub = {
                                        currentScreen = HomeScreen.SpeakStrong
                                    },
                                    onOpenClaim = { id ->
                                        selectedClaimId = id
                                        disabilityClaimViewModel.selectClaim(id)
                                        currentScreen = HomeScreen.ClaimDetail
                                    },
                                    onAddClaim = {
                                        selectedClaimId = null
                                        disabilityClaimViewModel.selectClaim(null)
                                        currentScreen = HomeScreen.ClaimForm
                                    }
                                )
                                HomeScreen.ClaimDetail -> com.example.unseenandstrong.ui.claims.ClaimDetailScreen(
                                    viewModel = disabilityClaimViewModel,
                                    isFlareDay = isFlareDay,
                                    onBackToClaims = {
                                        currentScreen = HomeScreen.StdLtdClaimsList
                                    },
                                    onEditClaim = { id ->
                                        selectedClaimId = id
                                        disabilityClaimViewModel.selectClaim(id)
                                        currentScreen = HomeScreen.ClaimForm
                                    },
                                    onLinkInteraction = {}, // Handled internally by dialog
                                    onLinkDocument = {}, // Handled internally by dialog
                                    onOpenInteraction = {
                                        currentScreen = HomeScreen.Log
                                    },
                                    onOpenDocument = {
                                        currentScreen = HomeScreen.Vault
                                    }
                                )
                                HomeScreen.ClaimForm -> {
                                    val claimToEdit by disabilityClaimViewModel.selectedClaim.collectAsState()
                                    com.example.unseenandstrong.ui.claims.ClaimFormScreen(
                                        claim = claimToEdit,
                                        isFlareDay = isFlareDay,
                                        onSave = { claim, enableReq ->
                                            disabilityClaimViewModel.saveClaim(claim, enableReq)
                                            currentScreen = HomeScreen.StdLtdClaimsList
                                        },
                                        onCancel = {
                                            currentScreen = if (selectedClaimId == null) {
                                                HomeScreen.StdLtdClaimsList
                                            } else {
                                                HomeScreen.ClaimDetail
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (
            !notificationPermissionRequestInFlight &&
            followUpReminderPreferences.remindersEnabled &&
            !FollowUpNotificationSupport.canPostNotifications(this)
        ) {
            disableFollowUpReminders(
                message = "Follow-up reminders are off because notifications are not currently allowed.",
                showSettingsAction = true
            )
        }
    }

    private fun restoreFollowUpReminderState() {
        followUpRemindersEnabled = followUpReminderPreferences.remindersEnabled
        if (!followUpRemindersEnabled) return

        FollowUpNotificationSupport.createChannel(this)
        if (FollowUpNotificationSupport.canPostNotifications(this)) {
            lifecycleScope.launch(Dispatchers.IO) {
                followUpReminderCoordinator.reconcileAll(database)
            }
        } else {
            disableFollowUpReminders(
                message = "Follow-up reminders are off because notifications are not currently allowed.",
                showSettingsAction = true
            )
        }
    }

    private fun handleFollowUpReminderToggle(enabled: Boolean) {
        if (!enabled) {
            disableFollowUpReminders()
            return
        }

        FollowUpNotificationSupport.createChannel(this)
        when {
            FollowUpNotificationSupport.canPostNotifications(this) -> {
                enableFollowUpReminders()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !FollowUpNotificationSupport.hasRuntimePermission(this) &&
                !followUpReminderPreferences.notificationPermissionRequested -> {
                followUpReminderPreferences.notificationPermissionRequested = true
                notificationPermissionRequestInFlight = true
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                disableFollowUpReminders(
                    message = "Follow-up reminders are still off. You can allow notifications in Android settings whenever you’re ready.",
                    showSettingsAction = true
                )
            }
        }
    }

    private fun enableFollowUpReminders() {
        followUpReminderCoordinator.setRemindersEnabled(true)
        followUpRemindersEnabled = true
        followUpReminderMessage = "Follow-up reminders are on."
        showNotificationSettingsAction = false
        lifecycleScope.launch(Dispatchers.IO) {
            followUpReminderCoordinator.reconcileAll(database)
        }
    }

    private fun disableFollowUpReminders(
        message: String? = null,
        showSettingsAction: Boolean = false
    ) {
        followUpReminderCoordinator.setRemindersEnabled(false)
        followUpRemindersEnabled = false
        followUpReminderMessage = message
        showNotificationSettingsAction = showSettingsAction
    }

    private fun openNotificationSettings() {
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        )
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
    Vault,
    StdLtdClaimsList,
    ClaimDetail,
    ClaimForm;

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
            StdLtdClaimsList -> "STD/LTD Claims"
            ClaimDetail -> "Claim Details"
            ClaimForm -> "Claim Form"
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
            BenefitsTracker,
            StdLtdClaimsList,
            ClaimDetail,
            ClaimForm -> Icons.Default.Description
            Vault -> Icons.Default.Folder
        }
}

private val HomeScreenSaver = Saver<HomeScreen, String>(
    save = { screen -> screen.name },
    restore = { savedName ->
        HomeScreen.entries
            .firstOrNull { it.name == savedName }
            ?.takeUnless { it == HomeScreen.Meds || it == HomeScreen.Cycle }
            ?: HomeScreen.CheckIn
    }
)

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
        HomeScreen.BenefitsTracker,
        HomeScreen.StdLtdClaimsList,
        HomeScreen.ClaimDetail,
        HomeScreen.ClaimForm -> HomeScreen.SpeakStrong
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
                        contentDescription = null
                    )
                },
                selectedContentColor = if (isFlareDay) {
                    SoftBlushPink
                } else {
                    LavenderPurple
                },
                unselectedContentColor = if (isFlareDay) {
                    PaleCloudWhite.copy(alpha = 0.78f)
                } else {
                    DeepFogGrey
                }
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
            "Flare Day Mode",
            color = if (isFlareDayActive) PaleCloudWhite else DeepFogGrey
        )
        Switch(
            checked = isFlareDayActive,
            onCheckedChange = { onToggle() },
            modifier = Modifier.semantics {
                stateDescription = if (isFlareDayActive) "On" else "Off"
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = LavenderPurple,
                uncheckedThumbColor = SoftBlushPink,
                checkedTrackColor = LavenderPurple.copy(alpha = 0.45f),
                uncheckedTrackColor = SoftBlushPink.copy(alpha = 0.45f)
            )
        )
    }
}
