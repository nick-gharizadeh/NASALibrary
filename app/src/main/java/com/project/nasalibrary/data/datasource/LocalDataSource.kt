package com.project.nasalibrary.data.datasource


import com.project.nasalibrary.data.database.FavoriteItemDao
import com.project.nasalibrary.model.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val favoriteItemDao: FavoriteItemDao
) {

    suspend fun insertFavoriteItem(item: Item) {
        favoriteItemDao.insertFavoriteItem(item)
    }

    suspend fun deleteFavoriteItem(item: Item) {
        favoriteItemDao.deleteFavoriteItem(item)
    }


    fun getAllFavoriteItems(): Flow<List<Item>> {
        return favoriteItemDao.getAllFavoriteItems()
    }

    suspend fun getFavoriteItemByHref(href: String): Item? {
        return favoriteItemDao.getFavoriteItemByHref(href)
    }

    fun isItemFavorite(href: String): Flow<Boolean> {
        return favoriteItemDao.isItemFavorite(href)
    }
}