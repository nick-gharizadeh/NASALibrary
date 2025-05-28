package com.project.nasalibrary.ui.detailFragment

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.project.nasalibrary.R
import com.project.nasalibrary.databinding.FragmentDetailBinding
import com.project.nasalibrary.utils.Constants.VIDEO_MEDIA_TYPE
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class DetailFragment : Fragment() {

    // Interface for MainActivity communication[full screen option]
    interface FullscreenListener {
        fun toggleActivityUIForFullscreen(isFullscreen: Boolean)
    }

    private var fullscreenListener: FullscreenListener? = null

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var playbackPosition: Long = 0
    private var toolbar: Toolbar? = null
    private var isPlayerFullscreen = false
    private var originalPlayerViewHeightPx: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FullscreenListener) {
            fullscreenListener = context
        } else {
            throw RuntimeException("$context must implement FullscreenListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        originalPlayerViewHeightPx =
            resources.getDimensionPixelSize(R.dimen.player_view_original_height)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playback_position", 0)
            isPlayerFullscreen = savedInstanceState.getBoolean("is_player_fullscreen", false)
        }

        val item = args.Item
        val title = item.data[0].title
        val nasaId = item.data[0].nasaId
        val description = item.data[0].description
        val date = viewModel.correctDateFormat(item.data[0].dateCreated)
        val mediaType = item.data[0].mediaType
        val keywords = item.data[0].keywords?.joinToString(", ")
        val imageHref = item.links?.get(0)?.href

        if (mediaType == VIDEO_MEDIA_TYPE) {
            nasaId?.let { viewModel.callAssetApi(it) }
            loadItemAssetsURL()
            binding.headerImage.visibility = View.GONE
            binding.playerConstraintView.visibility = View.VISIBLE
        } else {
            binding.playerConstraintView.visibility = View.GONE
            binding.headerImage.visibility = View.VISIBLE
            Glide.with(requireView()).load(imageHref)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transform(RoundedCorners(30)).into(binding.headerImage)
            binding.headerImage.setOnClickListener {
                item.data[0].nasaId?.let { id -> gotoImageDialogFragment(id) }
            }
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (isPlayerFullscreen) {
                toolbar?.visibility = View.GONE
                return@addOnOffsetChangedListener
            }
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                toolbar?.visibility = View.VISIBLE
            } else if (verticalOffset == 0) {
                toolbar?.visibility = View.GONE
            }
        }


        // init views
        binding.textViewTitle.text = title
        binding.textViewDescription.text = description
        binding.textViewDate.text = date
        if (keywords != null) {
            binding.textViewKeywords.text = keywords
            binding.keywordSection.visibility = View.VISIBLE
        } else {
            binding.keywordSection.visibility = View.GONE
        }

        binding.shareImageView.setOnClickListener {
            val shareText = """
            ✨ $title ✨
            $description
            🗓️ Date: $date
            🏷️ Keywords: $keywords
            ${if (!imageHref.isNullOrBlank()) "🖼️ Media Link: $imageHref" else ""}
        """.trimIndent()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via"))
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isPlayerFullscreen) {
                        performToggleFullscreen(false)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })

        if (isPlayerFullscreen && mediaType == VIDEO_MEDIA_TYPE) {
            view.post {
                performToggleFullscreen(true, isRestoringState = true)
            }
        }
    }

    private fun gotoImageDialogFragment(nasaId: String) {
        val action = DetailFragmentDirections.actionDetailFragmentToImageDialogFragment(nasaId)
        findNavController().navigate(action)
    }

    private fun loadItemAssetsURL() {
        viewModel.assetData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkRequest.Loading -> {
                    binding.lottieAnimationViewLoading.visibility = View.VISIBLE
                }

                is NetworkRequest.Success -> {
                    binding.lottieAnimationViewLoading.visibility = View.GONE
                    response.data?.let { data ->
                        val videoHref = viewModel.findSmallestMp4Url(data)
                        videoHref?.let {
                            Log.d("videoHref", it)
                            initializePlayer(it)
                        }
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

    private fun initializePlayer(videoHref: String) {
        if (_binding == null) return

        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build().also {
                it.addListener(playerListener)
            }
        } else {
            player?.removeListener(playerListener)
            player?.addListener(playerListener)
        }

        binding.videoPlayerView.player = player
        binding.videoPlayerView.setFullscreenButtonClickListener { shouldEnterFullscreen ->
            performToggleFullscreen(shouldEnterFullscreen)
        }

        val mediaItem = MediaItem.fromUri(videoHref)
        player?.setMediaItem(mediaItem)
        player?.seekTo(playbackPosition)
        player?.prepare()
        updateVideoLoadingIndicatorVisibility()

    }

    private fun performToggleFullscreen(
        shouldEnterFullscreen: Boolean,
        isRestoringState: Boolean = false
    ) {
        if (_binding == null) return

        if (!isRestoringState && this.isPlayerFullscreen == shouldEnterFullscreen) {
            return
        }
        this.isPlayerFullscreen = shouldEnterFullscreen
        val activity = requireActivity()
        val window = activity.window

        fullscreenListener?.toggleActivityUIForFullscreen(shouldEnterFullscreen) // Notify Activity

        if (shouldEnterFullscreen) {
            binding.appBarLayout.setExpanded(false, false)
            binding.nestedScrollView.visibility = View.GONE
            toolbar?.visibility = View.GONE

            binding.appBarLayout.updateLayoutParams<ViewGroup.LayoutParams> {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            binding.playerConstraintView.updateLayoutParams<ViewGroup.LayoutParams> {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            binding.videoPlayerView.updateLayoutParams<ViewGroup.LayoutParams> {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            if (!isRestoringState) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        } else {
            binding.nestedScrollView.visibility = View.VISIBLE

            binding.appBarLayout.updateLayoutParams<ViewGroup.LayoutParams> {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            binding.videoPlayerView.updateLayoutParams<ViewGroup.LayoutParams> {
                height = originalPlayerViewHeightPx
            }

            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.root)
                .show(WindowInsetsCompat.Type.systemBars())

            if (!isRestoringState) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            binding.appBarLayout.setExpanded(true, true)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (_binding == null) return
            updateVideoLoadingIndicatorVisibility()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (_binding == null) return
            updateVideoLoadingIndicatorVisibility()
        }

        override fun onPlayerError(error: PlaybackException) {
            if (_binding == null) return
            binding.lottieAnimationViewLoading.visibility = View.GONE
            Snackbar.make(binding.root, "Video error: ${error.message}", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun updateVideoLoadingIndicatorVisibility() {
        val currentPlayer = player ?: return
        if (_binding == null) { // Check if binding is null
            return
        }
        val showLoading = currentPlayer.playWhenReady &&
                currentPlayer.playbackState == Player.STATE_BUFFERING
        binding.lottieAnimationViewLoading.visibility = if (showLoading) View.VISIBLE else View.GONE
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            it.removeListener(playerListener)
            it.release()
            player = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        player?.let {
            outState.putLong("playback_position", it.currentPosition)
        }
        outState.putBoolean("is_player_fullscreen", isPlayerFullscreen)
    }

    override fun onResume() {
        super.onResume()
        if (args.Item.data[0].mediaType == VIDEO_MEDIA_TYPE && player == null) {
            val currentAssetData = viewModel.assetData.value
            if (currentAssetData is NetworkRequest.Success) {
                currentAssetData.data?.let { data ->
                    val videoHref = viewModel.findSmallestMp4Url(data)
                    videoHref?.let { initializePlayer(it) }
                }
            } else if (currentAssetData == null || currentAssetData is NetworkRequest.Error) {
                args.Item.data[0].nasaId?.let { viewModel.callAssetApi(it) }
            }
        } else if (player != null && isPlayerFullscreen) {
            performToggleFullscreen(true, isRestoringState = true)
        }
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            if (it.playWhenReady) {
                playbackPosition = it.currentPosition
            }
            it.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!requireActivity().isChangingConfigurations) {
            releasePlayer()
        }
    }

    override fun onDestroyView() {
        player?.let {
            playbackPosition = it.currentPosition
        }
        if (!requireActivity().isChangingConfigurations) {
            releasePlayer()
        }
        _binding = null // Crucial to prevent memory leaks
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        fullscreenListener = null
    }
}


