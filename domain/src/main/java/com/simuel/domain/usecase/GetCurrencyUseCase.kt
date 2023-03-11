package com.simuel.domain.usecase

import com.simuel.domain.model.Quote
import com.simuel.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
){
    suspend operator fun invoke(): Result<Quote> = runCatching {
        repository.getCurrency().getOrThrow()
    }
}