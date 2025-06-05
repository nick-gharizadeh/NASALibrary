package com.project.nasalibrary.data.datasource


import com.project.nasalibrary.data.database.FavoriteDao
import com.project.nasalibrary.model.FavoriteItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val favoriteDao: FavoriteDao) {

    suspend fun addFavorite(favoriteItem: FavoriteItemEntity) {
        favoriteDao.addFavorite(favoriteItem)
    }

    suspend fun removeFavoriteById(nasaId: String) {
        favoriteDao.removeFavoriteById(nasaId)
    }

    fun getAllFavorites(): Flow<List<FavoriteItemEntity>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun getFavoriteById(nasaId: String): FavoriteItemEntity? {
        return favoriteDao.getFavoriteById(nasaId)
    }

    fun isFavorite(nasaId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(nasaId)
    }
}