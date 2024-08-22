package com.jmr.coasterappwatch.domain.model


data class Ride(
    val id: Int?,
    val name: String?,
    val isOpen: Boolean,
    val waitTime: Int?,
    val lastUpdated: String
)
