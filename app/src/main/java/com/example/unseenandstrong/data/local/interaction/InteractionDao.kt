package com.example.unseenandstrong.data.local.interaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: InteractionEntity): Long

    @Update
    suspend fun updateInteraction(interaction: InteractionEntity): Int

    @Delete
    suspend fun deleteInteraction(interaction: InteractionEntity): Int

    @Query("SELECT * FROM interactions WHERE id = :id LIMIT 1")
    suspend fun getInteractionById(id: Long): InteractionEntity?

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun getAllInteractions(): Flow<List<InteractionEntity>>

    @Query("SELECT * FROM interactions")
    suspend fun getAllInteractionsOnce(): List<InteractionEntity>
}
