package com.example.unseenandstrong.data.local.checkin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: DailyCheckInEntity): Long

    @Query("SELECT * FROM daily_check_ins WHERE entry_date = :date LIMIT 1")
    suspend fun getCheckInByDate(date: String): DailyCheckInEntity?

    @Query("SELECT * FROM daily_check_ins ORDER BY entry_date DESC")
    fun getAllCheckIns(): Flow<List<DailyCheckInEntity>>
}
