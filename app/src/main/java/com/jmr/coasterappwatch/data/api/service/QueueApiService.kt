package com.jmr.coasterappwatch.data.api.service

import com.jmr.coasterappwatch.data.api.model.park.ParkResponse
import com.jmr.coasterappwatch.data.api.model.parkinfo.response.ResponseParkData
import retrofit2.http.GET
import retrofit2.http.Path

interface QueueApiService {

    @GET("parks.json")
    suspend fun requestCompanyList(): List<ResponseParkData>

    @GET("parks/{id}/queue_times.json")
    suspend fun requestPark(
        @Path("id") parkId: Int = 298
    ): ParkResponse

}
