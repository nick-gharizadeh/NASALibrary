package com.project.nasalibrary.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.nasalibrary.model.Item
import retrofit2.Response
import java.io.IOException

class LoadNextPageException : Exception()

class ManualTriggerPagingSource<T : Any>(
    private val fetchApi: suspend () -> Response<T>,
    private val extractItems: (T) -> List<Item>
) : PagingSource<Int, Item>() {

    private var itemList: List<Item> = emptyList()

    // This flag determines if we should throw our custom error to pause loading.
    private var shouldThrowError = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val pageNumber = params.key ?: 1

        if (pageNumber > 1 && shouldThrowError) {
            shouldThrowError = false
            return LoadResult.Error(LoadNextPageException())
        }


        return try {
            if (itemList.isEmpty()) {
                val response = fetchApi()
                if (response.isSuccessful) {
                    response.body()?.let { itemList = extractItems(it) }
                } else {
                    return LoadResult.Error(Exception("API Error: ${response.code()}"))
                }
            }

            val pageSize = 10
            val fromIndex = (pageNumber - 1) * pageSize

            if (fromIndex >= itemList.size) {
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }

            val toIndex = minOf(fromIndex + pageSize, itemList.size)
            val pagedData = itemList.subList(fromIndex, toIndex)

            val nextKey = if (toIndex >= itemList.size) {
                null
            } else {
                pageNumber + 1
            }

            shouldThrowError = true

            LoadResult.Page(
                data = pagedData,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: IOException) {
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