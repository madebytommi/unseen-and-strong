package com.example.unseenandstrong.data.local.cycle

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycleLog(log: CycleLogEntity): Long

    @Update
    suspend fun updateCycleLog(log: CycleLogEntity): Int

    @Delete
    suspend fun deleteCycleLog(log: CycleLogEntity): Int

    @Query("SELECT * FROM cycle_logs ORDER BY date DESC")
    fun getAllCycleLogs(): Flow<List<CycleLogEntity>>

    @Query("SELECT * FROM cycle_logs WHERE date = :date LIMIT 1")
    suspend fun getCycleLogByDate(date: Long): CycleLogEntity?
}
