package com.simuel.data.mapper

import com.simuel.data.remote.model.CurrencyDataResponse
import com.simuel.domain.model.Quote

internal fun CurrencyDataResponse.toDomain(): Quote = Quote(
    timestamp= timestamp,
    usdKrw = quotes.usdKrw,
    usdJpy = quotes.usdJpy,
    usdPhp = quotes.usdPhp
)