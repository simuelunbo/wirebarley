package com.simuel.domain.repository

import com.simuel.domain.model.Quote

interface CurrencyRepository {
    suspend fun getCurrency(): Result<Quote>
}