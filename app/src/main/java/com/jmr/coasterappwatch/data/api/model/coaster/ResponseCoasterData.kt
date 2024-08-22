package com.jmr.coasterappwatch.data.api.model.coaster

import com.jmr.coasterappwatch.data.api.model.coaster.response.inner.ResponseLand
import com.jmr.coasterappwatch.data.api.model.coaster.response.inner.ResponseRide
import com.jmr.coasterappwatch.data.api.model.coaster.response.inner.toLand
import com.jmr.coasterappwatch.data.api.model.coaster.response.inner.toRide
import com.jmr.coasterappwatch.domain.model.Coaster
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseCoasterData(
    @Json(name = "rides")
    var rides: List<ResponseRide>,

    @Json(name = "lands")
    var lands: List<ResponseLand>,
)

fun ResponseCoasterData.toCoaster() = Coaster(
    rideList = rides.map { it.toRide() },
    landList = lands.map { it.toLand() }
)