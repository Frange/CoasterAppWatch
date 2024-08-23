package com.jmr.coasterappwatch.di

import com.jmr.coasterappwatch.data.api.model.coaster.CoasterModel
import com.jmr.coasterappwatch.data.api.model.coaster.CoasterModelImpl
import com.jmr.coasterappwatch.data.api.model.company.CompanyModel
import com.jmr.coasterappwatch.data.api.model.company.CompanyModelImpl
import com.jmr.coasterappwatch.data.api.model.park.ParkModel
import com.jmr.coasterappwatch.data.api.model.park.ParkModelImpl
import com.jmr.coasterappwatch.data.api.model.ride.RideModel
import com.jmr.coasterappwatch.data.api.model.ride.RideModelImpl
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Singleton
    @Provides
    fun provideCompanyModel(
        repository: QueueRepository
    ): CompanyModel = CompanyModelImpl(repository)

    @Singleton
    @Provides
    fun provideParkModel(
        repository: QueueRepository
    ): ParkModel = ParkModelImpl(repository)

    @Singleton
    @Provides
    fun provideCoasterModel(
        repository: QueueRepository
    ): CoasterModel = CoasterModelImpl(repository)

    @Singleton
    @Provides
    fun provideRideModel(
        repository: QueueRepository
    ): RideModel = RideModelImpl(repository)

}