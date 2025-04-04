package com.project.nasalibrary.ui.searchFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.adapter.SearchAdapter
import com.project.nasalibrary.databinding.FragmentSearchBinding
import com.project.nasalibrary.model.Item
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var searchAdapter: SearchAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.performSearchWithDebounce(it.toString())
                    loadSearchData()
                }
            }
        })

        binding.SearchRecyclerView.adapter = searchAdapter
        searchAdapter.setOnItemClickListener {
            gotoDetailFragment(it)
        }

    }

    private fun loadSearchData() {
        viewModel.searchData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkRequest.Loading -> {
                    binding.loadingAnimationGroup.visibility = View.VISIBLE
                }
                is NetworkRequest.Success -> {
                    response.data?.let { data ->
                        binding.loadingAnimationGroup.visibility = View.GONE
                        if (data.collection?.items?.isNotEmpty() == true) {
                            searchAdapter.setData(data.collection!!.items!!)
                            Snackbar.make(
                                binding.root,
                                data.collection?.items?.get(0)?.href.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()

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
        val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}