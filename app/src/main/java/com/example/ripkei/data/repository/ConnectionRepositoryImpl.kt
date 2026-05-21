package com.example.ripkei.data.repository

import com.example.ripkei.data.local.ConnectionDao
import com.example.ripkei.domain.model.ConnectionLog
import com.example.ripkei.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepositoryImpl @Inject constructor(
    private val connectionDao: ConnectionDao
) : ConnectionRepository {
    override fun getAllLogs(): Flow<List<ConnectionLog>> = connectionDao.getAllLogs()
    
    override fun getActiveConnections(): Flow<List<ConnectionLog>> = connectionDao.getActiveConnections()
    
    override suspend fun logConnection(log: ConnectionLog): Long {
        return connectionDao.insertLog(log)
    }

    override suspend fun updateConnection(log: ConnectionLog) {
        connectionDao.updateLog(log)
    }
}
