package com.jmr.coasterappwatch.di

import android.content.Context
import com.google.gson.Gson
import com.jmr.coasterappwatch.data.api.AppUrl
import com.jmr.coasterappwatch.data.api.service.MockApiService
import com.jmr.coasterappwatch.data.api.service.QueueApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQueueApiService(
        okHttpClient: OkHttpClient
    ): QueueApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppUrl.BASE_QUEUE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(QueueApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(Interceptor {
                val token = "17272049-aba4-43f9-be9c-c086347e1ec6"
                val newRequest = it.request().newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                it.proceed(newRequest)
            }).build()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun provideMockApiService(
        @ApplicationContext context: Context
    ): MockApiService {
        return MockApiService(context)
    }

    @Singleton
    @Provides
    fun providesGson() = Gson()

}