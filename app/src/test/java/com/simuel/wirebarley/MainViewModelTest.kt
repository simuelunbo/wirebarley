package com.simuel.wirebarley

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.simuel.domain.model.Quote
import com.simuel.domain.usecase.GetCurrencyUseCase
import com.simuel.wirebarley.util.CountryState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class MainViewModelTest {

    private val useCase: GetCurrencyUseCase = mockk()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `환율 데이터 현재 초단위 시간을 가져 왔을때 현재 날짜, 시간 값으로 변환 해준다`() {
        // given
        val quote = Quote(1121.42, 110.959498, 52.72027, 1678498323)
        coEvery { useCase() } returns Result.success(quote)
        val viewModel = MainViewModel(useCase)

        // when
        viewModel.getCurrentTime(quote.timestamp)

        // then
        val expected = "2023-03-11 10:32"
        val actual = viewModel.currentTime.getOrAwaitValue().toString()
        assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun `나라를 선택 했을때 나라 상태 값이 바뀐다`() {
        // given
        val quote = Quote(1121.42, 110.959498, 52.72027, 1678498323)
        coEvery { useCase() } returns Result.success(quote)
        val viewModel = MainViewModel(useCase)

        // when
        viewModel.updateSelected(CountryState.Korea.name)

        // then
        val expected = CountryState.Korea::class.java

        val actual = viewModel.country.getOrAwaitValue()
        assertThat(actual).isInstanceOf(expected)
    }

    @Test
    fun `나라가 변경 되었을 때 해당 나라의 환율 값으로 변경 된다`() {
        // given
        val quote = Quote(1121.42, 110.959498, 52.72027, 1678498323)
        coEvery { useCase() } returns Result.success(quote)
        val viewModel = MainViewModel(useCase)

        // when
        viewModel.changeCountryCurrencyRate("한국 (KRW)")

        // then
        val expected = "1,121.42 한국 (KRW) / USD"
        val actual = viewModel.currencyRate.getOrAwaitValue().toString()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `송금액이 10000 초과 하면 에러 메시지가 나온다`() {
        // given
        val quote = Quote(1121.42, 110.959498, 52.72027, 1678498323)
        coEvery { useCase() } returns Result.success(quote)
        val viewModel = MainViewModel(useCase)

        // when
        viewModel.checkRemittance(100000)

        // then
        val expected = "송금액이 바르지 않습니다."
        val actual = viewModel.error.getOrAwaitValue().peek()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `송금액을 입력 하였을 때 수취 금액을 출력 해준다`() {
        // given
        val quote = Quote(1121.42, 110.959498, 52.72027, 1678498323)
        coEvery { useCase() } returns Result.success(quote)
        val viewModel = MainViewModel(useCase)

        // when
        viewModel.calculateReceipt(10)

        // then
        val expected = "11,214.20"
        val actual = viewModel.receivedResult.getOrAwaitValue().toString()
        assertThat(actual).isEqualTo(expected)
    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {

            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}