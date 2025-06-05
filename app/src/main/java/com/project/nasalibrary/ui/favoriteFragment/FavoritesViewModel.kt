package com.project.nasalibrary.ui.favoriteFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nasalibrary.data.repository.FavoritesRepository
import com.project.nasalibrary.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoritesRepository
) : ViewModel() {

    val allFavoriteItems: Flow<List<Item>> = favoriteRepository.getAllFavoriteItems()

    fun addFavorite(item: Item) {
        viewModelScope.launch {
            favoriteRepository.addFavoriteItem(item)
        }
    }

    fun removeFavorite(item: Item) {
        viewModelScope.launch {
            favoriteRepository.removeFavoriteItem(item)
        }
    }

    fun isItemFavorite(nasaId: String): Flow<Boolean> {
        return favoriteRepository.isItemFavorite(nasaId)
    }
}