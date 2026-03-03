// Used with Room: Data Access Object, allows data interactions

package com.example.periodtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow //datatype for stream data - NOT part of our app

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycle_entries")
    fun getAll(): Flow<List<CycleData>> //automatically updates with insert/deletes

    @Insert
    fun insert(entry: CycleData)

    @Query("DELETE FROM cycle_entries WHERE id = :id")
    fun delete(entry: CycleData)
}