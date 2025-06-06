package com.project.nasalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.project.nasalibrary.R
import com.project.nasalibrary.databinding.MainItemBinding
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.Constants
import javax.inject.Inject


class FavoritesAdapter @Inject constructor() :
    ListAdapter<Item, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = MainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class FavoriteViewHolder(private val binding: MainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item?) {
            item?.let { currentItem ->
                binding.apply {
                    val firstDataItem = currentItem.data.firstOrNull()
                    imageViewPlayIcon.visibility =
                        if (firstDataItem?.mediaType == Constants.VIDEO_MEDIA_TYPE) View.VISIBLE else View.GONE
                    textViewTitle.text = firstDataItem?.title ?: ""
                    val imageHref = currentItem.links?.firstOrNull()?.href
                    Glide.with(itemView.context)
                        .load(imageHref)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transform(CenterCrop(), RoundedCorners(20))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.ic_error_image)
                        .into(imageViewMainItem)
                    root.setOnClickListener {
                        onItemClickListener?.invoke(currentItem)
                    }
                }
            }
        }
    }


    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    class FavoriteDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.data[0].nasaId == oldItem.data[0].nasaId
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}