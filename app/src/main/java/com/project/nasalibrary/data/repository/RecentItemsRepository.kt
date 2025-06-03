package com.project.nasalibrary.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.project.nasalibrary.data.datasource.RemoteDataSource
import com.project.nasalibrary.paging.ManualTriggerPagingSource
import javax.inject.Inject

class RecentItemsRepository @Inject constructor(private val remote: RemoteDataSource) {
    fun getRecentItems() = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            ManualTriggerPagingSource(
                fetchApi = { remote.getRecentItems() },
                extractItems = { it.collection.items }
            )
        }
    ).flow
}