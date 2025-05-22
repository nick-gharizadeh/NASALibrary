package com.project.nasalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.project.nasalibrary.databinding.MainItemBinding
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.BaseDiffUtils
import com.project.nasalibrary.utils.Constants.VIDEO_MEDIA_TYPE
import javax.inject.Inject


class SearchAdapter @Inject constructor() : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var binding: MainItemBinding
    private var items = emptyList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                // Show Play Icon if its a video
                if(item.data[0].mediaType==VIDEO_MEDIA_TYPE)
                {
                    binding.imageViewPlayIcon.visibility = View.VISIBLE
                }
                //Text
                textViewTitle.text = item.data[0].title ?: ""
                //Image
                val imageHref = item.links?.get(0)?.href
                Glide.with(itemView)
                    .load(imageHref)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(binding.imageViewMainItem)
                //Click
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item)
                    }
                }
            }

        }
    }

    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<Item>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}