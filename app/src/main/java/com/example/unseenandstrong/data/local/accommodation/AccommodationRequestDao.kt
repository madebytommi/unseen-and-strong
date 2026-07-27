package com.example.unseenandstrong.data.local.accommodation

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccommodationRequestDao {
    @Query("SELECT * FROM accommodation_requests ORDER BY submissionDate DESC")
    fun getAllRequests(): Flow<List<AccommodationRequestEntity>>

    @Query("SELECT * FROM accommodation_requests WHERE id = :id")
    suspend fun getRequest(id: Int): AccommodationRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: AccommodationRequestEntity): Long

    @Update
    suspend fun updateRequest(request: AccommodationRequestEntity): Int

    @Delete
    suspend fun deleteRequest(request: AccommodationRequestEntity): Int
}
