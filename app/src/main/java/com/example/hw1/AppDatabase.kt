package com.example.hw1

import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import androidx.room.Database

@Database(entities = [Profile::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ProfileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}