package com.jmr.coasterappwatch.data.api.model.parkinfo.response

import com.jmr.coasterappwatch.domain.model.ParkInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ResponseParkList(
    @SerialName("list")
    val list: List<ResponseParkData>? = emptyList()
)

fun ResponseParkList.toDomain(): List<ParkInfo> {
    return list?.flatMap { responseData ->
        responseData.parks
    } ?: emptyList()
}