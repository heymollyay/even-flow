// App Database that sets up configurations and is the access point for data

package com.example.periodtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.periodtracker.data.CycleDao


@Database(entities = [CycleData::class], version = 1, exportSchema = false) //avoids file leaking
abstract class AppDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao //call to data access object

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        //local database instances (stored in the app)
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "cycle_tracker.db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE!! //not-null confirmation
        }
    }
}
