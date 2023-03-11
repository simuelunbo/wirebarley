package com.simuel.domain.error

sealed class ErrorData(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class DataNotFound(
        override val message: String
    ) : ErrorData(message)

    object NetworkUnavailable : ErrorData()
}
