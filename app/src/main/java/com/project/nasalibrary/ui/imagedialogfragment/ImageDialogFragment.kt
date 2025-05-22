package com.project.nasalibrary.ui.imagedialogfragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.databinding.FragmentImageDialogBinding
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDialogFragment : DialogFragment() {


    private val viewModel: ImageDialogViewModel by viewModels()
    private var _binding: FragmentImageDialogBinding? = null
    private val binding get() = _binding!!
    private val args: ImageDialogFragmentArgs by navArgs()
    private var scaleFactor = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get safe arg
        val nasaId = args.nasaId
        // call api and show image
        viewModel.callAssetApi(nasaId)
        loadImage()

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Transparent background (80% opacity)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#CC000000")))
        // Enable zoom
        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }


    }

    private fun loadImage() {
        viewModel.assetData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkRequest.Loading -> {
                    binding.lottieAnimationViewLoading.visibility = View.VISIBLE
                }

                is NetworkRequest.Success -> {
                    binding.lottieAnimationViewLoading.visibility = View.GONE
                    response.data?.let { data ->
                        // Load image with Glide
                        val url = viewModel.findLargeImageHref(data)
                        Glide.with(requireContext())
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imageView)
                    }
                }

                is NetworkRequest.Error -> {
                    response.message?.let {
                        Snackbar.make(
                            binding.root,
                            it, Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = 0.1f.coerceAtLeast(scaleFactor.coerceAtMost(5.0f))
            binding.imageView.scaleX = scaleFactor
            binding.imageView.scaleY = scaleFactor
            return true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}