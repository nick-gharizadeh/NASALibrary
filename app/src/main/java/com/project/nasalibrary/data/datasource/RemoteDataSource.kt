package com.project.nasalibrary.data.datasource

import com.project.nasalibrary.data.network.ApiServices
import javax.inject.Inject


class RemoteDataSource @Inject constructor(private val api: ApiServices) {

        suspend fun search(query: String) = api.search(query)
        suspend fun getPopularItems() = api.getPopularItems()


}
