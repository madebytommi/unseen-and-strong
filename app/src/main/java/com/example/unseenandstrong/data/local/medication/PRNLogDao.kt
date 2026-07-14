package com.example.unseenandstrong.data.local.medication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PRNLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPRNLog(log: PRNLogEntity): Long

    @Update
    suspend fun updatePRNLog(log: PRNLogEntity): Int

    @Delete
    suspend fun deletePRNLog(log: PRNLogEntity): Int

    @Query("SELECT * FROM prn_logs ORDER BY timeTaken DESC")
    fun getAllPRNLogs(): Flow<List<PRNLogEntity>>

    @Query("SELECT * FROM prn_logs WHERE medId = :medId ORDER BY timeTaken DESC")
    fun getPRNLogsForMedication(medId: Long): Flow<List<PRNLogEntity>>
}
