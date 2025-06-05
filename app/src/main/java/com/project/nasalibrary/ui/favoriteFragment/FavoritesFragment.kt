package com.project.nasalibrary.ui.favoriteFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.nasalibrary.adapter.FavoritesAdapter
import com.project.nasalibrary.adapter.PopularAdapter
import com.project.nasalibrary.databinding.FragmentFavoriteBinding // ViewBinding for fragment layout
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.ui.BaseFragment
import com.project.nasalibrary.ui.homeFragment.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : BaseFragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
    }

    override fun onRetry() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFavorites.adapter = favoritesAdapter
        favoritesAdapter.setOnItemClickListener { gotoDetailFragment(it) }
    }

    private fun gotoDetailFragment(item: Item) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }


    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFavoriteItems.collectLatest { favoritesList ->
                    if (favoritesList.isEmpty()) {
                        binding.recyclerViewFavorites.visibility = View.GONE
                        binding.textViewEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewFavorites.visibility = View.VISIBLE
                        binding.textViewEmptyState.visibility = View.GONE
                        favoritesAdapter.submitList(favoritesList)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewFavorites.adapter = null // for RecyclerView memory leak prevention
        _binding = null // Clear binding reference
    }
}