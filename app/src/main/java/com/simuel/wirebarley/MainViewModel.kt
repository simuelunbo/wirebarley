package com.simuel.wirebarley

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simuel.domain.model.Quote
import com.simuel.domain.usecase.GetCurrencyUseCase
import com.simuel.wirebarley.util.CountryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: GetCurrencyUseCase
) : ViewModel() {
    private val currency = MutableLiveData<Quote>()

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String>
        get() = _currentTime

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>>
        get() = _error

    private val _country = MutableLiveData<CountryState>(CountryState.Korea)
    val country: LiveData<CountryState>
        get() = _country

    private val _currencyRate = MutableLiveData<String>()
    val currencyRate: LiveData<String>
        get() = _currencyRate

    private val _receivedResult = MutableLiveData<String?>("0")
    val receivedResult: LiveData<String?>
        get() = _receivedResult

    val remittance = MutableLiveData("0")

    init {
        getCurrency()
    }

    private fun getCurrency() = viewModelScope.launch {
        useCase().onSuccess {
            currency.value = it
            getCurrentTime(it.timestamp)
            setCountryCurrencyRate(_country.value ?: CountryState.Korea)
        }.onFailure {
            val msg = it.message ?: "알수 없는 에러"
            _error.value = Event(msg)
        }
    }

    fun changeCountryCurrencyRate(country: String) {
        when (country) {
            CountryState.Korea.name -> _currencyRate.value =
                "${currency.value?.usdKrw?.changeFormat()} $country / USD"
            CountryState.Japan.name -> _currencyRate.value =
                "${currency.value?.usdJpy?.changeFormat()} $country / USD"
            CountryState.Philippines.name -> _currencyRate.value =
                "${currency.value?.usdPhp?.changeFormat()} $country / USD"
        }
    }

    private fun setCountryCurrencyRate(country: CountryState) {
        when (country) {
            is CountryState.Korea -> _currencyRate.value =
                "${currency.value?.usdKrw?.changeFormat()} ${country.name} / USD"
            is CountryState.Japan -> _currencyRate.value =
                "${currency.value?.usdJpy?.changeFormat()} ${country.name} / USD"
            is CountryState.Philippines -> _currencyRate.value =
                "${currency.value?.usdPhp?.changeFormat()} ${country.name} / USD"
        }
    }

    fun updateSelected(country: String) {
        when (country) {
            CountryState.Korea.name -> _country.value = CountryState.Korea
            CountryState.Japan.name -> _country.value = CountryState.Japan
            CountryState.Philippines.name -> _country.value = CountryState.Philippines
        }
    }

    fun calculateReceipt(amount: Int) {
        if (!checkRemittance(amount) || currency.value == null) {
            return
        }
        remittance.value = amount.toString()
        when (_country.value ?: CountryState.Korea) {
            is CountryState.Korea -> _receivedResult.value =
                (amount * (currency.value?.usdKrw!!)).changeFormat()
            is CountryState.Japan -> _receivedResult.value =
                (amount * (currency.value?.usdJpy!!)).changeFormat()
            is CountryState.Philippines -> _receivedResult.value =
                (amount * (currency.value?.usdPhp!!)).changeFormat()
        }
    }

    fun checkRemittance(dollar: Int): Boolean {
        if (dollar < 0 || dollar > 10000) {
            val msg = "송금액이 바르지 않습니다."
            _error.value = Event(msg)
            return false
        }
        return true
    }


    fun getCurrentTime(time: Long) {
        val date = Date(time * 1000L)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
        _currentTime.value = dateFormat.format(date)
    }

    private fun Double.changeFormat(): String {
        if (this == 0.00) {
            return "0"
        }
        return DecimalFormat("#,###.00").format(this)
    }
}
