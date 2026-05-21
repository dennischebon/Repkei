package com.example.ripkei.domain.security

import com.example.ripkei.domain.model.ConnectionLog

enum class RiskLevel { LOW, MEDIUM, HIGH }

data class SecurityAlert(
    val riskLevel: RiskLevel,
    val reason: String
)

class SecurityAnalyzer {
    fun analyze(log: ConnectionLog): SecurityAlert {
        // Mock logic for security detection
        if (log.destinationPort == 4444 || log.destinationPort == 6666) {
            return SecurityAlert(RiskLevel.HIGH, "Suspicious port detected (Possible C2)")
        }
        
        if (log.destinationIp.startsWith("185.") || log.destinationIp.startsWith("45.")) {
            // Simplified "blacklisted" range check
            return SecurityAlert(RiskLevel.MEDIUM, "Known high-risk IP range")
        }

        if (log.bytesSent > 1024 * 1024 * 10) { // > 10MB sent at once
             return SecurityAlert(RiskLevel.MEDIUM, "Large data upload detected")
        }

        return SecurityAlert(RiskLevel.LOW, "Normal activity")
    }
}
