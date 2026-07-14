package com.example.unseenandstrong.data.local.medication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Update
    suspend fun updateMedication(medication: MedicationEntity): Int

    @Delete
    suspend fun deleteMedication(medication: MedicationEntity): Int

    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllMedications(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveMedications(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE id = :id LIMIT 1")
    suspend fun getMedicationById(id: Long): MedicationEntity?
}
