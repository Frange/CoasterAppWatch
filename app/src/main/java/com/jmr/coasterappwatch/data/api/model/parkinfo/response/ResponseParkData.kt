package com.jmr.coasterappwatch.data.api.model.parkinfo.response

import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseParkData(
    @Json(name = "id")
    var id: Int,

    @Json(name = "name")
    var name: String,

    @Json(name = "parks")
    var parks: List<ParkInfo>
)

fun ResponseParkData.toCompany() = Company(
    id = id,
    name = name,
    parks = parks
)
