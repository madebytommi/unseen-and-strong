package com.example.unseenandstrong.data.local.medication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ReactionEntity): Long

    @Update
    suspend fun updateReaction(reaction: ReactionEntity): Int

    @Delete
    suspend fun deleteReaction(reaction: ReactionEntity): Int

    @Query("SELECT * FROM reactions ORDER BY date DESC")
    fun getAllReactions(): Flow<List<ReactionEntity>>

    @Query("SELECT * FROM reactions WHERE medId = :medId ORDER BY date DESC")
    fun getReactionsForMedication(medId: Long): Flow<List<ReactionEntity>>
}
