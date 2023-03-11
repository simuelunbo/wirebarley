package com.simuel.data.error

internal sealed class ErrorData(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class DataNotFound(
        override val message: String?
    ) : ErrorData(message)

    object NetworkUnavailable : ErrorData("네트워크 연결을 확인하세요")
}