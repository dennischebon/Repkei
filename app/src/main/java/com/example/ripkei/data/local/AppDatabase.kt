package com.example.ripkei.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ripkei.domain.model.ConnectionLog

@Database(entities = [ConnectionLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao
}
