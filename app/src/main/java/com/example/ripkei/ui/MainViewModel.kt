package com.example.ripkei.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ripkei.domain.model.ConnectionLog
import com.example.ripkei.domain.repository.ConnectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.ripkei.domain.security.RiskLevel
import com.example.ripkei.domain.security.SecurityAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ConnectionRepository
) : ViewModel() {

    private val securityAnalyzer = SecurityAnalyzer()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allLogs: StateFlow<List<ConnectionLog>> = repository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getRiskLevel(log: ConnectionLog): RiskLevel {
        return securityAnalyzer.analyze(log).riskLevel
    }

    val filteredLogs: StateFlow<List<ConnectionLog>> = combine(allLogs, searchQuery) { logs, query ->
        if (query.isEmpty()) logs
        else logs.filter { 
            it.destinationIp.contains(query, ignoreCase = true) || 
            it.packageName?.contains(query, ignoreCase = true) == true ||
            it.appName?.contains(query, ignoreCase = true) == true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeConnections: StateFlow<List<ConnectionLog>> = repository.getActiveConnections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topApps: StateFlow<Map<String, Int>> = allLogs.map { logs ->
        logs.groupBy { it.appName ?: "Unknown" }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .toMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
