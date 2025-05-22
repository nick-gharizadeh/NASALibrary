package com.project.nasalibrary.ui.detailFragment

import android.net.Uri
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
class DetailViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    val assetData = MutableLiveData<NetworkRequest<AssetResponse>>()


    fun callAssetApi(nasaId: String) = viewModelScope.launch {
        assetData.postValue(NetworkRequest.Loading())
        val response = repository.getAsset(nasaId)
        assetData.value = NetworkResponse(response).getNetworkResponse()
    }

    fun correctDateFormat(originalDateText: String): String {
        val indexOfT = originalDateText.indexOf("T")
        val correctedDate = originalDateText.substring(0, indexOfT)
        return correctedDate
    }


    /**
     * Finds the URL of the smallest MP4 video from a list of URLs
     * It prioritizes URLs containing "~small.mp4", then "~mobile.mp4", then "~preview.mp4".
     * If none of these specific indicators are found, it returns the first general MP4 URL
     */
    fun findSmallestMp4Url(assetResponse: AssetResponse): String? {
        val urls: List<String> = assetResponse.collection.items.map { it.href }
        val preferredSizes = listOf("~small.mp4", "~mobile.mp4", "~preview.mp4")
        var selectedUrl: String?
        for (sizeIndicator in preferredSizes) {
            selectedUrl = urls.firstOrNull { it.endsWith(".mp4") && it.contains(sizeIndicator) }
        }

        selectedUrl = urls.firstOrNull { it.endsWith(".mp4") }
        return selectedUrl?.let { url ->
            var processedUrl = url

            if (processedUrl.startsWith("http://")) {
                processedUrl = processedUrl.replace("http://", "https://")
            }

            // Uri.encode handles spaces by converting them to %20 and also encodes other special characters.
            Uri.encode(processedUrl, "/:?&=%")
        }
    }

}