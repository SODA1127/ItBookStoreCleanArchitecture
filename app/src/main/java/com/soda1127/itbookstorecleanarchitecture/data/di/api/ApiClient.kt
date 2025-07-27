package com.soda1127.itbookstorecleanarchitecture.data.di.api

import com.google.gson.Gson
import com.soda1127.itbookstorecleanarchitecture.url.BookStoreUrl.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val gson: Gson
) {

    private fun createApiAdapter() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiAdapter by lazy { createApiAdapter() }

    fun provideBookStoreApi(): BooksApiService =
        apiAdapter.create(BooksApiService::class.java)

}
