package com.example.periodtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@Entity(tableName = "cycle_entries")
data class CycleData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val encryptedDate: String,
    val encryptedFlow: String,
    val encryptedNotes: String,
    val createdAt: Long = System.currentTimeMillis(),
)

