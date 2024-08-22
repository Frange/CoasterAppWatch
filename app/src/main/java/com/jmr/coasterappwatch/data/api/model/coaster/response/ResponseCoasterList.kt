package com.jmr.coasterappwatch.data.api.model.coaster.response

import com.jmr.coasterappwatch.data.api.model.coaster.response.inner.ResponseCoasterData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseCoasterList(
    @Json(name = "list")
    var list: List<ResponseCoasterData>?
)