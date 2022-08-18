package com.woowahan.domain.model

sealed class DomainEvent<T> {
    data class Success<T>(val data: T): DomainEvent<T>()
    data class Failure<T>(
        val throwable: Throwable,
        val data: T? = null
    ): DomainEvent<T>()

    companion object {
        fun <T>success(data: T): Success<T> = Success(data)
        fun <T>failure(throwable: Throwable, data: T? = null): Failure<T> = Failure(throwable, data)
    }

    inline fun onSuccess(block: (T) -> Unit): DomainEvent<T> {
        when(this){
            is Success -> block(data)
            else -> {}
        }
        return this
    }

    inline fun onFailure(block: (Throwable) -> Unit): DomainEvent<T> {
        when(this){
            is Failure -> block(throwable)
            else -> {}
        }
        return this
    }

    inline fun onFailureWithData(block: (Throwable, T?) -> Unit): DomainEvent<T> {
        when(this){
            is Failure -> block(throwable, data)
            else -> {}
        }
        return this
    }

}