package com.simuel.domain.usecase

import com.google.common.truth.Truth
import com.simuel.domain.model.Quote
import com.simuel.domain.repository.CurrencyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.DisplayName

@ExperimentalCoroutinesApi
class CurrencyUseCaseTest {
    private val repository: CurrencyRepository = mockk()

    @Test
    @DisplayName("해당 환율 데이터를 성공적으로 가져온다.")
    fun getQuoteSuccess() = runTest {
        // given
        val quote = Quote(1121.419945, 110.959498, 52.72027,1678498323)
        val useCase = GetCurrencyUseCase(repository)
        coEvery { repository.getCurrency() } returns Result.success(quote)
        // when
        val actual = useCase().getOrThrow()

        // then
        Truth.assertThat(actual).isEqualTo(quote)
    }
}