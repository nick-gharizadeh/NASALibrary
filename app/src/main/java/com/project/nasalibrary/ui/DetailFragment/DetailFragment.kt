package com.project.nasalibrary.ui.detailFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
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
import com.project.nasalibrary.databinding.FragmentDetailBinding
import com.project.nasalibrary.utils.Constants.VIDEO_MEDIA_TYPE
import com.project.nasalibrary.utils.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private var toolbar: Toolbar? = null
    private val viewModel: DetailViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var playbackPosition: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playback_position", 0)
        }
        // item data
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
            binding.apply {
                headerImage.visibility = View.GONE
                playerConstraintView.visibility = View.VISIBLE
            }

        } else {
            binding.apply {

                Glide.with(requireView()).load(imageHref)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transform(RoundedCorners(30)).into(headerImage)
                headerImage.setOnClickListener {
                    item.data[0].nasaId?.let { nasaId -> gotoImageDialogFragment(nasaId) }
                }
            }

        }


        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                toolbar?.visibility = View.VISIBLE
            } else if (verticalOffset == 0) {
                toolbar?.visibility = View.GONE
            }

        }




        binding.apply {
            textViewTitle.text = title
            textViewDescription.text = description
            textViewDate.text = date
            if (keywords != null) {
                textViewKeywords.text = keywords
                keywordSection.visibility = View.VISIBLE
            }


            // Share
            shareImageView.setOnClickListener {
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

                val shareIntent = Intent.createChooser(sendIntent, "Share via")
                startActivity(shareIntent)
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
                    response.data?.let { data ->
                        val videoHref = viewModel.findSmallestMp4Url(data)
                        if (videoHref != null) {
                            Log.d("videoHref", videoHref)
                        }
                        if (videoHref != null) {
                            initializePlayer(videoHref)
                            binding.lottieAnimationViewLoading.visibility = View.GONE
                        }

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


    private fun initializePlayer(videoHref: String) {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build().also {
                it.addListener(playerListener)
            }
        } else {
            player?.removeListener(playerListener)
            player?.addListener(playerListener)
        }

        binding.videoPlayerView.player = player
        val mediaItem = MediaItem.fromUri(videoHref)
        player?.setMediaItem(mediaItem)
        player?.seekTo(playbackPosition)
        player?.prepare()
        updateVideoLoadingIndicatorVisibility()
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updateVideoLoadingIndicatorVisibility()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updateVideoLoadingIndicatorVisibility()
        }

        override fun onPlayerError(error: PlaybackException) {
            binding.lottieAnimationViewLoading.visibility = View.GONE // Hide on error
            // Handle player error (e.g., show a message)
            Snackbar.make(binding.root, "Video playback error: ${error.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateVideoLoadingIndicatorVisibility() {
        val currentPlayer = player
        if (currentPlayer == null || _binding == null) {
            _binding?.lottieAnimationViewLoading?.visibility = View.GONE
            return
        }

        // Show loading if player wants to play AND is in buffering state
        val showLoading = currentPlayer.playWhenReady &&
                currentPlayer.playbackState == Player.STATE_BUFFERING

        binding.lottieAnimationViewLoading.visibility = if (showLoading) View.VISIBLE else View.GONE
    }


    private fun releasePlayer() {
        player?.let {
            // Save the current playback state before releasing
            playbackPosition = it.currentPosition
            it.release() // Release the player
            player = null // Set player to null to allow garbage collection
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Release the player resources when the fragment is stopped
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (args.Item.data[0].mediaType == VIDEO_MEDIA_TYPE && player == null) {
            val currentAssetData = viewModel.assetData.value
            if (currentAssetData is NetworkRequest.Success) {
                currentAssetData.data?.let { data ->
                    val videoHref = viewModel.findSmallestMp4Url(data)
                    if (videoHref != null) {
                        initializePlayer(videoHref)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current playback position
        player?.let {
            outState.putLong("playback_position", it.currentPosition)
        }
    }

    // Pause the player when the fragment is no longer visible
    override fun onPause() {
        super.onPause()
        // Save the current playback state
        player?.let {
            playbackPosition = it.currentPosition
            it.pause() // Pause playback
        }
    }
}

