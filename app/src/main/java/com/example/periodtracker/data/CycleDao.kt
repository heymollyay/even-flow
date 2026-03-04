// Used with Room: Data Access Object, allows data interactions

package com.example.periodtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow //datatype for stream data - NOT part of our app

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycle_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<CycleData>>

    @Insert
    suspend fun insert(entry: CycleData)
}