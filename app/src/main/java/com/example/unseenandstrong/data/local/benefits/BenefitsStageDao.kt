package com.example.unseenandstrong.data.local.benefits

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BenefitsStageDao {
    @Query("SELECT * FROM benefits_stages ORDER BY stageOrder ASC")
    fun getAllStages(): Flow<List<BenefitsStageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: BenefitsStageEntity): Long

    @Update
    suspend fun updateStage(stage: BenefitsStageEntity): Int

    @Query("SELECT COUNT(*) FROM benefits_stages")
    suspend fun getStageCount(): Int

    @Query("SELECT * FROM benefits_stages")
    suspend fun getAllStagesOnce(): List<BenefitsStageEntity>
}
