package com.woowahan.data.entity

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