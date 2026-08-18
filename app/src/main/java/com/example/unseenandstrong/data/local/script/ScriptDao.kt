package com.example.unseenandstrong.data.local.script

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scripts: List<ScriptEntity>): List<Long>

    @Query("SELECT * FROM scripts ORDER BY category ASC, title ASC")
    fun getAllScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE category = :category ORDER BY title ASC")
    fun getScriptsByCategory(category: String): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE id = :id LIMIT 1")
    suspend fun getScriptById(id: Long): ScriptEntity?
}

