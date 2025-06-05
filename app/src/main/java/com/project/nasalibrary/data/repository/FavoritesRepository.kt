package com.project.nasalibrary.data.repository


import com.project.nasalibrary.data.datasource.LocalDataSource
import com.project.nasalibrary.model.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class FavoritesRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    suspend fun addFavoriteItem(item: Item) {
        localDataSource.insertFavoriteItem(item)
    }

    suspend fun removeFavoriteItem(item: Item) {
        localDataSource.deleteFavoriteItem(item)
    }


    fun getAllFavoriteItems(): Flow<List<Item>> {
        return localDataSource.getAllFavoriteItems()
    }

    suspend fun getFavoriteItemByHref(href: String): Item? {
        return localDataSource.getFavoriteItemByHref(href)
    }

    fun isItemFavorite(href: String): Flow<Boolean> {
        return localDataSource.isItemFavorite(href)
    }

}
