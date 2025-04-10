package com.project.nasalibrary.ui.detailFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.project.nasalibrary.databinding.FragmentDetailBinding
import com.project.nasalibrary.model.Link
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private var toolbar: Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = args.Item
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                toolbar?.visibility = View.VISIBLE
            } else if (verticalOffset == 0) {
                toolbar?.visibility = View.GONE
            }
        }

        binding.apply {
            textViewTitle.text = item.data?.get(0)?.title ?: "No title"
            textViewDescription.text = item.data?.get(0)?.description
            val imageHref = item.links?.get(0)?.href
            Glide.with(requireView())
                .load(imageHref)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transform(RoundedCorners(30))
                .into(headerImage)
            headerImage.setOnClickListener {
                item.data?.get(0)?.nasaId?.let { nasaId -> gotoImageDialogFragment(nasaId) }
            }

        }

    }

    private fun gotoImageDialogFragment(nasaId: String) {
        val action = DetailFragmentDirections.actionDetailFragmentToImageDialogFragment(nasaId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

