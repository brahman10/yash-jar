package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants.WINNINGS_ANIMATION_FINISHED
import com.jar.app.weekly_magic_common.databinding.FragmentWinCardOrChallengeParentBinding
import com.jar.app.weekly_magic_common.impl.events.RedirectToWeeklyChallengeEvent
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class WinCardOrChallengeParentFragment : BaseFragment<FragmentWinCardOrChallengeParentBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<WinCardOrChallengeParentFragmentArgs>()

    private val viewModelProvider by viewModels<WinCardOrChallengeViewModelAndroid> {
        defaultViewModelProviderFactory
    }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    private var weeklyChallengeDetail: WeeklyChallengeDetail? = null

    private var currentPos = -1

    companion object {
        const val MOVE_TO_NEXT = "MOVE_TO_NEXT"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWinCardOrChallengeParentBinding
        get() = FragmentWinCardOrChallengeParentBinding::inflate

    override fun setupAppBar() =
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))


    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setObservers()
        setupListener()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
        setPrefData()
    }

    private fun getData() {
        viewModel.fetchWeeklyChallengeDetailById(args.challengeId)
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeFlow.collect(
                    onSuccess = { weeklyChallengeDetail ->
                        weeklyChallengeDetail?.let {
                            setupUI(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }


    private fun setupUI(data: WeeklyChallengeDetail) {
        weeklyChallengeDetail = data

        /* Didnt understand the logic behind the lottie animations so commenting out and for workaround for now, will clean up later*/
     //   moveToNextScreen()


        replaceFragment(WinCardOrChallengeChildChallengeAnimationFragment.newInstance(
            weeklyChallengeDetail!!
        ))
    }

    private fun setupListener() {
        childFragmentManager.setFragmentResultListener(MOVE_TO_NEXT, this) { _, _ ->
            moveToNextScreen()
        }
    }

    private fun moveToNextScreen() {
        if (weeklyChallengeDetail == null) {
            getCustomString(MR.strings.feature_weekly_magic_common_details_not_found).snackBar(binding.root)
        }
        finishScreen()
        /* Didnt understand the logic behind the lottie animations so commenting out and for workaround for now, will clean up later*/
       /* currentPos += 1
        when (currentPos) {
            0 -> replaceFragment(
                WinCardOrChallengeChildCardAnimationFragment.newInstance(
                    args.showPurchaseTextAnimation,
                    args.fromScreen,
                    weeklyChallengeDetail!!
                )
            )
            1 -> replaceFragment(
                WinCardOrChallengeChildCardResultFragment.newInstance(
                    weeklyChallengeDetail!!,
                    args.fromScreen
                )
            )
            2 -> {
                if(challengeIsNotWon()){
                    finishScreen()
                }else
                    replaceFragment(WinCardOrChallengeChildChallengeAnimationFragment.newInstance(
                    weeklyChallengeDetail!!
                ))
            }
            else -> {
               finishScreen()
            }

        }*/
    }

    private fun finishScreen() {
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
        if (args.launchWeeklyHome) {
            popBackStack()
            EventBus.getDefault().postSticky(RedirectToWeeklyChallengeEvent(args.fromScreen))
        } else {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.set(WINNINGS_ANIMATION_FINISHED, true)
            popBackStack()
        }
    }

    private fun challengeIsNotWon(): Boolean {
        viewModel.weeklyChallengeFlow.value.data?.data?.let {
            return it.totalNumberofcards.orZero() == 0 || it.totalNumberofcards.orZero() != it.numCardsCollected.orZero()
        }
        return true
    }

    private fun replaceFragment(
        childFragment: Fragment
    ) {
        val fm: FragmentManager = childFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(binding.fragmentContainer.id, childFragment.apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        })
        ft.commit()
    }

    private fun setPrefData() {
        viewModel.weeklyChallengeFlow.value.data?.data?.let {
            if(it.numCardsCollected.orZero() > prefs.getWonMysteryCardCount() || it.challengeId != prefs.getWonMysteryCardChallengeId()){
                prefs.setWonMysteryCardCount(it.numCardsCollected.orZero())
                prefs.setWonMysteryCardChallengeId(it.challengeId?:"")
            }
            if(it.numCardsCollected.orZero() == it.totalNumberofcards.orZero() && it.totalNumberofcards.orZero() != 0){
                viewModel.markWeeklyChallengeWinViewed(it.challengeId?:"")
            }
        }
    }
}