package com.simuel.data.remote.api

import com.simuel.data.remote.model.CurrencyDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WireBarleyService {

    @GET("live")
    suspend fun getCurrencyData(
        @Query("currencies") currencies: String = "KRW, JPY, PHP"
    ) : CurrencyDataResponse
}