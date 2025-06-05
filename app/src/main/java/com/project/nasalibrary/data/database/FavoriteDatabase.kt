package com.project.nasalibrary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.AppTypeConvertors

@Database(
    entities = [Item::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppTypeConvertors::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteItemDao
}