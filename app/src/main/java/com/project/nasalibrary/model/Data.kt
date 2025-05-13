package com.project.nasalibrary.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    @SerializedName("album")
    var album: List<String?>?,
    @SerializedName("center")
    var center: String?, // KSC
    @SerializedName("date_created")
    var dateCreated: String, // 1985-01-01T00:00:00Z
    @SerializedName("description")
    var description: String?, // Productions "Apollo 11 Moon Landing" and "Project Apollo 11" plus imagery featuring Goddard's contributions to space.  TRT:  00:48:22
    @SerializedName("keywords")
    var keywords: List<String>,
    @SerializedName("location")
    var location: String?, // KSC
    @SerializedName("media_type")
    var mediaType: String?, // video
    @SerializedName("nasa_id")
    var nasaId: String?, // KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141
    @SerializedName("photographer")
    var photographer: String?, // NASA
    @SerializedName("title")
    var title: String? // Apollo 11 Productions
): Parcelable