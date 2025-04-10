package com.project.nasalibrary.model


import com.google.gson.annotations.SerializedName

data class LinkItems(
    @SerializedName("href")
    var href: String? // http://images-assets.nasa.gov/image/as11-40-5874/as11-40-5874~orig.jpg
)