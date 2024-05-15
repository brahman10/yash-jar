package com.jar.app.feature_weekly_magic.impl.ui.home

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.feature_weekly_magic.databinding.FragmentWeeklyChallengeHomeBinding
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class WeeklyChallengeHomeFragment : BaseFragment<FragmentWeeklyChallengeHomeBinding>() {

    @Inject
    lateinit var weeklyChallengeCommonApi: WeeklyChallengeCommonApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<WeeklyChallengeHomeFragmentArgs>()

    private var currentChallengeId = ""

    private val viewModelProvider by viewModels<WeeklyChallengeHomeViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    companion object {
        const val OPEN_INFO_DIALOG = "OPEN_INFO_DIALOG"
        const val MOVE_TO_LEFT = "MOVE_TO_LEFT"
        const val MOVE_TO_RIGHT = "MOVE_TO_RIGHT"
        const val REFRESH_DATA = "REFRESH_DATA"
        const val CHALLENGE_ID = "CHALLENGE_ID"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWeeklyChallengeHomeBinding
        get() = FragmentWeeklyChallengeHomeBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setUpClickListeners()
        setUpObservers()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
    }

    private fun setUpClickListeners() {
        binding.btnBack.setDebounceClickListener {
            registerClickEvent("Back_Arrow")
            popBackStack()
        }
        binding.btnOnBoarding.setDebounceClickListener {
            registerClickEvent("Help_Icon")
            weeklyChallengeCommonApi.showWeeklyChallengeOnBoardingDialog(
                false,
                WeeklyMagicConstants.AnalyticsKeys.Screens.Weekly_Magic_Screen
            )
        }
        childFragmentManager.setFragmentResultListener(MOVE_TO_LEFT, this) { _, data ->
            data.getString(CHALLENGE_ID)?.takeIf { it.isNotBlank() }?.let {
                replaceWeeklyChallengeHistoryFragment(it, enterFromRight = true)
            }
        }
        childFragmentManager.setFragmentResultListener(OPEN_INFO_DIALOG, this) { _, _ ->
            openInfoDialog()
        }
        childFragmentManager.setFragmentResultListener(REFRESH_DATA, this) { _, _ ->
            getData()
        }
        childFragmentManager.setFragmentResultListener(MOVE_TO_RIGHT, this) { _, data ->
            data.getString(CHALLENGE_ID)?.takeIf { it.isNotBlank() }?.let {
                if (it.equals(currentChallengeId, true)) {
                    replaceWeeklyChallengeMainFragment(enterFromRight = false, shouldAnimate = true)
                } else {
                    replaceWeeklyChallengeHistoryFragment(it, enterFromRight = false)
                }
            }
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeMetaDataFlow.collect(
                    onSuccess = {
                        currentChallengeId = it?.challengeId ?: ""
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeDetailFlow.collect(
                    onSuccess = {
                        it?.let {
                            currentChallengeId = it.challengeId ?: ""
                            setPrefData()
                            replaceWeeklyChallengeMainFragment(enterFromRight = true, shouldAnimate = false)
                            val currentTime = System.currentTimeMillis()
                            analyticsHandler.postEvent(WeeklyMagicConstants.AnalyticsKeys.WeeklyMagic_Shown_Ts,
                                mapOf(
                                    EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = currentTime, startTime = args.clickTime.toLong())
                                )
                            )
                        }
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun getData() {
        viewModel.fetchWeeklyChallengeMetaData()
        viewModel.fetchWeeklyChallengeDetails()
    }

    private fun replaceWeeklyChallengeHistoryFragment(
        challengeId: String,
        enterFromRight: Boolean
    ) {
        replaceFragment(
            WeeklyChallengeHistoryFragment.newInstance(challengeId, args.fromScreen),
            enterFromRight,
            true
        )
    }

    private fun replaceWeeklyChallengeMainFragment(
        enterFromRight: Boolean,
        shouldAnimate: Boolean
    ) {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.takeIf {
            it.numCardsCollected.orZero() != 0 && it.totalNumberofcards.orZero() == it.numCardsCollected.orZero()
        }?.let {
            replaceFragment(
                WeeklyChallengeHistoryFragment.newInstance(
                    challengeId = currentChallengeId,
                    args.fromScreen
                ),
                enterFromRight,
                shouldAnimate
            )
            if (it.currentWeekChallengeViewedStatus == false) {
                viewModel.markChallengeWon(currentChallengeId)
            }
        } ?: kotlin.run {
            replaceFragment(
                WeeklyChallengeMainFragment.newInstance(args.fromScreen),
                enterFromRight,
                shouldAnimate
            )
        }
    }

    private fun replaceFragment(
        childFragment: Fragment,
        enterFromRight: Boolean = true,
        shouldAnimate: Boolean = true
    ) {
        val fm: FragmentManager = childFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(binding.fragmentContainer.id, childFragment.apply {
            if (shouldAnimate) {
                if (enterFromRight) {
                    //exitTransition = Slide(Gravity.END)
                    enterTransition = Slide(Gravity.START)
                } else {
                    //exitTransition = Slide(Gravity.START)
                    enterTransition = Slide(Gravity.END)
                }
            }
        })
        ft.commit()
    }

    private fun registerClickEvent(optionChosen: String) {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.let {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_WeeklyMagicScreen,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to optionChosen,
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to (it.totalNumberofcards?.toString() ?: ""),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to (it.numCardsCollected?.toString() ?: "")
                )
            )

        }
    }

    private fun openInfoDialog() {
        navigateTo(
            WeeklyChallengeHomeFragmentDirections.actionWeeklyChallengeHomeToWeeklyChallengeInfoBottomSheet(),
            shouldAnimate = true
        )
    }

    private fun setPrefData() {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.let {
            if (it.numCardsCollected.orZero() > prefs.getWonMysteryCardCount() || it.challengeId != prefs.getWonMysteryCardChallengeId()) {
                prefs.setWonMysteryCardCount(it.numCardsCollected.orZero())
                prefs.setWonMysteryCardChallengeId(it.challengeId ?: "")
            }
            if (it.numCardsCollected.orZero() == it.totalNumberofcards.orZero() && it.totalNumberofcards.orZero() != 0) {
                viewModel.markChallengeWon(it.challengeId ?: "")
            }
        }
    }
}