package com.project.nasalibrary.model.popular


import com.google.gson.annotations.SerializedName
import com.project.nasalibrary.model.Collection

data class PopularResponse(
    @SerializedName("collection")
    var collection: Collection
)