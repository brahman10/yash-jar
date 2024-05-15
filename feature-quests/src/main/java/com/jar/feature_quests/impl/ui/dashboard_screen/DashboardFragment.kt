package com.jar.feature_quests.impl.ui.dashboard_screen

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.data.event.*
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.BaseConstants.BuyGoldFlowContext.QUESTS
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.app.feature_spin.api.SpinApi
import com.jar.app.core_base.domain.model.JackPotResponseV2
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.feature_quests.impl.ui.splash_screen.percentageOffset
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.shared.domain.model.QUEST_CLICK_ACTION
import com.jar.feature_quests.shared.domain.model.Quest
import com.jar.feature_quests.shared.domain.model.QuestStatus
import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@OptIn(ExperimentalGlideComposeApi::class)
@AndroidEntryPoint
internal class DashboardFragment : BaseComposeFragment() {

    @Inject
    lateinit var prefsApi: PrefsApi

    private val viewModel by viewModels<DashboardViewModel> { defaultViewModelProviderFactory }

    private var networkJob: Job? = null

    private val args by navArgs<DashboardFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    fun RenderRedInternetBar(shouldShowInternetNotWorking: Boolean) {
        AnimatedVisibility(
            visible = shouldShowInternetNotWorking,
            enter = slideInVertically(tween(durationMillis = 500, easing = Ease)),
            exit = slideOutVertically(tween(durationMillis = 500, easing = Ease))
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E))
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painterResource(id = R.drawable.ic_wifi_off), "")
                Text(
                    stringResource(com.jar.app.feature_quests.R.string.feature_quests_internet_not_working),
                    style = JarTypography.body1,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun RenderScreen() {
        val uiState = viewModel.dashboardStateFlow.collectAsStateWithLifecycle()
        Box(Modifier.fillMaxSize()) {
            Column {
                RenderBaseToolBar(
                    modifier = Modifier.background(colorResource(id = R.color.color_272239)),
                    onBackClick = {
                        viewModel.fireClickedAnalyticsEvent(
                            mapOf(
                                QuestEventKey.Properties.button_type to QuestEventKey.Values.back_button,
                                QuestEventKey.Properties.card_swipe to QuestEventKey.Values.no
                            )
                        )
                        popBackStack()
                    },
                    title = stringResource(com.jar.app.feature_quests.R.string.feature_quests_quest)
                )
                RenderRedInternetBar(uiState.value.shouldShowInternetNotWorking)
                RenderDashboard(Modifier, uiState.value)
            }

            AnimatedVisibility(
                visible = uiState.value.shouldShowCongratsCard,
                enter = fadeIn(tween(durationMillis = 300, easing = Ease)),
                exit = fadeOut(tween(durationMillis = 300, easing = Ease))
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Url(
                            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                        )
                    )
                    val progress by animateLottieCompositionAsState(composition, iterations = 1)

                    LottieAnimation(
                        modifier = Modifier.fillMaxSize(),
                        composition = composition,
                        progress = { progress },
                        contentScale = ContentScale.FillWidth,
                    )
                    showPopup {
                        viewModel.onShownCongratsCard()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun RenderDashboard(modifier: Modifier, value: DashboardViewState) {
        val data = value.dashboardResponse?.data?.data ?: return
        CoinRowBackground(
            modifier = modifier
                .fillMaxHeight()
                .background(colorResource(id = R.color.color_100C24)),
            data
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GlideImage(
                    modifier = Modifier
                        .fillMaxHeight(0.15f)
                        .padding(top = 30.dp, start = 8.dp, end = 8.dp),
                    model = data.questBanner,
                    contentDescription = ""
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
                CardShadowCarousel(Modifier, data,
                    playCta = {
                        handlePlayDashboard(it)
                    },
                    cardSwiped = {
                        viewModel.fireClickedAnalyticsEvent(
                            mapOf(
                                QuestEventKey.Properties.card_swipe to QuestEventKey.Values.yes
                            )
                        )
                    }
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                )
                ViewRewardsSection()
            }
        }
    }

    @Inject
    lateinit var spinGameApiRef: Lazy<SpinApi>


    private val spinsApi by lazy {
        spinGameApiRef.get()
    }

    fun handlePlayDashboard(quest: Quest) {
        if (quest.getStatusEnum() == QuestStatus.UNLOCKED) {
            viewModel.markGameInProgress(quest)
        } else {
            handleNavigationForQuest(quest)
        }
    }

    private fun handleNavigationForQuest(quest: Quest) {
        viewModel.fireClickedAnalyticsEvent(
            mapOf(
                QuestEventKey.Properties.button_type to
                        if ((quest.getStatusEnum() == QuestStatus.COMPLETED &&
                                    quest.cardDetails?.questCardCta?.getClickActionEnum() == QUEST_CLICK_ACTION.DEEP_LINK)
                            || quest.cardDetails?.questCardCta?.getClickActionEnum() == QUEST_CLICK_ACTION.VIEW_REWARDS
                        )
                            QuestEventKey.Values.view_rewards_earned
                        else
                            QuestEventKey.Values.play,
                QuestEventKey.Properties.level_type to quest.type.orEmpty(),
                QuestEventKey.Properties.card_swipe to QuestEventKey.Values.no
            )
        )
        when (quest.cardDetails?.questCardCta?.getClickActionEnum()) {
            QUEST_CLICK_ACTION.SPIN_GAME -> {
                spinsApi.openSpinFragmentV2(
                    SpinsContextFlowType.QUESTS,
                    com.jar.app.feature_quests.R.id.dashboardFragment.toString()
                )
            }

            QUEST_CLICK_ACTION.QUIZ_GAME -> {
                navigateToTrivia()
            }

            QUEST_CLICK_ACTION.TXN_GAME -> {
                EventBus.getDefault().post(OpenBuyGoldEvent(QUESTS))
            }

            QUEST_CLICK_ACTION.VIEW_REWARDS -> {
                navigateToRewardSection()
            }

            QUEST_CLICK_ACTION.DEEP_LINK -> {
                val fromScreen = BaseConstants.QuestFlowConstants.QUESTS
                prefsApi.setUserLifeCycleForMandate(fromScreen)
                EventBus.getDefault().post(
                    HandleDeepLinkEvent(
                        deepLink = quest.cardDetails?.questCardCta?.deeplink!!,
                        fromScreen = fromScreen
                    )
                )
                // !! added purposely for this to crash if deeplink is null from backend
            }

            else -> {
                throw Exception("Should have been handled")
            }
        }
    }

    @Composable
    fun ViewRewardsSection() {
        Row(
            Modifier
                .fillMaxWidth()
                .debounceClickable {
                    viewModel.fireClickedAnalyticsEvent(
                        mapOf(
                            QuestEventKey.Properties.button_type to QuestEventKey.Values.view_all_rewards,
                            QuestEventKey.Properties.card_swipe to QuestEventKey.Values.no
                        )
                    )
                    navigateToRewardSection()
                }, horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(com.jar.app.feature_quests.R.string.feature_quests_view_all_rewards),
                style = JarTypography.body1.copy(textDecoration = TextDecoration.Underline),
                color = Color.White
            )
        }
    }

    private fun navigateToTrivia() {
        navigateTo(DashboardFragmentDirections.actionDashboardFragmentToQuestTriviaScreenFragment())
    }

    private fun navigateToRewardSection() {
        navigateTo("android-app://com.jar.app/questRewards/${args.fromScreen}")
    }

    @Composable
    fun CoinRowBackground(
        modifier: Modifier,
        data: QuestsDashboardData?,
        content: @Composable () -> Unit
    ) {
        Box(modifier) {
            Image(
                painterResource(id = com.jar.app.feature_quests.R.drawable.img),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .percentageOffset(0, 0)
                    .fillMaxWidth(0.1f),
                model = data?.bgCoinImage2.orEmpty(),
                contentDescription = ""
            )
            content()
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .percentageOffset(0, 0)
                    .fillMaxWidth(0.1f),
                model = data?.bgCoinImage1.orEmpty(),
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .percentageOffset(0, 15)
                    .fillMaxWidth(0.1f),
                model = data?.bgCoinImage3.orEmpty(),
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .percentageOffset(0, 5)
                    .fillMaxWidth(0.1f),
                model = data?.bgCoinImage4.orEmpty(),
                contentDescription = ""
            )
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.color_272239)
        setNavigationBarColor(R.color.color_272239)
        viewModel.fetchHomeState()
        observeLiveData()
        EventBus.getDefault().post(RefreshHomeFeedEvent())
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.markInProgressFLow.collectLatest {
                    handleNavigationForQuest(it)
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Pair<String, JackPotResponseV2?>>(BaseConstants.QuestFlowConstants.QUEST_BRAND_COUPON)
            ?.observe(viewLifecycleOwner) {
                val data = it?.second?.spinBrandCouponOutcomeResponse ?: return@observe
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    BaseConstants.QuestFlowConstants.QUEST_BRAND_COUPON,
                    null
                )
                data?.let { brandCouponOutcome ->
                    navigateTo(
                        DashboardFragmentDirections.actionDashboardFragmentToJackpotPopupDialog(
                            it.first,
                            brandCouponOutcome
                        )
                    )
                }
            }
    }
}