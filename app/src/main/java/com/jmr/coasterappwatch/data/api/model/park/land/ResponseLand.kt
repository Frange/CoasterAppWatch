package com.jmr.coasterappwatch.data.api.model.park.land

import com.jmr.coasterappwatch.data.api.model.park.ride.response.ResponseRide
import com.jmr.coasterappwatch.data.api.model.park.ride.response.toRide
import com.jmr.coasterappwatch.domain.model.Land
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseLand(
    @Json(name = "id")
    var id: Int,

    @Json(name = "name")
    var name: String,

    @Json(name = "rides")
    var rides: List<ResponseRide>

)

fun ResponseLand.toLand() = Land(
    id = id,
    name = name,
    rideList = rides.map { it.toRide() }
)
