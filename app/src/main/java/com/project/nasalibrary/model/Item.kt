package com.project.nasalibrary.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.project.nasalibrary.utils.AppTypeConvertors
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite_items")
@TypeConverters(AppTypeConvertors::class)
@Parcelize
data class Item(
    @SerializedName("data")
    var data: List<Data>,
    @PrimaryKey
    @SerializedName("href")
    var href: String, // https://images-assets.nasa.gov/video/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141/collection.json
    @SerializedName("links")
    var links: List<Link?>?
) : Parcelable