package com.jar.app.weekly_magic_common.impl

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.activity.BaseActivity
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.app.weekly_magic_common.impl.events.RedirectToWeeklyChallengeEvent
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

internal class WeeklyChallengeCommonApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val activity: FragmentActivity,
    private val appScope: CoroutineScope,
    private val prefs: PrefsApi,
    private val dispatcherProvider: DispatcherProvider,
    private val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase
) : WeeklyChallengeCommonApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }
    private var weeklyChallengeApiJob: Job? = null

    override fun showWeeklyChallengeOnBoardingDialog(
        triggerReturnResult: Boolean,
        fromScreen: String
    ) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/weeklyChallenge/onBoarding/$triggerReturnResult/$fromScreen"),
            getNavOptions(shouldAnimate = false)
        )
    }

    override fun showPreviousWeekChallengeStory(challengeId: String, fromScreen: String) {
        if (challengeId.isBlank()) {
            return
        }
        navController.navigate(
            Uri.parse("android-app://com.jar.app/weeklyChallenge/weeklyChallengeDetailFragment/${challengeId}"),
            getNavOptions(shouldAnimate = false)
        )
    }

    override fun showMysteryCardWonDialog(data: WeeklyChallengeMetaData) {
        if (isDialogAlreadyNotShowing()) {
            activity.lifecycleScope.launch(Dispatchers.Main) {
                delay(200)
                if (data.showWinAnimation(
                        prefs.getWonMysteryCardCount(),
                        prefs.getWonMysteryCardChallengeId()
                    )
                ) {
                    delay(200)
                    if (isDialogAlreadyNotShowing()) {
                        navController.navigate(
                            Uri.parse(
                                "android-app://com.jar.app/weeklyChallenge/mysteryCardWonDialogFragment/${data.challengeId}/${
                                    data.lastTxnAmount.orZero().toFloat()
                                }/${data.transactionType?.takeIf { it.isNotBlank() } ?: WeeklyMagicConstants.TransactionType.TYPE_MANUAL}"
                            ),
                            getNavOptions(shouldAnimate = true)
                        )
                        navController.currentBackStackEntry?.savedStateHandle
                            ?.getLiveData<Boolean>(WeeklyMagicConstants.On_MYSTERY_CARD_WON_DIALOG_FINISHED)
                            ?.observe(activity) {
                                activity.lifecycleScope.launch(Dispatchers.Main) {
                                    if (it != true) {
                                        markCardOrChallengeAsWon(data)
                                    } else {
                                        startWinAnimationAndWeeklyMagicHomeFlow(
                                            data,
                                            WeeklyMagicConstants.AnalyticsKeys.Screens.Home_Screen_Bottom_Sheet,
                                            false
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }

    }

    private fun isDialogAlreadyNotShowing(): Boolean {
        val label = navController.currentDestination?.label
        return label == null || label != "MysteryCardWonDialogFragment"
    }

    override fun startWinAnimationAndWeeklyMagicHomeFlow(
        dataFromServer: WeeklyChallengeMetaData,
        fromScreen: String,
        showPurchaseTextAnimation: Boolean
    ) {
        if (dataFromServer.userOnboarded == false) {
            showWeeklyChallengeOnBoardingDialog(true, fromScreen)
            navController.currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Boolean>(WeeklyMagicConstants.ON_BOARDING_ANIMATION_FINISHED)
                ?.observe(activity) {
                    activity.lifecycleScope.launch(Dispatchers.Main) {
                        showMysteryCardOrChallengeWonFlow(
                            dataFromServer,
                            fromScreen,
                            showPurchaseTextAnimation
                        )
                    }
                }
        } else {
            showMysteryCardOrChallengeWonFlow(dataFromServer, fromScreen, showPurchaseTextAnimation)
        }
    }

    private fun showMysteryCardOrChallengeWonFlow(
        dataFromServer: WeeklyChallengeMetaData,
        fromScreen: String,
        showPurchaseTextAnimation: Boolean
    ) {
        if (dataFromServer.showWinAnimation(
                prefs.getWonMysteryCardCount(), prefs.getWonMysteryCardChallengeId()
            )
        ) {
            showMysteryCardOrChallengeWonScreen(
                dataFromServer.challengeId!!,
                showPurchaseTextAnimation,
                fromScreen,
                true
            )
        } else {
            showWeeklyHomeScreen(fromScreen)
        }
    }

    private fun showWeeklyHomeScreen(fromScreen: String) {
        EventBus.getDefault()
            .postSticky(RedirectToWeeklyChallengeEvent(fromScreen))
    }

    override fun markCardOrChallengeAsWon(data: WeeklyChallengeMetaData) {
        if (data.showWinAnimation(
                prefs.getWonMysteryCardCount(),
                prefs.getWonMysteryCardChallengeId()
            )
        ) {
            if (data.cardsWon.orZero() == data.totalCards.orZero()) {
                weeklyChallengeApiJob?.cancel()
                weeklyChallengeApiJob = appScope.launch {
                    markWeeklyChallengeViewedUseCase.markCurrentWeeklyChallengeViewed(data.challengeId!!)
                        .collect(
                            onLoading = {
                                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                    withContext(dispatcherProvider.main) {
                                        (activity as BaseActivity<*>).dismissProgressBar()
                                    }
                                }
                            },
                            onSuccess = {
                                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                    withContext(dispatcherProvider.main) {
                                        (activity as BaseActivity<*>).dismissProgressBar()
                                    }
                                    EventBus.getDefault()
                                        .postSticky(RefreshWeeklyChallengeMetaEvent())
                                }
                            },
                            onError = { errorMessage, errorCode ->
                                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                    withContext(dispatcherProvider.main) {
                                        (activity as BaseActivity<*>).dismissProgressBar()
                                    }
                                    EventBus.getDefault()
                                        .postSticky(RefreshWeeklyChallengeMetaEvent())
                                }
                            }
                        )
                }
            }
            prefs.setWonMysteryCardCount(data.cardsWon.orZero())
            prefs.setWonMysteryCardChallengeId(data.challengeId!!)
        }
    }

    override fun showMysteryCardOrChallengeWonScreen(
        challengeId: String,
        showPurchaseTextAnimation: Boolean,
        fromScreen: String,
        launchWeeklyHome: Boolean
    ) {
        if (challengeId.isBlank()) {
            return
        }
        navController.navigate(
            Uri.parse("android-app://com.jar.app/weeklyChallenge/cardOrChallengeWin/$challengeId/$showPurchaseTextAnimation/${fromScreen}/${launchWeeklyHome}"),
            getNavOptions(shouldAnimate = false)
        )
    }

    override fun tearDown() {
        weeklyChallengeApiJob?.cancel()
    }
}