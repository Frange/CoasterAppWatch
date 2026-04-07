package com.jmr.coasterappwatch.data.repository.queue

import com.jmr.coasterappwatch.data.api.model.park.toPark
import com.jmr.coasterappwatch.data.api.service.QueueApiService
import com.jmr.coasterappwatch.data.store.FavoriteManager
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val service: QueueApiService,
    private val favoriteManager: FavoriteManager
) : QueueRepository {

    private val priorityNames = listOf("Stunt Fall", "Superman", "Batman")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun requestAllParkList(): Flow<AppResult<List<ParkInfo>>> =
        favoriteManager.favoriteParks
            .onStart { emit(emptySet()) }
            .flatMapLatest { favIds ->
                flow {
                    emit(AppResult.Loading())
                    try {
                        val response = service.requestCompanyList()
                        val allParks = response.flatMap { it.parks ?: emptyList() }
                            .map { park ->
                                park.copy(isFavorite = favIds.contains(park.id.toString()))
                            }
                            .sortedWith(compareByDescending<ParkInfo> { it.isFavorite }.thenBy { it.name })

                        emit(AppResult.Success(allParks))
                    } catch (e: Exception) {
                        emit(AppResult.Error(e))
                    }
                }
            }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun requestParkList(id: Int): Flow<AppResult<Park>> =
        favoriteManager.favoriteRides.flatMapLatest { favRides ->
            flow {
                emit(AppResult.Loading())
                try {
                    val parkResponse = service.requestPark(id).toPark()

                    // 1. Extraemos TODAS las atracciones (las sueltas + las que están dentro de lands)
                    val allRidesFromLands = parkResponse.landList.flatMap { it.rideList }
                    val combinedRides = (parkResponse.rideList + allRidesFromLands).distinctBy { it.id }

                    // 2. Procesamos la lista completa (Marcar favoritos + Ordenar una sola vez)
                    val finalSortedList = processRides(combinedRides, favRides)

                    // 3. Devolvemos el objeto Park con la lista unificada en rideList
                    // y landList vacío para evitar confusiones en la UI
                    emit(
                        AppResult.Success(
                            parkResponse.copy(
                                rideList = finalSortedList,
                                landList = emptyList() // Vaciamos lands para que la UI solo use rideList
                            )
                        )
                    )
                } catch (e: Exception) {
                    emit(AppResult.Error(e))
                }
            }
        }.flowOn(Dispatchers.IO)

    private fun processRides(rides: List<Ride>, favRides: Set<String>): List<Ride> {
        return rides.map { ride ->
            val isFav = favRides.contains(ride.id.toString()) || priorityNames.contains(ride.name)
            ride.copy(isFavourite = isFav)
        }.sortedWith(
            compareByDescending<Ride> { it.isFavourite }
                .thenBy { it.name }
        )
    }
}