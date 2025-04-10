package com.project.nasalibrary.ui.imagedialogfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nasalibrary.data.repository.AssetRepository
import com.project.nasalibrary.model.AssetResponse
import com.project.nasalibrary.utils.NetworkRequest
import com.project.nasalibrary.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ImageDialogViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    val assetData = MutableLiveData<NetworkRequest<AssetResponse>>()


    fun callPopularApi(nasaId:String) = viewModelScope.launch {
        assetData.postValue(NetworkRequest.Loading())
        val response = repository.getAsset(nasaId)
        assetData.value = NetworkResponse(response).getNetworkResponse()
    }


    fun findLargeImageHref(assetResponse: AssetResponse): String? {
        return assetResponse.collection?.items?.find {
            it.href?.contains("~large.jpg") ?: false
        }?.href
    }

}