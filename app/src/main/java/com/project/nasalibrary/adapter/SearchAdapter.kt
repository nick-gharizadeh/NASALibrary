package com.project.nasalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
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


class SearchAdapter @Inject constructor() :
    PagingDataAdapter<Item, SearchAdapter.ViewHolder>(ITEM_COMPARATOR) {

    inner class ViewHolder(private val binding: MainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item?) {
            item?.let { currentItem ->
                binding.apply {
                    val firstDataItem = currentItem.data.firstOrNull()
                    imageViewPlayIcon.visibility =
                        if (firstDataItem?.mediaType == Constants.VIDEO_MEDIA_TYPE) View.VISIBLE else View.GONE
                    textViewTitle.text = firstDataItem?.title ?: ""

                    val imageHref = currentItem.links?.firstOrNull { it?.href != null }?.href
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.href == newItem.href
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}