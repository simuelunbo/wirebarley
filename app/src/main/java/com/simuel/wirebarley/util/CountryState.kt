package com.simuel.wirebarley.util

sealed class CountryState(val name: String){
    object Korea : CountryState("한국 (KRW)")
    object Japan : CountryState("일본 (JYP)")
    object Philippines: CountryState("필리핀 (PHP)")
}
