package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class LinkX(
    @SerializedName("href")
    var href: String?, // http://images-api.nasa.gov/search?q=apollo+11&page=2
    @SerializedName("prompt")
    var prompt: String?, // Next
    @SerializedName("rel")
    var rel: String? // next
)