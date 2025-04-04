package com.project.nasalibrary.ui.searchFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nasalibrary.data.repository.SearchRepository
import com.project.nasalibrary.model.search.SearchResponse
import com.project.nasalibrary.utils.NetworkRequest
import com.project.nasalibrary.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel  @Inject constructor(private val repository: SearchRepository) : ViewModel() {
    val searchData = MutableLiveData<NetworkRequest<SearchResponse>>()

    fun callSearchApi(query: String) = viewModelScope.launch {
        searchData.value = NetworkRequest.Loading()
        val response = repository.search(query)
        searchData.value = NetworkResponse(response).getNetworkResponse()
    }

    fun performSearchWithDebounce(query: String) {
        viewModelScope.launch {
            flow {
                emit(query)
            }
                .debounce(1000) // Debounce for 500ms
                .flowOn(Dispatchers.IO)
                .collect { searchQuery ->
                    callSearchApi(searchQuery)
                }
        }
    }



}