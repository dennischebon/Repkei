package com.example.ripkei.domain.repository

import com.example.ripkei.domain.model.ConnectionLog
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
    fun getAllLogs(): Flow<List<ConnectionLog>>
    fun getActiveConnections(): Flow<List<ConnectionLog>>
    suspend fun logConnection(log: ConnectionLog): Long
    suspend fun updateConnection(log: ConnectionLog)
}
