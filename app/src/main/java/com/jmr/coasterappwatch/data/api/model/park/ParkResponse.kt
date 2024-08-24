package com.jmr.coasterappwatch.data.api.model.park

import com.jmr.coasterappwatch.data.api.model.park.land.ResponseLand
import com.jmr.coasterappwatch.data.api.model.park.land.toLand
import com.jmr.coasterappwatch.data.api.model.park.ride.response.ResponseRide
import com.jmr.coasterappwatch.data.api.model.park.ride.response.toRide
import com.jmr.coasterappwatch.domain.model.Park
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParkResponse(
    @Json(name = "rides")
    var rides: List<ResponseRide>,

    @Json(name = "lands")
    var lands: List<ResponseLand>,
)

fun ParkResponse.toPark() = Park(
    rideList = rides.map { it.toRide() },
    landList = lands.map { it.toLand() }
)
