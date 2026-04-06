package com.jmr.coasterappwatch.data.repository.queue

import com.jmr.coasterappwatch.data.api.model.park.toPark
import com.jmr.coasterappwatch.data.api.service.QueueApiService
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val service: QueueApiService
) : QueueRepository {

    private val priorityNames = listOf("Stunt Fall", "Superman", "Batman")

    override fun requestAllParkList(): Flow<AppResult<List<ParkInfo>>> = flow {
        emit(AppResult.Loading())

        try {
            val response = service.requestCompanyList()
            val allParks = response.flatMap { company ->
                company.parks
            }.sortedBy { it.name }

            emit(AppResult.Success(allParks))
        } catch (e: Exception) {
            emit(AppResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(id: Int): Flow<AppResult<Park>> = flow {
        emit(AppResult.Loading())
        try {
            val parkResponse = service.requestPark(id).toPark()
            val processedRide = processRides(parkResponse.rideList)
            val processedLands = parkResponse.landList.map { land ->
                land.copy(rideList = processRides(land.rideList))
            }

            emit(
                AppResult.Success(
                    parkResponse.copy(
                        rideList = processedRide,
                        landList = processedLands
                    )
                )
            )
        } catch (e: Exception) {
            emit(AppResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun processRides(rides: List<Ride>): List<Ride> {
        return rides.map { ride ->
            ride.copy(isFavourite = priorityNames.contains(ride.name))
        }.sortedWith(compareByDescending<Ride> { it.isFavourite }.thenBy { it.name })
    }
}