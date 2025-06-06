package com.project.nasalibrary.data.repository

import com.project.nasalibrary.data.datasource.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.paging.SearchPagingSource
import com.project.nasalibrary.utils.Constants
import kotlinx.coroutines.flow.Flow

@ActivityRetainedScoped
class SearchRepository @Inject constructor(private val remote: RemoteDataSource) {

    fun search(query: String): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = Constants.NETWORK_PAGE_SIZE * 2
            ),
            pagingSourceFactory = { SearchPagingSource(remote, query) }
        ).flow
    }
}