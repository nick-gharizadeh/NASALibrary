package com.project.nasalibrary.data.network

import com.project.nasalibrary.model.AssetResponse
import com.project.nasalibrary.model.popular.PopularResponse
import com.project.nasalibrary.model.recent.RecentResponse
import com.project.nasalibrary.model.search.SearchResponse
import com.project.nasalibrary.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiServices {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("page_size") pageSize: Int = 15
    ): Response<SearchResponse>

    @GET(Constants.POPULAR_URL)
    suspend fun getPopularItems(): Response<PopularResponse>

    @GET(Constants.RECENT_URL)
    suspend fun getRecentItems(): Response<RecentResponse>

    @GET("/asset/{nasa_id}")
    suspend fun getAsset(@Path("nasa_id") nasaId: String): Response<AssetResponse>

}
