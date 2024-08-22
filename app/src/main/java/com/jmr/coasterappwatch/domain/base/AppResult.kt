package com.jmr.coasterappwatch.domain.base

sealed class AppResult<out T> {

    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val exception: Throwable) : AppResult<Nothing>()
    data class Loading(val isLoading: Boolean = true) : AppResult<Nothing>()
    data class Exception(val exception: Throwable) : AppResult<Nothing>()

    companion object {
        fun <T> success(data: T): AppResult<T> = Success(data)
        fun error(exception: Throwable): AppResult<Nothing> = Error(exception)
        fun loading(): AppResult<Nothing> = Loading()
        fun exception(exception: Throwable): AppResult<Nothing> = Exception(exception)
    }
}
