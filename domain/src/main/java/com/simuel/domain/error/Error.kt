package com.simuel.domain.error

sealed class Error(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class DataNotFound(
        override val message: String?
    ) : Error(message)

    object NetworkUnavailable : Error()
}
