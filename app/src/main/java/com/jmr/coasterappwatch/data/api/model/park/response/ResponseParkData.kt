package com.jmr.coasterappwatch.data.api.model.park.response

import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseParkData(
    @Json(name = "id")
    var id: Int,

    @Json(name = "name")
    var name: String,

    @Json(name = "parks")
    var parks: List<Park>
)

fun ResponseParkData.toCompany() = Company(
    id = id,
    name = name,
    parkList = parks
)
