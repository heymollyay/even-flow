// App Database that sets up configurations and is the access point for data

package com.example.periodtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CycleData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun CycleDao(): CycleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        //local database instances (stored in the app)
        fun getInstance(context:Context): AppDatabase {

        }
    }
}
