package com.jmr.coasterappwatch.domain.model


data class Company(
    val id: Int? = 0,
    val name: String ? = null,
    val parkList: List<Park>? = null,
)
