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
    var searchedText = MutableLiveData<String>()

    private fun callSearchApi(query: String) = viewModelScope.launch {
        searchData.value = NetworkRequest.Loading()
        val response = repository.search(query)
        searchData.value = NetworkResponse(response).getNetworkResponse()
    }

    fun performSearchWithDebounce() {
        viewModelScope.launch {
            flow {
                emit(searchedText.value)
            }
                .debounce(1500) // Debounce for 1500ms
                .flowOn(Dispatchers.IO)
                .collect { searchQuery ->
                    searchedText.value?.let { callSearchApi(it) }
                }
        }
    }



}