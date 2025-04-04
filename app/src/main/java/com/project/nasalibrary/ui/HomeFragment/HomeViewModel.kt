package com.project.nasalibrary.ui.homeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nasalibrary.data.repository.PopularItemsRepository
import com.project.nasalibrary.model.popular.PopularResponse
import com.project.nasalibrary.utils.NetworkRequest
import com.project.nasalibrary.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: PopularItemsRepository) : ViewModel() {
    val popularItemsData = MutableLiveData<NetworkRequest<PopularResponse>>()

    init {
        callPopularApi()
    }

    fun callPopularApi() = viewModelScope.launch{
        popularItemsData.postValue(NetworkRequest.Loading())
        val response = repository.getPopularItems()
        popularItemsData.value = NetworkResponse(response).getNetworkResponse()
    }

}
