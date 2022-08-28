package com.woowahan.domain.model

import java.io.IOException
import java.net.SocketTimeoutException

object ThrowableUtil {
    private const val socketTimeoutMessage = "요청 시간이 초과되었습니다"
    private const val noConnectivityIOExceptionMessage = "네트워크 상태가 올바르지 않습니다"
    private const val apiBodyIsNullMessage = "응답 데이터가 올바르지 않습니다"
    private const val apiStatusCodeNotOkMessage = "응답 코드가 올바르지 않습니다"
    private const val unKnownMessage = "알 수 없는 오류입니다"

    fun throwableToMessage(throwable: Throwable): String {
        return when(throwable){
            is NoConnectivityIOException -> noConnectivityIOExceptionMessage
            is ApiBodyIsNull -> apiBodyIsNullMessage
            is ApiStatusCodeNotOk -> apiStatusCodeNotOkMessage
            is SocketTimeoutException -> socketTimeoutMessage
            else -> throwable.message ?: unKnownMessage
        }
    }
}

data class ApiStatusCodeNotOk(
    private val statusCode: Int?
): Throwable() {
    override val message = "api response status code is not 200[$statusCode]"
}

class ApiBodyIsNull: Throwable() {
    override val message = "api response body is null"
}

data class ApiIsNotSuccessful(
    override val message: String
): Throwable()

data class NoConnectivityIOException(
    override val message: String = "네트워크 상태가 올바르지 않습니다"
): IOException()
