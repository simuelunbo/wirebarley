package com.simuel.data.di

import com.simuel.data.remote.api.WireBarleyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    private const val TIMEOUT_SECONDS = 15L
    private const val BASE_URL = "https://api.apilayer.com/currency_data/"
    private const val APIKEY = "GJUaXQw0az0XL5KFz9HcEX6rmtXUbo3J"

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): WireBarleyService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).addInterceptor(Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("apikey", APIKEY)
                .build()
            chain.proceed(request)
        }).readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
}