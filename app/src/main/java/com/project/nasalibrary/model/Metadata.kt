package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("total_hits")
    var totalHits: Int? // 1580
)