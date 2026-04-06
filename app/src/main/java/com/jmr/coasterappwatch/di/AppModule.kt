package com.jmr.coasterappwatch.di

import android.content.Context
import com.jmr.coasterappwatch.data.api.AppUrl
import com.jmr.coasterappwatch.data.api.service.MockApiService
import com.jmr.coasterappwatch.data.api.service.QueueApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQueueApiService(
        okHttpClient: OkHttpClient
    ): QueueApiService {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        return Retrofit.Builder()
            .baseUrl(AppUrl.BASE_QUEUE_URL)
            .client(okHttpClient)
            // Usamos Kotlinx Serialization en lugar de Gson
            .addConverterFactory(json.asConverterFactory(contentType))
            // ELIMINADO: RxJava2CallAdapterFactory (No hace falta para Coroutines)
            .build()
            .create(QueueApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "17272049-aba4-43f9-be9c-c086347e1ec6")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }.build()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideMockApiService(
        @ApplicationContext context: Context
    ): MockApiService = MockApiService(context)
}