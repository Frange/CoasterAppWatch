package com.jmr.coasterappwatch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ride(
    val id: Int,
    val name: String,
    val isOpen: Boolean,
    val waitTime: Int? = 0,
    val isFavourite: Boolean = false,
    val lastUpdated: String? = ""
)

@Serializable
data class Land(
    val id: Int,
    val name: String,
    val rideList: List<Ride> = emptyList()
)

@Serializable
data class Park(
    val id: Int = 0,
    val name: String= "",
    val landList: List<Land> = emptyList(),
    val rideList: List<Ride> = emptyList()
)

@Serializable
data class Company(
    val id: Int? = 0,
    val name: String? = null,
    val parks: List<ParkInfo>? = null,
)

@Serializable
data class ParkInfo(
    val id: Int? = 0,
    val name: String,
    val country: String,
    val continent: String,
    val latitude: String,
    val longitude: String,
    val timezone: String
)