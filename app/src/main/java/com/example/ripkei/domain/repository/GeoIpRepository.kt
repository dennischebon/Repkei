package com.example.ripkei.domain.repository

import com.example.ripkei.data.remote.IpLocation

interface GeoIpRepository {
    suspend fun getLocation(ip: String): IpLocation?
}
