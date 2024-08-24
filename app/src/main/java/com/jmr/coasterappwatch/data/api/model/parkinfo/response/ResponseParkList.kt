package com.jmr.coasterappwatch.data.api.model.parkinfo.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseParkList(
    @Json(name = "list")
    var list: List<ResponseParkData>?
)