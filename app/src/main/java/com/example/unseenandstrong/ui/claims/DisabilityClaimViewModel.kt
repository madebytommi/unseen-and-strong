package com.example.unseenandstrong.ui.claims

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.accommodation.AccommodationRequestEntity
import com.example.unseenandstrong.data.local.claims.ClaimDocumentCrossRef
import com.example.unseenandstrong.data.local.claims.ClaimInteractionCrossRef
import com.example.unseenandstrong.data.local.claims.DisabilityClaimEntity
import com.example.unseenandstrong.data.local.claims.DisabilityClaimTaskEntity
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import androidx.lifecycle.ViewModelProvider
import com.example.unseenandstrong.data.local.interaction.InteractionDao
import com.example.unseenandstrong.data.local.vault.VaultDocumentDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ClaimFilter {
    ALL, STD, LTD
}

class DisabilityClaimViewModel(
    application: Application,
    private val interactionDao: InteractionDao,
    private val vaultDocumentDao: VaultDocumentDao
) : AndroidViewModel(application) {
    private val database = UnseenDatabase.getDatabase(application)
    private val claimDao = database.disabilityClaimDao()
    private val requestDao = database.accommodationRequestDao()

    private val _claimFilter = MutableStateFlow(ClaimFilter.ALL)
    val claimFilter: StateFlow<ClaimFilter> = _claimFilter

    val claims: StateFlow<List<DisabilityClaimEntity>> = claimDao.observeAllClaims()
        .combine(_claimFilter) { claimsList, filter ->
            when (filter) {
                ClaimFilter.ALL -> claimsList
                ClaimFilter.STD -> claimsList.filter { it.claimType == "STD" }
                ClaimFilter.LTD -> claimsList.filter { it.claimType == "LTD" }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedClaimId = MutableStateFlow<Long?>(null)
    
    val selectedClaim: StateFlow<DisabilityClaimEntity?> = _selectedClaimId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else claimDao.observeClaim(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
        
    val claimTasks: StateFlow<List<DisabilityClaimTaskEntity>> = _selectedClaimId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else claimDao.observeTasksForClaim(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val linkedInteractions: StateFlow<List<InteractionEntity>> = _selectedClaimId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else claimDao.observeLinkedInteractions(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val linkedDocuments: StateFlow<List<VaultDocumentEntity>> = _selectedClaimId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else claimDao.observeLinkedDocuments(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: ClaimFilter) {
        _claimFilter.value = filter
    }

    fun selectClaim(id: Long?) {
        _selectedClaimId.value = id
    }

    fun saveClaim(
        claim: DisabilityClaimEntity,
        enableRequestLogIntegration: Boolean
    ) {
        viewModelScope.launch {
            database.withTransaction {
                var currentClaim = claim
                
                if (enableRequestLogIntegration) {
                    val requestNotes = buildRequestNotes(currentClaim)
                    val submissionDate = currentClaim.filedDate ?: System.currentTimeMillis()
                    
                    if (currentClaim.linkedRequestId != null) {
                        val existingRequest = requestDao.getRequest(currentClaim.linkedRequestId.toInt())
                        if (existingRequest != null) {
                            requestDao.updateRequest(
                                existingRequest.copy(
                                    requestType = currentClaim.claimType,
                                    status = currentClaim.status,
                                    notes = requestNotes,
                                    submissionDate = submissionDate
                                )
                            )
                        } else {
                            val newRequestId = requestDao.insertRequest(
                                AccommodationRequestEntity(
                                    requestType = currentClaim.claimType,
                                    status = currentClaim.status,
                                    notes = requestNotes,
                                    submissionDate = submissionDate
                                )
                            )
                            currentClaim = currentClaim.copy(linkedRequestId = newRequestId)
                        }
                    } else {
                        val newRequestId = requestDao.insertRequest(
                            AccommodationRequestEntity(
                                requestType = currentClaim.claimType,
                                status = currentClaim.status,
                                notes = requestNotes,
                                submissionDate = submissionDate
                            )
                        )
                        currentClaim = currentClaim.copy(linkedRequestId = newRequestId)
                    }
                } else {
                    if (currentClaim.linkedRequestId != null) {
                        val existingRequest = requestDao.getRequest(currentClaim.linkedRequestId.toInt())
                        if (existingRequest == null) {
                            currentClaim = currentClaim.copy(linkedRequestId = null)
                        }
                    }
                }
                
                if (currentClaim.id == 0L) {
                    claimDao.insertClaim(currentClaim)
                } else {
                    claimDao.updateClaim(currentClaim)
                }
            }
        }
    }

    private fun buildRequestNotes(claim: DisabilityClaimEntity): String {
        val parts = mutableListOf<String>()
        if (claim.claimNumber.isNotBlank()) parts.add("Claim #: ${claim.claimNumber}")
        if (claim.administratorName.isNotBlank()) parts.add("Admin: ${claim.administratorName}")
        if (claim.nextAction.isNotBlank()) parts.add("Next: ${claim.nextAction}")
        if (claim.notes.isNotBlank()) parts.add(claim.notes)
        return parts.joinToString("\n")
    }

    fun deleteClaim(claim: DisabilityClaimEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            database.withTransaction {
                claimDao.clearInteractionLinksForClaim(claim.id)
                claimDao.clearDocumentLinksForClaim(claim.id)
                claimDao.deleteClaim(claim)
            }
            onDeleted()
        }
    }

    fun saveTask(task: DisabilityClaimTaskEntity) {
        viewModelScope.launch {
            if (task.id == 0L) {
                claimDao.insertTask(task)
            } else {
                claimDao.updateTask(task)
            }
        }
    }

    fun deleteTask(task: DisabilityClaimTaskEntity) {
        viewModelScope.launch {
            claimDao.deleteTask(task)
        }
    }
    
    fun toggleTaskComplete(task: DisabilityClaimTaskEntity) {
        val isNowComplete = task.status != "Complete"
        val newStatus = if (isNowComplete) "Complete" else "In progress"
        val newCompletedDate = if (isNowComplete) {
            task.completedDate ?: System.currentTimeMillis()
        } else {
            task.completedDate 
        }
        
        saveTask(task.copy(status = newStatus, completedDate = newCompletedDate, updatedAt = System.currentTimeMillis()))
    }

    fun linkInteraction(interactionId: Long) {
        val claimId = _selectedClaimId.value ?: return
        viewModelScope.launch {
            claimDao.linkInteraction(ClaimInteractionCrossRef(claimId, interactionId))
        }
    }

    fun unlinkInteraction(interactionId: Long) {
        val claimId = _selectedClaimId.value ?: return
        viewModelScope.launch {
            claimDao.unlinkInteraction(ClaimInteractionCrossRef(claimId, interactionId))
        }
    }

    fun linkDocument(documentId: Long) {
        val claimId = _selectedClaimId.value ?: return
        viewModelScope.launch {
            claimDao.linkDocument(ClaimDocumentCrossRef(claimId, documentId))
        }
    }

    fun unlinkDocument(documentId: Long) {
        val claimId = _selectedClaimId.value ?: return
        viewModelScope.launch {
            claimDao.unlinkDocument(ClaimDocumentCrossRef(claimId, documentId))
        }
    }
    
    val allInteractions = interactionDao.getAllInteractions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val allDocuments = vaultDocumentDao.getAllDocuments().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    class Factory(
        private val application: Application,
        private val interactionDao: InteractionDao,
        private val vaultDocumentDao: VaultDocumentDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DisabilityClaimViewModel::class.java)) {
                return DisabilityClaimViewModel(application, interactionDao, vaultDocumentDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
