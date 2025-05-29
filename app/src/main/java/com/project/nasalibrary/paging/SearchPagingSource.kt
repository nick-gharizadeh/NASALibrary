package com.project.nasalibrary.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.nasalibrary.data.datasource.RemoteDataSource
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.Constants
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val query: String
) : PagingSource<Int, Item>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val pageIndex = params.key ?: Constants.STARTING_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            val response = remoteDataSource.search(
                query = query,
                page = pageIndex,
                pageSize = Constants.NETWORK_PAGE_SIZE
            )

            if (response.isSuccessful) {
                val items = response.body()?.collection?.items ?: emptyList()
                val nextKey = if (items.size == Constants.NETWORK_PAGE_SIZE) {
                    pageIndex + 1
                } else {
                    null
                }
                val prevKey = if (pageIndex == Constants.STARTING_PAGE_INDEX) {
                    null
                } else {
                    pageIndex - 1
                }
                LoadResult.Page(
                    data = items,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}