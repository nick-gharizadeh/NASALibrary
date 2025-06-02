package com.project.nasalibrary.ui.imagedialogfragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.project.nasalibrary.R
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

    var scaleFactor = 1.0f
    private val minScale = 1.0f
    private val maxScale = 5.0f

    private val panSensitivity = 1.8f


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
                    response.data?.let { data ->
                        val url = viewModel.findPreferredImageUrl(data)
                        Glide.with(requireContext())
                            .load(url)
                            .error(R.drawable.ic_error_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imageView)
                    }
                    binding.lottieAnimationViewLoading.visibility = View.GONE

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
                    animateToScaleAndCenter(minScale)
                } else {
                    animateToScaleAndCenter(maxScale / 2)
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (scaleFactor > minScale) {
                    var newTranslationX = binding.imageView.translationX - (distanceX * panSensitivity)
                    var newTranslationY = binding.imageView.translationY - (distanceY * panSensitivity)

                    val maxTranslationX = (binding.imageView.width * scaleFactor - binding.imageView.width) / 2
                    val maxTranslationY = (binding.imageView.height * scaleFactor - binding.imageView.height) / 2

                    newTranslationX = newTranslationX.coerceIn(-maxTranslationX, maxTranslationX)
                    newTranslationY = newTranslationY.coerceIn(-maxTranslationY, maxTranslationY)

                    binding.imageView.translationX = newTranslationX
                    binding.imageView.translationY = newTranslationY
                }
                return true
            }
        })
    }

    private fun animateToScaleAndCenter(targetScale: Float) {
        val targetTranslationX = 0f
        val targetTranslationY = 0f

        val scaleXAnimator = ObjectAnimator.ofFloat(binding.imageView, "scaleX", targetScale)
        val scaleYAnimator = ObjectAnimator.ofFloat(binding.imageView, "scaleY", targetScale)
        val translationXAnimator = ObjectAnimator.ofFloat(binding.imageView, "translationX", targetTranslationX)
        val translationYAnimator = ObjectAnimator.ofFloat(binding.imageView, "translationY", targetTranslationY)

        AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator, translationXAnimator, translationYAnimator)
            duration = 300
            start()
        }
        scaleFactor = targetScale
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(minScale, maxScale)

            binding.imageView.scaleX = scaleFactor
            binding.imageView.scaleY = scaleFactor

            val maxTranslationX = (binding.imageView.width * scaleFactor - binding.imageView.width) / 2
            val maxTranslationY = (binding.imageView.height * scaleFactor - binding.imageView.height) / 2

            binding.imageView.translationX = binding.imageView.translationX.coerceIn(-maxTranslationX, maxTranslationX)
            binding.imageView.translationY = binding.imageView.translationY.coerceIn(-maxTranslationY, maxTranslationY)

            if (scaleFactor == minScale) {
                binding.imageView.translationX = 0f
                binding.imageView.translationY = 0f
            }

            return true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}