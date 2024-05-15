package com.jar.app.core_ui.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.jar.app.core_ui.databinding.VideoPlayerViewBinding
import com.jar.app.core_ui.extension.setDebounceClickListener

class VideoPlayerView @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private var _binding: VideoPlayerViewBinding? = null
    private val binding by lazy {
        _binding!!
    }

    private var videoUrl = ""
    private var currentAnimation: ObjectAnimator? = null
    private var onVideoStarted: (() -> Unit?)? = null
    private var onBackButtonClicked: (() -> Unit?)? = null
    private var onVideoEnded: (() -> Unit?)? = null
    private var onReplayClicked: (() -> Unit?)? = null
    private var onSkipClicked: (() -> Unit?)? = null

    protected fun isBindingInitialized() = _binding != null

    init {
        if (this.childCount == 0) {
            _binding = VideoPlayerViewBinding.inflate(LayoutInflater.from(this.context), this, true)
            this.removeAllViews()
            this.addView(binding.root)
        }
        setClickListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListener() {
        binding.videoView.setOnLongClickListener {
            toggleVideoPlayState()
            true
        }

        binding.videoView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                pauseVideo()
            else if (event.action == MotionEvent.ACTION_UP)
                resumeVideo()
            true
        }

        binding.btnReplay.setOnClickListener {
            startVideo(videoUrl)
            onReplayClicked?.invoke()
        }

        binding.videoView.setOnCompletionListener {
            onVideoEnded?.invoke()
        }

        binding.btnBack.setDebounceClickListener {
            onBackButtonClicked?.invoke()
        }

        binding.btnSkip.setDebounceClickListener {
            onSkipClicked?.invoke()
        }
    }

    fun startVideo(url: String) {
        videoUrl = url
        binding.btnReplay.isVisible = false
        binding.videoView.apply {
            setVideoURI(Uri.parse(url))
            seekTo(0)
            onVideoStarted?.invoke()
            start()
            startProgressAnimationAndUpdate()
        }
    }

    private fun startProgressAnimationAndUpdate() {
        binding.videoView.setOnPreparedListener {
            val total = binding.videoView.duration
            currentAnimation?.cancel()
            binding.progressBar.max = total
            currentAnimation =
                ObjectAnimator.ofInt(binding.progressBar, "progress", 0, total)
            currentAnimation?.duration = total.toLong()
            currentAnimation?.interpolator = LinearInterpolator()
            currentAnimation?.start()
        }
    }

    private fun toggleVideoPlayState() {
        if (binding.videoView.isPlaying) {
            pauseVideo()
        } else {
            resumeVideo()
        }
    }

    private fun pauseVideo() {
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
            currentAnimation?.pause()
        }
    }

    private fun resumeVideo() {
        binding.videoView.start()
        currentAnimation?.resume()
    }

    fun setOnVideoStartedListener(onVideoStarted: () -> Unit) {
        this.onVideoStarted = onVideoStarted
    }

    fun setOnVideoEndedListener(onVideoEnded: () -> Unit) {
        this.onVideoEnded = onVideoEnded
    }

    fun setOnReplayVideoClickedListener(onReplayClicked: () -> Unit) {
        this.onReplayClicked = onReplayClicked
    }

    fun setOnBackButtonClickedListener(onBackButtonClicked: () -> Unit) {
        this.onBackButtonClicked = onBackButtonClicked
    }

    fun setOnSkipButtonClickedListener(onSkipClicked: () -> Unit) {
        this.onSkipClicked = onSkipClicked
    }

    fun setSkipButtonVisibility(isVisible: Boolean) {
        binding.btnSkip.isVisible = isVisible
    }

    fun setBackButtonVisibility(isVisible: Boolean) {
        binding.btnBack.isVisible = isVisible
    }

    fun setReplayButtonVisibility(isVisible: Boolean) {
        binding.btnReplay.isVisible = isVisible
    }

    fun setIsLoopingEnabled(isLoopingEnabled: Boolean = false) {
        binding.videoView.setOnPreparedListener {
            it.isLooping = isLoopingEnabled
        }
    }

    fun teardown() {
        currentAnimation?.cancel()
        currentAnimation = null
        onVideoStarted = null
        onBackButtonClicked = null
        onVideoEnded = null
        onReplayClicked = null
        onSkipClicked = null
        _binding = null
    }
}