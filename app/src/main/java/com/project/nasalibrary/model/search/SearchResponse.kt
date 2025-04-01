package com.project.nasalibrary.model.search


import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("collection")
    val collection: Collection?
) {
    data class Collection(
        @SerializedName("href")
        val href: String?, // http://images-api.nasa.gov/search?q=apollo%2011
        @SerializedName("items")
        val items: List<Item?>?,
        @SerializedName("links")
        val links: List<Link?>?,
        @SerializedName("metadata")
        val metadata: Metadata?,
        @SerializedName("version")
        val version: String? // 1.1
    ) {
        data class Item(
            @SerializedName("data")
            val `data`: List<Data?>?,
            @SerializedName("href")
            val href: String?, // https://images-assets.nasa.gov/video/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141/collection.json
            @SerializedName("links")
            val links: List<Link?>?
        ) {
            data class Data(
                @SerializedName("album")
                val album: List<String?>?,
                @SerializedName("center")
                val center: String?, // KSC
                @SerializedName("date_created")
                val dateCreated: String?, // 1985-01-01T00:00:00Z
                @SerializedName("description")
                val description: String?, // Productions "Apollo 11 Moon Landing" and "Project Apollo 11" plus imagery featuring Goddard's contributions to space.  TRT:  00:48:22
                @SerializedName("keywords")
                val keywords: List<String?>?,
                @SerializedName("location")
                val location: String?, // KSC
                @SerializedName("media_type")
                val mediaType: String?, // video
                @SerializedName("nasa_id")
                val nasaId: String?, // KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141
                @SerializedName("photographer")
                val photographer: String?, // NASA
                @SerializedName("title")
                val title: String? // Apollo 11 Productions
            )

            data class Link(
                @SerializedName("height")
                val height: Int?, // 266
                @SerializedName("href")
                val href: String?, // https://images-assets.nasa.gov/video/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141/KSC-19850101-MH-NAS01-0001-Apollo_11_Buzz_Aldrin_Experts-B_2141~medium.jpg
                @SerializedName("rel")
                val rel: String?, // alternate
                @SerializedName("render")
                val render: String?, // image
                @SerializedName("size")
                val size: Int?, // 15000
                @SerializedName("width")
                val width: Int? // 400
            )
        }

        data class Link(
            @SerializedName("href")
            val href: String?, // http://images-api.nasa.gov/search?q=apollo+11&page=2
            @SerializedName("prompt")
            val prompt: String?, // Next
            @SerializedName("rel")
            val rel: String? // next
        )

        data class Metadata(
            @SerializedName("total_hits")
            val totalHits: Int? // 1580
        )
    }
}