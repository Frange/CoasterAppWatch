package com.jmr.coasterappwatch.data.api.model.parkinfo.response

import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.ParkInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ResponseParkData(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("parks")
    val parks: List<ParkInfo> = emptyList()
)

fun ResponseParkData.toCompany() = Company(
    id = id,
    name = name,
    parks = parks
)