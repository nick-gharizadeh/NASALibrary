package com.project.nasalibrary.ui.homeFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.project.nasalibrary.data.repository.PopularItemsRepository
import com.project.nasalibrary.data.repository.RecentItemsRepository
import com.project.nasalibrary.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    popularRepository: PopularItemsRepository,
    recentRepository: RecentItemsRepository
) : ViewModel() {

    val popularItemsData: Flow<PagingData<Item>> =
        popularRepository.getPopularItems().cachedIn(viewModelScope)

    val recentItemsData: Flow<PagingData<Item>> =
        recentRepository.getRecentItems().cachedIn(viewModelScope)
}
