package com.jmr.coasterappwatch.di

import android.app.Application
import com.google.gson.Gson
import com.jmr.coasterappwatch.data.api.service.MockApiService
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
    fun providePoiRepository(
        application: Application,
        gson: Gson,
        service: QueueApiService,
        mockApiService: MockApiService
    ): QueueRepository = QueueRepositoryImpl(application, gson, service, mockApiService)

}