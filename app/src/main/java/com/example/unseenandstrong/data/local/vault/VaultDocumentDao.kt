package com.example.unseenandstrong.data.local.vault

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: VaultDocumentEntity): Long

    @Delete
    suspend fun deleteDocument(document: VaultDocumentEntity): Int

    @Query("DELETE FROM claim_document_cross_ref WHERE documentId = :documentId")
    suspend fun deleteClaimDocumentLinks(documentId: Long): Int

    @Transaction
    suspend fun deleteDocumentAndLinks(document: VaultDocumentEntity): Int {
        deleteClaimDocumentLinks(document.id)
        return deleteDocument(document)
    }

    @Query("SELECT * FROM vault_documents WHERE id = :documentId LIMIT 1")
    suspend fun getDocument(documentId: Long): VaultDocumentEntity?

    @Query("SELECT * FROM vault_documents ORDER BY dateAdded DESC")
    fun getAllDocuments(): Flow<List<VaultDocumentEntity>>
}
