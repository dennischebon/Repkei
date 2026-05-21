package com.example.ripkei.data.local

import androidx.room.*
import com.example.ripkei.domain.model.ConnectionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM connection_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ConnectionLog>>

    @Query("SELECT * FROM connection_logs WHERE isActive = 1")
    fun getActiveConnections(): Flow<List<ConnectionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConnectionLog): Long

    @Update
    suspend fun updateLog(log: ConnectionLog)

    @Query("DELETE FROM connection_logs WHERE timestamp < :threshold")
    suspend fun deleteOldLogs(threshold: Long)
}
