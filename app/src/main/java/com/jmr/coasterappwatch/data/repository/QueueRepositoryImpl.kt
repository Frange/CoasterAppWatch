package com.jmr.coasterappwatch.data.repository

import com.google.gson.Gson
import com.jmr.coasterappwatch.data.api.model.coaster.toCoaster
import com.jmr.coasterappwatch.data.api.model.park.response.ResponseParkList
import com.jmr.coasterappwatch.data.api.model.park.response.toCompany
import com.jmr.coasterappwatch.data.api.model.service.QueueApiService
import com.jmr.coasterappwatch.domain.model.Coaster
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.Ride
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class QueueRepositoryImpl @Inject constructor(
    private val gson: Gson,
    private val service: QueueApiService
) : QueueRepository {

    companion object {
        const val COMPANY_PARQUES_REUNIDOS = "Parques Reunidos"
        const val PARK_WARNER = "Parque Warner Madrid"
    }

    private var companyList: List<Company> = arrayListOf()
    private var parkList: List<Park> = arrayListOf()
    private var coaster: Coaster = Coaster(arrayListOf(), arrayListOf())
    private lateinit var rideList: List<Ride>

    override fun requestCompanyList() = flow {
        emit(AppResult.loading())
        try {
            val response = service.requestCompanyList()
            val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
            companyList = searchAndSortCompany(formattedResponse.list?.map { it.toCompany() }!!)

            emit(AppResult.success(companyList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(position: Int) = flow {
        emit(AppResult.loading())
        try {
            parkList = searchAndSortPark(companyList[position].parkList!!)
            emit(AppResult.success(parkList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestCoaster(position: Int, sortedByTime: Boolean) = flow {
        emit(AppResult.loading())
        try {
            val id = parkList[position].id!!
            val response = service.requestCoasters(id)
            coaster = response.toCoaster()

            val sortedList = if (sortedByTime) coaster.rideList?.sortedBy { it.waitTime }
            else sortCoasterByStar(coaster.rideList)

            coaster.rideList = sortedList

            emit(AppResult.success(coaster))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestRideList() = flow {
        emit(AppResult.loading())
        try {
            rideList = coaster.rideList!!
            emit(AppResult.success(rideList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }


    override fun getCurrentCompanyList(): List<Company> {
        return companyList
    }

    override fun getCurrentParkList(): List<Park> {
        return parkList
    }

    override fun getCurrentCoasterList(): Coaster {
        return coaster
    }

    private fun searchAndSortCompany(list: List<Company>): List<Company> {
        return if (list.isNotEmpty()) {
            val sortedList = list.sortedBy { it.name }
            val index = sortedList.indexOfFirst { it.name == COMPANY_PARQUES_REUNIDOS }
            if (index != -1) {
                mutableListOf(sortedList[index]).apply {
                    addAll(sortedList.filterNot { it.name == COMPANY_PARQUES_REUNIDOS })
                }
            } else {
                sortedList
            }
        } else {
            list
        }
    }

    private fun searchAndSortPark(list: List<Park>): List<Park> {
        return if (list.isNotEmpty()) {
            val sortedList = list.sortedBy { it.name }
            val index = sortedList.indexOfFirst { it.name == PARK_WARNER }
            if (index != -1) {
                mutableListOf(sortedList[index]).apply {
                    addAll(sortedList.filterNot { it.name == PARK_WARNER })
                }
            } else {
                sortedList
            }
        } else {
            list
        }
    }

    private fun cleanNameList(rideList: List<Ride>?): List<Ride>? {
        rideList?.forEach { cleanName(it.name) }

        return rideList
    }

    private fun cleanName(name: String?): String? {
        return when (name) {
            "Batman Gotham City Escape" -> "Gotham City Escape"
            "BATMAN: Arkham Asylum" -> "Arkham Asylum"
            "SUPERMAN™: La Atracción de Acero" -> "Superman"
            "Coaster Express" -> "Coaster Express (madera)"

            else -> name
        }
    }

    private fun sortCoasterByStar(rideList: List<Ride>?): List<Ride> {
        if (!rideList.isNullOrEmpty()) {
            val priorityList = listOf(
                "Batman Gotham City Escape",
                "BATMAN: Arkham Asylum",
                "SUPERMAN™: La Atracción de Acero",
                "Stunt Fall",
                "Coaster Express",
                "La Venganza del ENIGMA"
            )

            val withPriorityList = rideList.filter { it.name in priorityList }
            val withoutPriorityList = rideList.filter { it.name !in priorityList }

            return withPriorityList + withoutPriorityList
        } else {
            return arrayListOf()
        }
    }
}