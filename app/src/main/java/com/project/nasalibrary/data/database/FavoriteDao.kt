package com.project.nasalibrary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.nasalibrary.model.FavoriteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteItem: FavoriteItemEntity)

    @Query("DELETE FROM favorite_items WHERE nasaId = :nasaId")
    suspend fun removeFavoriteById(nasaId: String)

    @Query("SELECT * FROM favorite_items ORDER BY dateCreated DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Query("SELECT * FROM favorite_items WHERE nasaId = :nasaId")
    suspend fun getFavoriteById(nasaId: String): FavoriteItemEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE nasaId = :nasaId LIMIT 1)")
    fun isFavorite(nasaId: String): Flow<Boolean>
}