package com.jmr.coasterappwatch.di

import com.jmr.coasterappwatch.data.api.service.QueueApiService
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.data.repository.queue.QueueRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideQueueRepository(
        service: QueueApiService
    ): QueueRepository = QueueRepositoryImpl(service)

}