package com.project.nasalibrary.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.project.nasalibrary.utils.ListConverters
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "favorite_items")
@TypeConverters(ListConverters::class)
data class FavoriteItemEntity(
    @PrimaryKey
    @SerializedName("nasa_id")
    var nasaId: String,

    @SerializedName("album")
    var album: List<String?>?,

    @SerializedName("center")
    var center: String?,

    @SerializedName("date_created")
    var dateCreated: String,

    @SerializedName("description")
    var description: String?,

    @SerializedName("keywords")
    var keywords: List<String?>?,

    @SerializedName("location")
    var location: String?,

    @SerializedName("media_type")
    var mediaType: String?,

    @SerializedName("photographer")
    var photographer: String?,

    @SerializedName("title")
    var title: String?
) : Parcelable