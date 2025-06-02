package com.project.nasalibrary.ui.imagedialogfragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.databinding.FragmentImageDialogBinding
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDialogFragment : DialogFragment() {

    private var _binding: FragmentImageDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ImageDialogViewModel by viewModels()
    private val args: ImageDialogFragmentArgs by navArgs()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector

    private var scaleFactor = 1.0f
    private val minScale = 1.0f
    private val maxScale = 5.0f


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#80000000")))
        }
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

        setupGestureDetectors()
        setupViewModel()

        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true
        }

        viewModel.callAssetApi(args.nasaId)
    }

    private fun setupViewModel() {
        viewModel.assetData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkRequest.Loading -> {
                    binding.lottieAnimationViewLoading.visibility = View.VISIBLE
                }
                is NetworkRequest.Success -> {
                    binding.lottieAnimationViewLoading.visibility = View.GONE
                    response.data?.let { data ->
                        val url = viewModel.findPreferredImageUrl(data)
                        Glide.with(requireContext())
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imageView)
                    }
                }
                is NetworkRequest.Error -> {
                    binding.lottieAnimationViewLoading.visibility = View.GONE
                    response.message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupGestureDetectors() {
        scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (scaleFactor > minScale) {
                    animateScale(scaleFactor, minScale)
                } else {
                    animateScale(scaleFactor, maxScale / 2) // Zoom to a mid-level
                }
                return true
            }
        })
    }

    private fun animateScale(start: Float, end: Float) {
        ValueAnimator.ofFloat(start, end).apply {
            duration = 300 // Animation duration in milliseconds
            addUpdateListener { valueAnimator ->
                val newScale = valueAnimator.animatedValue as Float
                setScale(newScale)
            }
            start()
        }
    }

    private fun setScale(newScale: Float) {
        scaleFactor = newScale
        binding.imageView.scaleX = scaleFactor
        binding.imageView.scaleY = scaleFactor
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val newScale = scaleFactor * detector.scaleFactor
            setScale(newScale.coerceIn(minScale, maxScale))
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}