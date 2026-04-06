package com.jmr.coasterappwatch.data.api.model.park.land

import com.jmr.coasterappwatch.data.api.model.park.ride.response.ResponseRide
import com.jmr.coasterappwatch.data.api.model.park.ride.response.toRide
import com.jmr.coasterappwatch.domain.model.Land // Asegúrate de importar tu modelo de dominio
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ResponseLand(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("rides")
    val rides: List<ResponseRide> = emptyList()
)

fun ResponseLand.toLand() = Land(
    id = this.id,
    name = this.name,
    rideList = this.rides.map { it.toRide() }
)