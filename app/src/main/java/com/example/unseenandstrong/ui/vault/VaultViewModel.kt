package com.example.unseenandstrong.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.vault.VaultDocumentDao
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(
    private val vaultDocumentDao: VaultDocumentDao
) : ViewModel() {

    val documents: StateFlow<List<VaultDocumentEntity>> =
        vaultDocumentDao.getAllDocuments().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun saveDocument(title: String, category: String, fileUri: String) {
        val trimmedTitle = title.trim()
        val trimmedCategory = category.trim()
        val trimmedFileUri = fileUri.trim()

        if (trimmedTitle.isBlank() || trimmedCategory.isBlank() || trimmedFileUri.isBlank()) return

        viewModelScope.launch {
            vaultDocumentDao.insertDocument(
                VaultDocumentEntity(
                    title = trimmedTitle,
                    category = trimmedCategory,
                    fileUri = trimmedFileUri,
                    dateAdded = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteDocument(document: VaultDocumentEntity) {
        viewModelScope.launch {
            vaultDocumentDao.deleteDocumentAndLinks(document)
        }
    }

    class Factory(
        private val vaultDocumentDao: VaultDocumentDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VaultViewModel::class.java)) {
                return VaultViewModel(vaultDocumentDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
