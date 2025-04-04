package com.project.nasalibrary.ui.homeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.adapter.PopularAdapter
import com.project.nasalibrary.databinding.FragmentHomeBinding
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var popularAdapter: PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.RecyclerViewPopular.adapter = popularAdapter
        loadPopularData()
        popularAdapter.setOnItemClickListener {
            gotoDetailFragment(it)
        }

    }

    private fun loadPopularData() {
        viewModel.popularItemsData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkRequest.Loading -> {

                }
                is NetworkRequest.Success -> {
                    response.data?.let { data ->
                        if (data.collection.items.isNotEmpty()) {
                            data.collection.items.let { popularAdapter.setData(it) }

                        }
                    }
                }
                is NetworkRequest.Error -> {
                    response.message?.let {
                        Snackbar.make(binding.root ,
                            it, Snackbar.LENGTH_SHORT).show()
                    }
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
        _binding = null
    }

}