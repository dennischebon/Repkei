package com.example.ripkei.data.repository

import com.example.ripkei.data.remote.GeoIpService
import com.example.ripkei.data.remote.IpLocation
import com.example.ripkei.domain.repository.GeoIpRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoIpRepositoryImpl @Inject constructor(
    private val geoIpService: GeoIpService
) : GeoIpRepository {
    private val cache = mutableMapOf<String, IpLocation>()

    override suspend fun getLocation(ip: String): IpLocation? {
        if (cache.containsKey(ip)) return cache[ip]
        
        return try {
            val location = geoIpService.getLocation(ip)
            if (location.status == "success") {
                cache[ip] = location
                location
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
