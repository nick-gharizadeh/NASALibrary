package com.project.nasalibrary.data.repository


import com.project.nasalibrary.data.datasource.LocalDataSource
import com.project.nasalibrary.model.FavoriteItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    suspend fun addFavorite(item: FavoriteItemEntity) {
        localDataSource.addFavorite(item)
    }

    suspend fun removeFavoriteById(nasaId: String) {
        localDataSource.removeFavoriteById(nasaId)
    }

    fun getAllFavorites(): Flow<List<FavoriteItemEntity>> {
        return localDataSource.getAllFavorites()
    }

    suspend fun getFavoriteById(nasaId: String): FavoriteItemEntity? {
        return localDataSource.getFavoriteById(nasaId)
    }

    fun isFavorite(nasaId: String): Flow<Boolean> {
        return localDataSource.isFavorite(nasaId)
    }

}