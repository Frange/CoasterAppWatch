package com.jmr.coasterappwatch.data.repository.queue

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jmr.coasterappwatch.data.api.model.park.toPark
import com.jmr.coasterappwatch.data.api.model.parkinfo.response.ResponseParkList
import com.jmr.coasterappwatch.data.api.model.parkinfo.response.toCompany
import com.jmr.coasterappwatch.data.api.service.MockApiService
import com.jmr.coasterappwatch.data.api.service.QueueApiService
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Land
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.domain.model.Ride
import com.jmr.coasterappwatch.utils.priorityList
import com.jmr.coasterappwatch.utils.priorityOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val application: Application,
    private val gson: Gson,
    private val service: QueueApiService,
    private val mockService: MockApiService
) : QueueRepository {

    private var companyList: List<Company> = arrayListOf()
    private var parkInfoList: List<ParkInfo> = arrayListOf()
    private var park: Park = Park(arrayListOf(), arrayListOf())
    private lateinit var rideList: List<Ride>
    private lateinit var landList: List<Land>

    private val isMock = false

    override fun requestAllParkList() = flow {
        emit(AppResult.loading())

        try {
            val response =
                if (isMock)
                    mockService.requestMockCompanyList()
                else
                    service.requestCompanyList()

            val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
            companyList = searchAndSortCompany(formattedResponse.list?.map { it.toCompany() }!!)

            val allParkInfo = mutableListOf<ParkInfo>()

            companyList.forEach { company ->
                val parkInfoList = searchAndSortPark(company)
                allParkInfo.addAll(parkInfoList)
            }

            parkInfoList = sortFavouriteParkInfoList(allParkInfo)

            emit(AppResult.success(parkInfoList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(id: Int) = flow {
        emit(AppResult.loading())

        val response = service.requestPark(id)
        park = response.toPark()

        val rideList = mutableListOf<Ride>()
        if (!park.landList.isNullOrEmpty()) {
            park.landList!!.forEach {
                if (!it.rideList.isNullOrEmpty()) {
                    rideList.addAll(it.rideList)
                }
            }
        }
        park.rideList?.let { rideList.addAll(it) }

        val sortedList = sortFavouriteRides(rideList)

        park.rideList = sortedList

        emit(AppResult.success(park))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)


    override fun requestParkInfoList(position: Int) = flow {
        emit(AppResult.loading())

        parkInfoList = searchAndSortPark(companyList[position])

        emit(AppResult.success(parkInfoList))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun getCurrentCompanyList(): List<Company> {
        return companyList
    }

    override fun getCurrentParkList(): List<ParkInfo> {
        return parkInfoList
    }

    override fun getCurrentCoasterList(): Park {
        return park
    }

    private fun searchAndSortCompany(list: List<Company>): List<Company> {
        return if (list.isNotEmpty()) {
            list.sortedBy { it.name }
        } else {
            list
        }
    }

    private fun sortFavouriteParkInfoList(parkInfoList: List<ParkInfo>): List<ParkInfo> {
        val otherParks = parkInfoList.filter { it.name !in priorityOrder }
        val sortedPriorityParks = priorityOrder.mapNotNull { name ->
            parkInfoList.find { it.name == name }
        }
        return sortedPriorityParks + otherParks.sortedBy { it.name }
    }

    private fun searchAndSortPark(
        company: Company
    ): List<ParkInfo> {
        val list = company.parks
        return if (!list.isNullOrEmpty()) {
            list.sortedBy { it.name }
        } else {
            arrayListOf()
        }
    }

    private fun sortFavouriteRides(rideList: List<Ride>?): List<Ride> {
        if (!rideList.isNullOrEmpty()) {
            val sortedRides = mutableListOf<Ride>()

            for (rideName in priorityList) {
                val ride = rideList.find { it.name == rideName }
                ride?.let {
                    it.isFavourite = true
                    sortedRides.add(it)
                }
            }

            val ridesWithoutPriority = rideList.filter { it.name !in priorityList }
            sortedRides.addAll(ridesWithoutPriority)

            return sortedRides
        } else {
            return arrayListOf()
        }
    }

}
