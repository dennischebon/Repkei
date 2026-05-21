package com.example.ripkei.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class IpLocation(
    val query: String,
    val status: String,
    val country: String?,
    val city: String?,
    val lat: Double?,
    val lon: Double?,
    val isp: String?,
    val org: String?,
    val asName: String?
)

interface GeoIpService {
    @GET("json/{ip}")
    suspend fun getLocation(@Path("ip") ip: String): IpLocation
}
