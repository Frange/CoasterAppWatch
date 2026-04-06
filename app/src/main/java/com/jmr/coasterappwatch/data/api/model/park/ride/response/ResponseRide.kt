package com.jmr.coasterappwatch.data.api.model.park.ride.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.jmr.coasterappwatch.domain.model.Ride

@Serializable
data class ResponseRide(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("is_open") val is_open: Boolean,
    @SerialName("wait_time") val wait_time: Int?, // Nota el '?' por si viene nulo
    @SerialName("last_updated") val last_updated: String?
)

fun ResponseRide.toRide() = Ride(
    id = id,
    name = name,
    isOpen = is_open && wait_time!! > 1,
    waitTime = wait_time ?: 0,
    lastUpdated = last_updated ?: "",
    isFavourite = false
)