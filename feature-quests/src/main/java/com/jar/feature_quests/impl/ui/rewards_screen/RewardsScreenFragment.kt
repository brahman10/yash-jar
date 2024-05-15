package com.jar.feature_quests.impl.ui.rewards_screen

import android.os.Bundle
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.impl.util.QuestEventKey.Properties.back_button
import com.jar.feature_quests.impl.util.QuestEventKey.Properties.reward_tiles
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class RewardsScreenFragment : BaseComposeFragment() {

    private var job: Job? = null
    private val viewModel by viewModels<RewardsScreenViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<RewardsScreenFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun RenderScreen() {
        val state = viewModel.welcomeRewardsData.collectAsStateWithLifecycle(RestClientResult.none())

        var start by remember { mutableStateOf(false) }
        var start1 by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                RenderBaseToolBar(
                    modifier = Modifier.background(colorResource(id = R.color.color_272239)),
                    onBackClick = {
                        viewModel.fireClickedAnalyticEvent(back_button)
                        popBackStack()
                    }, title = state.value.data?.data?.title.orEmpty()
                )
            },
            backgroundColor = Color(0xFF141021)
        ) { contentPadding ->
            RewardsScreenCoinBackground(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                data = state.value.data?.data
            ) {
                val missedRewardCount = state.value.data?.data?.missedRewardsCount.orZero()
                val unLockedRewardCount = state.value.data?.data?.unlockedRewardsCount.orZero()
                val totalRewardCount = state.value.data?.data?.rewardsList?.size
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                    state.value.data?.data?.rewardsList?.let {
                        RenderList(it) { rewardItem, index ->
                            viewModel.fireClickedAnalyticEvent(reward_tiles)
                            rewardItem.deeplink?.takeIf { it.isNotEmpty() }?.let { deepLink ->
                                EventBus.getDefault().post(HandleDeepLinkEvent(deepLink))
                            }
                        }
                    }
                    totalRewardCount?.takeIf { it != 0 }?.let {
                        if (unLockedRewardCount + missedRewardCount == it) {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                            )
                            ViewRewardsInOfferSection()
                        }
                    }
                    Spacer(Modifier.fillMaxWidth().weight(1f))
                }
            }
        }

        BackHandler {
            viewModel.fireClickedAnalyticEvent(back_button)
            popBackStack()
        }
    }

    @Composable
    fun ViewRewardsInOfferSection() {
        Row(
            Modifier
                .fillMaxWidth()
                .debounceClickable {
                    viewModel.fireClickedAnalyticEvent(QuestEventKey.Values.view_in_offers)
                    navigateTo("android-app://com.jar.app/offerListPage")
                }, horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(com.jar.app.feature_quests.R.string.feature_quests_view_in_offer_section),
                style = JarTypography.body2.copy(textDecoration = TextDecoration.Underline),
                color = Color.White
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchWelcome()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }


    override fun setup(savedInstanceState: Bundle?) {
        observeFlows()
    }

    private fun observeFlows() {
    }
}