package com.project.nasalibrary.ui.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.R
import com.project.nasalibrary.adapter.PopularAdapter
import com.project.nasalibrary.adapter.RecentAdapter
import com.project.nasalibrary.databinding.FragmentHomeBinding
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.paging.LoadNextPageException
import com.project.nasalibrary.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var popularAdapter: PopularAdapter

    @Inject
    lateinit var recentAdapter: RecentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupClickListeners()
        observePopularData()
        observeRecentData()
    }

    override fun onRetry() {
        popularAdapter.retry()
        recentAdapter.retry()
    }

    private fun setupRecyclerViews() {
        binding.RecyclerViewPopular.adapter = popularAdapter
        binding.RecyclerViewRecent.adapter = recentAdapter
    }

    private fun setupClickListeners() {
        popularAdapter.setOnItemClickListener { gotoDetailFragment(it) }
        recentAdapter.setOnItemClickListener { gotoDetailFragment(it) }

        binding.buttonLoadMorePopular.setOnClickListener { popularAdapter.retry() }
        binding.buttonLoadMoreRecent.setOnClickListener { recentAdapter.retry() }
    }

    private fun observePopularData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularItemsData.collectLatest { pagingData ->
                popularAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            popularAdapter.loadStateFlow.collectLatest { loadStates ->
                // Initial load: show shimmer.
                binding.shimmerLayoutPopular.isVisible = loadStates.refresh is LoadState.Loading
                binding.RecyclerViewPopular.isVisible = loadStates.refresh is LoadState.NotLoading

                // Loading state for next page (append).
                binding.progressBarPopular.isVisible = loadStates.append is LoadState.Loading

                // Check for our custom error to show the "Load More" button.
                val appendError = loadStates.append as? LoadState.Error
                if (appendError?.error is LoadNextPageException) {
                    binding.buttonLoadMorePopular.isVisible = true
                    binding.buttonLoadMorePopular.text = getString(R.string.load_more)
                } else if (appendError != null) {
                    // Handle real errors
                    binding.buttonLoadMorePopular.isVisible = true
                    binding.buttonLoadMorePopular.text = getString(R.string.retry)
                    Snackbar.make(
                        binding.root,
                        "Error: ${appendError.error.localizedMessage}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.buttonLoadMorePopular.isVisible =
                        !loadStates.append.endOfPaginationReached
                }

                // Hide button if we are at the end of the list
                if (loadStates.append.endOfPaginationReached) {
                    binding.buttonLoadMorePopular.isVisible = false
                }
            }
        }
    }

    private fun observeRecentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentItemsData.collectLatest { pagingData ->
                recentAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            recentAdapter.loadStateFlow.collectLatest { loadStates ->
                // Initial load: show shimmer.
                binding.shimmerLayoutRecent.isVisible = loadStates.refresh is LoadState.Loading
                binding.RecyclerViewRecent.isVisible = loadStates.refresh is LoadState.NotLoading

                // Loading state for next page (append).
                binding.progressBarRecent.isVisible = loadStates.append is LoadState.Loading

                val appendError = loadStates.append as? LoadState.Error
                if (appendError?.error is LoadNextPageException) {
                    binding.buttonLoadMoreRecent.isVisible = true
                    binding.buttonLoadMoreRecent.text = getString(R.string.load_more)
                } else if (appendError != null) {
                    binding.buttonLoadMoreRecent.isVisible = true
                    binding.buttonLoadMoreRecent.text = getString(R.string.retry)
                    Snackbar.make(
                        binding.root,
                        "Error: ${appendError.error.localizedMessage}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.buttonLoadMoreRecent.isVisible =
                        !loadStates.append.endOfPaginationReached
                }

                if (loadStates.append.endOfPaginationReached) {
                    binding.buttonLoadMoreRecent.isVisible = false
                }
            }
        }
    }

    private fun gotoDetailFragment(item: Item) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.RecyclerViewPopular.adapter = null
        binding.RecyclerViewRecent.adapter = null
        _binding = null
    }
}