package com.project.nasalibrary.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Item(
    @SerializedName("data")
    var data: List<FavoriteItemEntity>,
    @SerializedName("href")
    var href: String?, // https://images-assets.nasa.gov/video/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141/collection.json
    @SerializedName("links")
    var links: List<Link?>?
) : Parcelable