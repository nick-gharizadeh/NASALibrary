package com.project.nasalibrary.model.search


import com.google.gson.annotations.SerializedName
import com.project.nasalibrary.model.Collection

data class SearchResponse(
    @SerializedName("collection")
    var collection: Collection?
)