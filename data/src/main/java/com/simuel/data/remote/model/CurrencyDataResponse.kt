package com.simuel.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class CurrencyDataResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("timestamp")
    var timestamp: Long,
    @SerializedName("source")
    var source: String,
    @SerializedName("quotes")
    var quotes: Quotes
)

data class Quotes(
    @SerializedName("USDKRW")
    val usdKrw: Double,
    @SerializedName("USDJPY")
    val usdJpy: Double,
    @SerializedName("USDPHP")
    val usdPhp: Double,
)