package com.project.nasalibrary.model.recent

import com.google.gson.annotations.SerializedName
import com.project.nasalibrary.model.Collection

data class RecentResponse(
    @SerializedName("collection")
    var collection: Collection
)