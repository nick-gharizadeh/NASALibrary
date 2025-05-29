package com.project.nasalibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.project.nasalibrary.databinding.ItemLoadStateBinding

class SearchLoadStateViewHolder(
    private val binding: ItemLoadStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.loadStateRetryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        binding.loadStateProgressBar.isVisible = loadState is LoadState.Loading
        binding.loadStateRetryButton.isVisible = loadState is LoadState.Error
        binding.loadStateErrorMessage.isVisible = loadState is LoadState.Error

        if (loadState is LoadState.Error) {
            binding.loadStateErrorMessage.text = loadState.error.localizedMessage ?: "Unknown Error"
        }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): SearchLoadStateViewHolder {
            val binding = ItemLoadStateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchLoadStateViewHolder(binding, retry)
        }
    }
}