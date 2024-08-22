package com.jmr.coasterappwatch.data.api.model.service

import com.google.gson.JsonArray
import com.jmr.coasterappwatch.data.api.model.coaster.ResponseCoasterData
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface QueueApiService {

    @GET("parks.json")
    suspend fun requestCompanyList(): JsonArray

    @GET("parks/{id}/queue_times.json")
    suspend fun requestCoasters(
        @Path("id") parkId: Int = 298
    ): ResponseCoasterData

}

object ApiClient {
    private const val BASE_URL = "https://queue-times.com/"

    fun create(): QueueApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(QueueApiService::class.java)
    }
}