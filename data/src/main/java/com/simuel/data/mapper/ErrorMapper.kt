package com.simuel.data.mapper

import com.simuel.data.error.ErrorData
import com.simuel.domain.error.Error


internal fun ErrorData.toDomain(): Error = when (this) {
    ErrorData.NetworkUnavailable -> Error.NetworkUnavailable
    is ErrorData.DataNotFound -> Error.DataNotFound(message)
}