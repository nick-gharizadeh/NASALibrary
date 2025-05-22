package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class LinkCollection(
    @SerializedName("href")
    var href: String, // http://images-api.nasa.gov/asset/as11-40-5874
    @SerializedName("items")
    var items: List<LinkItems>,
    @SerializedName("version")
    var version: String // 1.1
)