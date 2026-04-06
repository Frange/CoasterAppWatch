package com.jmr.coasterappwatch.data.api.model.park

import com.jmr.coasterappwatch.data.api.model.park.land.ResponseLand
import com.jmr.coasterappwatch.data.api.model.park.land.toLand
import com.jmr.coasterappwatch.data.api.model.park.ride.response.ResponseRide
import com.jmr.coasterappwatch.data.api.model.park.ride.response.toRide
import com.jmr.coasterappwatch.domain.model.Park
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ParkResponse(
    @SerialName("rides")
    val rides: List<ResponseRide>? = emptyList(),

    @SerialName("lands")
    val lands: List<ResponseLand>? = emptyList(),
)

fun ParkResponse.toPark() = Park(
    rideList = rides?.map { it.toRide() } ?: emptyList(),
    landList = lands?.map { it.toLand() } ?: emptyList()
)