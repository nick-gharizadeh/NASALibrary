package com.project.nasalibrary.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Link(
    @SerializedName("height")
    var height: Int?, // 266
    @SerializedName("href")
    var href: String?, // https://images-assets.nasa.gov/video/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141~medium.jpg
    @SerializedName("rel")
    var rel: String?, // alternate
    @SerializedName("render")
    var render: String?, // image
    @SerializedName("size")
    var size: Int?, // 15000
    @SerializedName("width")
    var width: Int? // 400
): Parcelable