package com.project.nasalibrary.ui.imageDialogFragment

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


    fun callAssetApi(nasaId: String) = viewModelScope.launch {
        assetData.postValue(NetworkRequest.Loading())
        val response = repository.getAsset(nasaId)
        assetData.value = NetworkResponse(response).getNetworkResponse()
    }


    // Searches for a preferred image URL in a specific order: medium, small, then large.
    fun findPreferredImageUrl(assetResponse: AssetResponse): String? {
        // Get all available image URLs
        val urls: List<String> = assetResponse.collection.items.map { it.href }
        // Define the search order
        val preferredSizes = listOf("~medium.jpg", "~small.jpg", "~large.jpg")
        // Find the first URL that matches the preferred sizes
        for (size in preferredSizes) {
            val foundUrl = urls.find { it.endsWith(size, ignoreCase = true) }
            if (foundUrl != null) {
                return foundUrl
            }
        }

        return urls.find { it.endsWith(".jpg", ignoreCase = true) }
    }

}