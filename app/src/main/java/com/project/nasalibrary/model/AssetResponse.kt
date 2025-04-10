package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class AssetResponse(
    @SerializedName("collection")
    var collection: LinkCollection?
)