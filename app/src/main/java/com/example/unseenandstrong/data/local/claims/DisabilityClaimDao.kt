package com.example.unseenandstrong.data.local.claims

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.vault.VaultDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisabilityClaimDao {
    
    // --- Claims ---

    @Query(
        """
        SELECT * FROM disability_claims 
        ORDER BY 
            CASE 
                WHEN nextActionDueDate IS NOT NULL THEN nextActionDueDate
                WHEN appealDeadline IS NOT NULL THEN appealDeadline
                WHEN benefitEndDate IS NOT NULL THEN benefitEndDate
                WHEN benefitStartDate IS NOT NULL THEN benefitStartDate
                WHEN leaveEndDate IS NOT NULL THEN leaveEndDate
                WHEN leaveStartDate IS NOT NULL THEN leaveStartDate
                ELSE 9999999999999 -- Far future
            END ASC,
            updatedAt DESC
        """
    )
    fun observeAllClaims(): Flow<List<DisabilityClaimEntity>>

    @Query("SELECT * FROM disability_claims WHERE id = :id")
    fun observeClaim(id: Long): Flow<DisabilityClaimEntity?>

    @Query("SELECT * FROM disability_claims WHERE id = :id")
    suspend fun getClaim(id: Long): DisabilityClaimEntity?

    @Insert
    suspend fun insertClaim(claim: DisabilityClaimEntity): Long

    @Update
    suspend fun updateClaim(claim: DisabilityClaimEntity): Int

    @Delete
    suspend fun deleteClaim(claim: DisabilityClaimEntity): Int

    // --- Tasks ---

    @Query("SELECT * FROM disability_claim_tasks WHERE claimId = :claimId ORDER BY sortOrder ASC, dueDate ASC")
    fun observeTasksForClaim(claimId: Long): Flow<List<DisabilityClaimTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DisabilityClaimTaskEntity): Long

    @Update
    suspend fun updateTask(task: DisabilityClaimTaskEntity): Int

    @Delete
    suspend fun deleteTask(task: DisabilityClaimTaskEntity): Int

    // --- Interactions ---

    @Query(
        """
        SELECT i.* FROM interactions i
        INNER JOIN claim_interaction_cross_ref r ON i.id = r.interactionId
        WHERE r.claimId = :claimId
        """
    )
    fun observeLinkedInteractions(claimId: Long): Flow<List<InteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkInteraction(crossRef: ClaimInteractionCrossRef): Long

    @Delete
    suspend fun unlinkInteraction(crossRef: ClaimInteractionCrossRef): Int

    // --- Documents ---

    @Query(
        """
        SELECT d.* FROM vault_documents d
        INNER JOIN claim_document_cross_ref r ON d.id = r.documentId
        WHERE r.claimId = :claimId
        """
    )
    fun observeLinkedDocuments(claimId: Long): Flow<List<VaultDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkDocument(crossRef: ClaimDocumentCrossRef): Long

    @Delete
    suspend fun unlinkDocument(crossRef: ClaimDocumentCrossRef): Int
    
    @Query("DELETE FROM claim_interaction_cross_ref WHERE claimId = :claimId")
    suspend fun clearInteractionLinksForClaim(claimId: Long): Int
    
    @Query("DELETE FROM claim_document_cross_ref WHERE claimId = :claimId")
    suspend fun clearDocumentLinksForClaim(claimId: Long): Int
}
