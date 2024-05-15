package com.jar.app.feature.guide

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.FragmentNewGuideBinding
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_base.util.orFalse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class NewGuideFragment : BaseBottomSheetDialogFragment<FragmentNewGuideBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<NewGuideFragmentArgs>()

    private var adapter: NewGuideAdapter? = null
    private var currentPosition = 0
    private var countDownJob: Job? = null
    private var timeLeft = STORY_DURATION_MILLIS
    private var isStoryPaused = false

    private var progressBars = ArrayList<ProgressBar>()
    private var currentAnimation: ObjectAnimator? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewGuideBinding
        get() = FragmentNewGuideBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        shouldShowFullHeight = true
    )

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text = args.title

        binding.tvCtaTitle.text = args.guideCta?.title
        binding.tvCtaDesc.text = args.guideCta?.description
        args.guideCta?.buttonText?.let {
            binding.btnAction.setText(it)
        }

        setUpViewPager()
        setupProgressLayout()
        setStoryData()
    }

    private fun setUpViewPager() {
        //disable swiping
        binding.vpGuide.isUserInputEnabled = false
        adapter = NewGuideAdapter(this::pauseSlide, this::resumeSlide)
        binding.vpGuide.adapter = adapter
        binding.vpGuide.offscreenPageLimit = args.storyUrls.size
        adapter?.submitList(args.storyUrls.asList())
        (binding.vpGuide.getChildAt(0) as? RecyclerView)?.let {
            it.layoutManager?.isItemPrefetchEnabled = false
            it.isNestedScrollingEnabled = false
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        //Don't make it debounce click listener
        binding.btnPrev.setOnClickListener {
            moveToPreviousSlide()
        }

        //Don't make it debounce click listener
        binding.btnNext.setOnClickListener {
            moveToNextSlide()
        }

        binding.btnClose.setDebounceClickListener {
            requireActivity().onBackPressed()
        }

        if (args.guideCta?.hasCta.orFalse()) {
            binding.btnAction.setDebounceClickListener {
                EventBus.getDefault().post(HandleDeepLinkEvent(args.guideCta?.ctaDeepLink!!))
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setStoryData() {
        timeLeft = STORY_DURATION_MILLIS
        binding.vpGuide.setCurrentItem(currentPosition, false)
        startTimer()
        startAnimation()
    }

    private fun setupProgressLayout() {
        progressBars.clear()
        binding.llProgressBar.removeAllViews()
        val totalCount = args.storyUrls.size
        val progressBarLayoutParam =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        val spaceLayoutParam = LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT)
        val progressColor = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_A841FF)
        repeat(totalCount) {
            val progressBar =
                ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
            progressBar.layoutParams = progressBarLayoutParam
            progressBar.progressDrawable.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    progressColor,
                    BlendModeCompat.SRC_ATOP
                )
            progressBar.max = 100
            progressBars.add(progressBar)
            binding.llProgressBar.addView(progressBar)
            if (it + 1 < totalCount)
                binding.llProgressBar.addView(getSpacer(spaceLayoutParam))
        }
    }

    private fun getSpacer(layoutParams: LinearLayout.LayoutParams): View {
        val space = View(context)
        space.layoutParams = layoutParams
        return space
    }

    private fun pauseSlide() {
        isStoryPaused = true
        countDownJob?.cancel()
        currentAnimation?.pause()
    }

    private fun resumeSlide() {
        isStoryPaused = false
        currentAnimation?.currentPlayTime = (STORY_DURATION_MILLIS - timeLeft)
        currentAnimation?.resume()
        startTimer()
    }

    private fun moveToNextSlide() {
        if (isMovingForwardAllowed().not())
            return
        currentPosition++
        if (currentPosition == args.storyUrls.size)
            toggleCtaView(true)
        else
            setStoryData()
        progressBars.getOrNull(currentPosition - 1)?.progress = 100
    }

    private fun moveToPreviousSlide() {
        if (isMovingBackwardAllowed().not())
            return
        currentPosition--
        if (currentPosition == args.storyUrls.size - 1)
            toggleCtaView(false)
        setStoryData()
        progressBars.getOrNull(currentPosition + 1)?.progress = 0
    }

    private fun isMovingBackwardAllowed() = currentPosition > 0

    private fun isMovingForwardAllowed() =
        isStoryPaused.not() && currentPosition < args.storyUrls.size

    private fun startTimer() {
        countDownJob?.cancel()
        countDownJob = uiScope.countDownTimer(
            totalMillis = timeLeft,
            intervalInMillis = COUNTDOWN_INTERVAL_MILLIS,
            onInterval = {
                timeLeft = it
            },
            onFinished = {
                moveToNextSlide()
            }
        )
    }

    private fun startAnimation() {
        currentAnimation?.end()
        val progressbar = progressBars[currentPosition]
        currentAnimation = ObjectAnimator.ofInt(progressbar, "progress", 0, 100)
        currentAnimation?.duration = STORY_DURATION_MILLIS
        currentAnimation?.interpolator = LinearInterpolator()
        currentAnimation?.start()
    }

    private fun toggleCtaView(isLast: Boolean) {
        if (args.guideCta?.hasCta.orFalse()) {
            binding.clStories.isVisible = !isLast
            binding.clCtaView.isVisible = isLast
            if (isLast)
                binding.animView.playAnimation()
            else
                binding.animView.cancelAnimation()
        }
    }

    override fun onDestroyView() {
        countDownJob?.cancel()
        currentAnimation?.cancel()
        super.onDestroyView()
    }

    companion object {
        const val STORY_DURATION_MILLIS = 4_000L
        const val COUNTDOWN_INTERVAL_MILLIS = 100L
    }
}