package com.jar.feature_quests.impl.ui.landing_screen

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_quests.R
import com.jar.feature_quests.impl.ui.splash_screen.SplashScreenViewModel
import com.jar.feature_quests.impl.ui.splash_screen.percentageOffset
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class LandingScreenFragment : BaseComposeFragment() {

    private var job: Job? = null
    private val viewModel by hiltNavGraphViewModels<SplashScreenViewModel>(R.id.feature_quests)
    private val args by navArgs<LandingScreenFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarDefault(title = getString(R.string.quest)))))
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun RenderScreen() {
        val state = viewModel.welcomeRewardsData.collectAsState(RestClientResult.none())

        var start by remember { mutableStateOf(false) }
        var start1 by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()) {
            Divider(color = colorResource(id = com.jar.app.core_ui.R.color.color_403950), thickness = 1.dp)
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .percentageOffset(0, -3),
                model = state.value?.data?.data?.landingViewItems?.landingCoin1,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth(0.15f)
                    .align(Alignment.TopEnd)
                    .percentageOffset(0, -2),
                model = state.value?.data?.data?.landingViewItems?.landingCoin2,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(0.1f)
                    .percentageOffset(0, 40),
                model = state.value?.data?.data?.landingViewItems?.landingCoin3,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .align(Alignment.TopEnd)
                    .percentageOffset(0, 37),
                model = state.value?.data?.data?.landingViewItems?.landingCoin4,
                contentDescription = ""
            )

            Column (
                Modifier
                    .align(Alignment.Center)
                    .percentageOffset(0, -15)
            , horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.value?.data?.data?.landingViewItems?.textAboveBanner.orEmpty(),
                    style = JarTypography.h6.copy(color = Color.White),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                GlideImage(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    model = state.value?.data?.data?.questBanner,
                    contentDescription = ""
                )
                Text(
                    text = state.value?.data?.data?.landingViewItems?.textBelowBanner.orEmpty(),
                    style = JarTypography.h1.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
                    color = Color.White,
                )
            }

            UnlockBox(modifier = Modifier.align(Alignment.BottomCenter), data = state.value.data?.data?.landingViewItems) {
                uiScope.launch {
                    viewModel.fireClickedLandingAnalyticsEvent(
                        mapOf(
                            QuestEventKey.Properties.button_type to QuestEventKey.Values.swipe_bar,
                            QuestEventKey.Properties.swiped to QuestEventKey.Values.yes
                        )
                    )
                    viewModel.unlockDone()
                }
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(20.dp))
        }

        var scale by remember { mutableStateOf(1f) }
        LaunchedEffect(key1 = Unit) {
            animate(0f, 0.9f) { value: Float, _: Float ->
                scale = value
            }
        }

        BackHandler {
            viewModel.fireClickedLandingAnalyticsEvent(
                mapOf(
                    QuestEventKey.Properties.button_type to QuestEventKey.Values.back_button,
                    QuestEventKey.Properties.swiped to QuestEventKey.Values.no
                )
            )
            popBackStack()
        }
    }

    private fun navigateToDashboard() {
        navigateTo(
            "android-app://com.jar.app/dashboardScreen/${args.fromScreen}",
            popUpTo = R.id.landingScreenFragment,
            inclusive = true
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeFlows()
        viewModel.fireShownLandingAnalyticsEvent()
    }

    private fun observeFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.unlockApi.collectLatest {
                    uiScope.launch {
                        navigateToDashboard()
                    }
                }
            }
        }
    }

}