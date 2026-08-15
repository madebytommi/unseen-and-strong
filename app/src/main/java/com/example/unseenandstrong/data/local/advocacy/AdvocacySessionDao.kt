package com.example.unseenandstrong.data.local.advocacy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AdvocacySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AdvocacySessionEntity): Long

    @Update
    suspend fun updateSession(session: AdvocacySessionEntity): Int

    @Query("SELECT * FROM advocacy_sessions ORDER BY updatedAt DESC")
    fun observeAllSessions(): Flow<List<AdvocacySessionEntity>>

    @Query("SELECT * FROM advocacy_sessions WHERE id = :id LIMIT 1")
    fun observeSession(id: Long): Flow<AdvocacySessionEntity?>

    @Query("SELECT * FROM advocacy_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): AdvocacySessionEntity?

    @Query("SELECT * FROM advocacy_sessions")
    suspend fun getAllSessions(): List<AdvocacySessionEntity>
}
