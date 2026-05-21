package com.example.ripkei.vpn

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAppName(packageName: String): String {
        return try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(ai).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    fun getPackageNameFromUid(uid: Int): String? {
        val pm = context.packageManager
        return pm.getPackagesForUid(uid)?.firstOrNull()
    }
}
