package com.example.sfmovies.util

sealed class State<T> {

    data class Success<T>(val data: T) : State<T>()
    data class Error<T>(val message: String) : State<T>()
    class Loading<T> : State<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> loading() = Loading<T>()
        fun <T> error(message: String) = Error<T>(message)
    }
}