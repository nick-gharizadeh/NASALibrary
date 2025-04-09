package com.project.nasalibrary.ui.homeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nasalibrary.data.repository.PopularItemsRepository
import com.project.nasalibrary.data.repository.RecentItemsRepository
import com.project.nasalibrary.model.popular.PopularResponse
import com.project.nasalibrary.model.recent.RecentResponse
import com.project.nasalibrary.utils.NetworkRequest
import com.project.nasalibrary.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val popularRepository: PopularItemsRepository,
    private val recentRepository: RecentItemsRepository
) : ViewModel() {
    val popularItemsData = MutableLiveData<NetworkRequest<PopularResponse>>()
    val recentItemsData = MutableLiveData<NetworkRequest<RecentResponse>>()

    init {
        callPopularApi()
        callRecentApi()
    }

    private fun callPopularApi() = viewModelScope.launch {
        popularItemsData.postValue(NetworkRequest.Loading())
        val response = popularRepository.getPopularItems()
        popularItemsData.value = NetworkResponse(response).getNetworkResponse()
    }

    private fun callRecentApi() = viewModelScope.launch {
        recentItemsData.postValue(NetworkRequest.Loading())
        val response = recentRepository.getRecentItems()
        recentItemsData.value = NetworkResponse(response).getNetworkResponse()
    }


}
