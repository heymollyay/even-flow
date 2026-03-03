package com.example.periodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_entries")
data class CycleData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val encryptedDate: String,
    val encryptedFlow: String,
    val encryptedNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)