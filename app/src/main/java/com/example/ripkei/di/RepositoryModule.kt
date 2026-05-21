package com.example.ripkei.di

import com.example.ripkei.data.repository.ConnectionRepositoryImpl
import com.example.ripkei.domain.repository.ConnectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConnectionRepository(
        connectionRepositoryImpl: ConnectionRepositoryImpl
    ): ConnectionRepository

    @Binds
    @Singleton
    abstract fun bindGeoIpRepository(
        geoIpRepositoryImpl: GeoIpRepositoryImpl
    ): GeoIpRepository
}
