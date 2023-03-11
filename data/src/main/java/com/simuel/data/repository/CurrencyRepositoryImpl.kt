package com.simuel.data.repository

import com.simuel.data.error.ErrorData
import com.simuel.data.mapper.toDomain
import com.simuel.data.remote.api.WireBarleyService
import com.simuel.domain.model.Quote
import com.simuel.domain.repository.CurrencyRepository
import java.net.UnknownHostException
import javax.inject.Inject

internal class CurrencyRepositoryImpl @Inject constructor(
    private val service: WireBarleyService
) : CurrencyRepository {
    override suspend fun getCurrency(): Result<Quote> {
        val result = runCatching { service.getCurrencyData().toDomain() }
        return when (val exception = result.exceptionOrNull()) {
            null -> result
            is UnknownHostException -> Result.failure(ErrorData.NetworkUnavailable.toDomain())
            else -> Result.failure(ErrorData.DataNotFound(exception.message).toDomain())
        }
    }
}