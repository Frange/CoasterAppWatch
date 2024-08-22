package com.jmr.coasterappwatch.domain.model

data class Land(
    val id: Int,
    val name: String,
    val rideList: List<Ride>?
)