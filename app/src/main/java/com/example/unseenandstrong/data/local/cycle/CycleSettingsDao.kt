package com.example.unseenandstrong.data.local.cycle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: CycleSettingsEntity): Long

    @Update
    suspend fun updateSettings(settings: CycleSettingsEntity): Int

    @Query("SELECT * FROM cycle_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<CycleSettingsEntity?>

    @Query("SELECT * FROM cycle_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): CycleSettingsEntity?
}
