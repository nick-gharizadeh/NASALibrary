package com.project.nasalibrary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.nasalibrary.model.FavoriteItemEntity
import com.project.nasalibrary.utils.ListConverters

@Database(
    entities = [FavoriteItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}