package com.example.unseenandstrong.data.local.medication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedLog(log: MedLogEntity): Long

    @Update
    suspend fun updateMedLog(log: MedLogEntity): Int

    @Delete
    suspend fun deleteMedLog(log: MedLogEntity): Int

    @Query("SELECT * FROM med_logs ORDER BY scheduledTime DESC")
    fun getAllMedLogs(): Flow<List<MedLogEntity>>

    @Query("SELECT * FROM med_logs WHERE medId = :medId ORDER BY scheduledTime DESC")
    fun getMedLogsForMedication(medId: Long): Flow<List<MedLogEntity>>
}
