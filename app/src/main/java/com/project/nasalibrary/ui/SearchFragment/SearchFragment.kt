package com.project.nasalibrary.ui.searchFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.adapter.SearchAdapter
import com.project.nasalibrary.adapter.SearchLoadStateAdapter
import com.project.nasalibrary.databinding.FragmentSearchBinding
import com.project.nasalibrary.model.Item
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var searchPagingAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
        observeSearchResults()
        observeLoadStates()
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        binding.SearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchPagingAdapter.withLoadStateFooter(
                footer = SearchLoadStateAdapter { searchPagingAdapter.retry() }
            )
        }
        searchPagingAdapter.setOnItemClickListener { item ->
            gotoDetailFragment(item)
        }
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { pagingData ->
                searchPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            searchPagingAdapter.loadStateFlow.collectLatest { loadStates ->
                val refreshState = loadStates.refresh

                binding.loadingAnimationGroup.isVisible = refreshState is LoadState.Loading

                if (refreshState is LoadState.NotLoading && searchPagingAdapter.itemCount == 0 && viewModel.searchQuery.value.isNotBlank()) {
                    binding.SearchRecyclerView.isVisible = false
                    binding.lottieAnimationViewNoResults.visibility = View.VISIBLE
                } else {
                    binding.SearchRecyclerView.isVisible = true
                    binding.lottieAnimationViewNoResults.visibility = View.GONE
                }

            }
        }
    }



    private fun gotoDetailFragment(item: Item) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.SearchRecyclerView.adapter = null
        _binding = null
    }
}