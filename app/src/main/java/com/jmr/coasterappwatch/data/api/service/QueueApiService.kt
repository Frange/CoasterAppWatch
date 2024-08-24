package com.jmr.coasterappwatch.data.api.service

import com.google.gson.JsonArray
import com.jmr.coasterappwatch.data.api.model.park.ParkResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface QueueApiService {

    @GET("parks.json")
    suspend fun requestCompanyList(): JsonArray

    @GET("parks/{id}/queue_times.json")
    suspend fun requestPark(
        @Path("id") parkId: Int = 298
    ): ParkResponse

}
