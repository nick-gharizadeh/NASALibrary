package com.project.nasalibrary.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.nasalibrary.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteItem(item: Item)

    @Delete
    suspend fun deleteFavoriteItem(item: Item)

    @Query("SELECT * FROM favorite_items")
    fun getAllFavoriteItems(): Flow<List<Item>>

    @Query("SELECT * FROM favorite_items WHERE href = :href")
    suspend fun getFavoriteItemByHref(href: String): Item?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE href = :href LIMIT 1)")
    fun isItemFavorite(href: String): Flow<Boolean>
}