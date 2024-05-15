package com.jar.app.feature_jar_duo.impl.ui.duo_intro_story

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentIntroStoryBinding
import com.jar.app.feature_jar_duo.impl.util.DeeplinkUtils.generateStoryDeeplink
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.SOURCE_HOME
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class DuoOnIntroStoryFragment : BaseFragment<FeatureDuoFragmentIntroStoryBinding>() {


    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var api: ContactsSyncApi

    @Inject
    lateinit var contactsSyncApi: ContactsSyncApi

    @Inject
    lateinit var prefs: PrefsApi

    private var adapter: DuoIntroStoryAdapter? = null
    private var currentPosition = 0
    private var countDownJob: Job? = null
    private var timeLeft = STORY_DURATION_MILLIS
    private var isStoryPaused = false

    private var progressBars = ArrayList<ProgressBar>()
    private var currentAnimation: ObjectAnimator? = null
    private val viewModel by viewModels<DuoIntroStoryViewModel> { defaultViewModelProviderFactory }

    val imageList = listOf<String>(
        "https://via.placeholder.com/150",
        "https://via.placeholder.com/150 ",
        "https://via.placeholder.com/150 "
    )
    private val args by navArgs<DuoOnIntroStoryFragmentArgs>()

    private val fromScreen by lazy {
        args.fromScreen
    }

    private val pendingInvites by lazy {
        args.pendingInvites
    }

    private val groupData by lazy {
        args.duoGroups
    }
    private val hasContactSynced by lazy {
        args.hasContactSynced
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentIntroStoryBinding
        get() = FeatureDuoFragmentIntroStoryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {

        setupUI()
        setupListeners()
    }


    private fun setupUI() {

        analyticsHandler.postEvent(
            DuoEventKey.Shown_Screen_Duo,
            mapOf(
                DuoEventKey.SCREEN to DuoEventKey.SCREEN_INTRO,
            )
        )
        getIntroStoryData()
        setupSkipButton()

        observeLiveData()


        binding.replayLayout.setOnClickListener {
            analyticsHandler.postEvent(
                DuoEventKey.Clicked_button_Duo_Introduction,
                mapOf(
                    DuoEventKey.CTA to DuoEventKey.Replay,
                )
            )
            startStoryView()
        }
        binding.btnProceed.setDebounceClickListener {
            analyticsHandler.postEvent(
                DuoEventKey.Clicked_button_Duo_Introduction,
                mapOf(
                    DuoEventKey.CTA to DuoEventKey.Proceed,
                )
            )
            proceedToNextScreen()
        }
        binding.skipButton.setDebounceClickListener {
            analyticsHandler.postEvent(
                DuoEventKey.Clicked_button_Duo_Introduction,
                mapOf(
                    DuoEventKey.CTA to DuoEventKey.Skip,
                )
            )
            if (fromScreen == SOURCE_HOME) {
                proceedToNextScreen()
            } else {
                popBackStack()
            }
        }
    }

    private fun proceedToNextScreen() {
        val deeplink = generateStoryDeeplink(
            DuoEventKey.SCREEN_INTRO,
            pendingInvites.orZero().toString(),
            groupData.toString(),
            false.toString()
        )
        when {
            hasContactSynced.not() -> {
                api.initiateContactsSyncFlow(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO, deeplink)
            }

            pendingInvites > 0 || groupData > 0 -> {
                navigateTo(
                    DuoOnIntroStoryFragmentDirections.actionDuoOnIntroStoryFragmentToDuosList(),
                    false,
                    popUpTo = R.id.duoOnIntroStoryFragment,
                    true
                )
            }

            else -> {
                contactsSyncApi.initiateContactsSyncFlow(
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                    deeplink
                )
            }

        }
    }

    private fun observeLiveData() {
        viewModel.introStoryLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setUpViewPager(it.duoIntroPageObjectList)
                startStoryView()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun getIntroStoryData() {
        viewModel.getIntroStoryData()
    }

    private fun setupSkipButton() {
        val storyVisitedCount = prefs.getDuoStoryViewCount()
        prefs.setDuoStoryViewCount(storyVisitedCount + 1)
        if (storyVisitedCount < 1 && fromScreen == SOURCE_HOME) {
            binding.skipButton.visibility = View.INVISIBLE
        } else {
            binding.skipButton.visibility = View.VISIBLE
        }
    }

    private fun startStoryView() {
        currentPosition = 0
        setupProgressLayout()
        setStoryData()
    }

    private fun showStoryScreen() {
        binding.groupStories.visibility = View.VISIBLE
        binding.groupProceed.visibility = View.GONE
    }

    private fun showProceedScreen() {
        binding.groupStories.visibility = View.GONE
        binding.groupProceed.visibility = View.VISIBLE
    }

    private fun setUpViewPager(data: List<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject>) {
        //disable swiping
        binding.vpGuide.isUserInputEnabled = false
        adapter = DuoIntroStoryAdapter(this::pauseSlide, this::resumeSlide)
        binding.vpGuide.adapter = adapter
        binding.vpGuide.offscreenPageLimit = data.size

        adapter?.submitList(data)
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
    }

    private fun setStoryData() {
        showStoryScreen()
        timeLeft = STORY_DURATION_MILLIS
        binding.vpGuide.setCurrentItem(currentPosition, false)
        startTimer()
        startAnimation()
    }

    private fun setupProgressLayout() {
        progressBars.clear()
        binding.llProgressBar.removeAllViews()
        val totalCount = imageList.size
        val progressBarLayoutParam =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        val spaceLayoutParam = LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT)
        val progressColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_A841FF)
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
        if (currentPosition == imageList.size)
            when (fromScreen) {
                SOURCE_HOME -> {
                    toggleCtaView(true)
                }

                else -> {
                    popBackStack()
                }
            }
        else
            setStoryData()
        progressBars.getOrNull(currentPosition - 1)?.progress = 100
    }

    private fun moveToPreviousSlide() {
        if (isMovingBackwardAllowed().not())
            return
        currentPosition--
        if (currentPosition == imageList.size - 1)
            toggleCtaView(false)
        setStoryData()
        progressBars.getOrNull(currentPosition + 1)?.progress = 0
    }

    private fun isMovingBackwardAllowed() = currentPosition > 0

    private fun isMovingForwardAllowed() =
        isStoryPaused.not() && currentPosition < imageList.size

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
        showProceedScreen()
        /* if (args.guideCta?.hasCta.orFalse()) {
             binding.clStories.isVisible = !isLast
             binding.clCtaView.isVisible = isLast
             if (isLast)
                 binding.animView.playAnimation()
             else
                 binding.animView.cancelAnimation()
         }*/
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