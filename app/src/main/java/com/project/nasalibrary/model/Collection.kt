package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class Collection(
    @SerializedName("href")
    var href: String?, // http://images-api.nasa.gov/search?q=apollo%2011
    @SerializedName("items")
    var items: List<Item>,
    @SerializedName("links")
    var links: List<LinkX>?,
    @SerializedName("metadata")
    var metadata: Metadata?,
    @SerializedName("version")
    var version: String? // 1.1
)