package com.project.nasalibrary.data.network

import com.project.nasalibrary.model.search.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {
    @GET("search")
    suspend fun search(@Query("q") query: String): Response<SearchResponse>
}
