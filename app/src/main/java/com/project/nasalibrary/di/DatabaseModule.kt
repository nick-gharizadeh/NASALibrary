package com.project.nasalibrary.di

import android.content.Context
import androidx.room.Room
import com.project.nasalibrary.data.database.FavoriteDao
import com.project.nasalibrary.data.database.FavoriteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Volatile
    private var INSTANCE: FavoriteDatabase? = null

    @Provides
    @Singleton
    fun provideFavoriteDatabase(@ApplicationContext context: Context): FavoriteDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                FavoriteDatabase::class.java,
                "favorite_item_database"
            )
                .fallbackToDestructiveMigration() // Choose a migration strategy
                .build()
            INSTANCE = instance
            instance
        }
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: FavoriteDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}