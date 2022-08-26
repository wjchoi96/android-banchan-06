package com.woowahan.domain.model

import java.io.IOException

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