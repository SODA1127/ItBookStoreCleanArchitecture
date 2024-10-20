package com.soda1127.itbookstorecleanarchitecture.data

import com.soda1127.itbookstorecleanarchitecture.data.reponse.BookSearchResultResponse
import com.soda1127.itbookstorecleanarchitecture.data.reponse.BookStoreNewResponse
import com.soda1127.itbookstorecleanarchitecture.url.BookStoreUrl
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


/**
 *

"error": "0",
"total": "26",
"page": "1",
"books": [
{
"title": "Learn Android Studio 3 with Kotlin",
"subtitle": "Efficient Android App Development",
"isbn13": "9781484239063",
"price": "$26.99",
"image": "https://itbook.store/img/books/9781484239063.png",
"url": "https://itbook.store/books/9781484239063"
},
 */

interface BookStoreApi {

    @GET(BookStoreUrl.EndPoint.NEW)
    suspend fun getNewBooks(): Response<BookStoreNewResponse>

    @GET(BookStoreUrl.EndPoint.SEARCH)
    suspend fun searchBooksByKeyword(@Path("query") keyword: String): Response<BookSearchResultResponse>

}
