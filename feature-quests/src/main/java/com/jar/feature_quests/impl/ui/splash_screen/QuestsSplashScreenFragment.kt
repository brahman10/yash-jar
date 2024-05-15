package com.jar.feature_quests.impl.ui.splash_screen

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.jar.app.feature_quests.R
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class QuestsSplashScreenFragment : BaseComposeFragment() {

    private var job: Job? = null
    private val viewModel by hiltNavGraphViewModels<SplashScreenViewModel>(R.id.feature_quests)
    private val args by navArgs<QuestsSplashScreenFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun RenderScreen() {
        val state = viewModel.welcomeRewardsData.collectAsState(RestClientResult.none())
        val coroutineScope = rememberCoroutineScope()

        var start by remember { mutableStateOf(false) }
        var start1 by remember { mutableStateOf(false) }
        Box(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239)))
        {
            Image(painterResource(id = R.drawable.img), contentDescription = "", contentScale = ContentScale.Crop)
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth(0.15f)
                    .percentageOffset(5, 15),
                model = state.value.data?.data?.splashViewItems?.splashCoin1,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.2f)
                    .percentageOffset(-10, 20),
                model = state.value.data?.data?.splashViewItems?.splashCoin2,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .percentageOffset(0, 55)
                    .fillMaxWidth(0.12f),
                model = state.value.data?.data?.splashViewItems?.splashCoin3,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.12f)
                    .percentageOffset(0, 50),
                model = state.value.data?.data?.splashViewItems?.splashCoin4,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth(0.28f)
                    .percentageOffset(-10, -20),
                model = state.value.data?.data?.splashViewItems?.splashCoin5,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth(0.12f)
                    .align(Alignment.BottomStart)
                    .percentageOffset(0, -3),
                model = state.value.data?.data?.splashViewItems?.splashCoin6,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.12f)
                    .percentageOffset(10, 0),
                model = state.value.data?.data?.splashViewItems?.splashCoin7,
                contentDescription = ""
            )
            GlideImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(0.8f),
                model = state.value.data?.data?.questBanner,
                contentDescription = ""
            )
        }

        LaunchedEffect(key1 = Unit) {
            coroutineScope.launch {
                delay(3000)
                navigateToLanding()
            }
        }
    }

    private fun navigateToLanding() {
        navigateTo(
            QuestsSplashScreenFragmentDirections.actionSplashScreenFragmentToLandingScreenFragment(
                args.fromScreen
            ), popUpTo = R.id.splashScreenFragment, inclusive = true
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }

    @Stable
    fun Modifier.animateFromTop(
        offSetX: Dp = 0.dp,
        offsetYPercent: Float,
        isVisible: Boolean
    ): Modifier = composed {
        val transition = updateTransition(targetState = isVisible, label = "transition")

        val alpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 1000) }, label = "alpha"
        ) { if (it) 1f else 0f }

        val offsetY by transition.animateDp(
            transitionSpec = { tween(durationMillis = 1000) }, label = "offsetY"
        ) { if (it) LocalConfiguration.current.screenHeightDp.dp * offsetYPercent / 100 else 0.dp }

        this.then(
            Modifier
                .alpha(alpha)
                .offset(y = offsetY, x = offSetX)
        )
    }

    @Stable
    fun Modifier.animateFromLeft(
        offsetXInitial: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        offsetXPercent: Float,
        isVisible: Boolean
    ): Modifier = composed {
        val transition = updateTransition(targetState = isVisible, label = "transition")

        val alpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 1000) }, label = "alpha"
        ) { if (it) 1f else 0f }

        val offsetX by transition.animateDp(
            transitionSpec = { tween(durationMillis = 1000) }, label = "offsetY"
        ) { if (it) LocalConfiguration.current.screenWidthDp.dp * offsetXPercent / 100 else offsetXInitial }

        this.then(
            Modifier
                .alpha(alpha)
                .offset(x = offsetX, y = offsetY)
        )
    }



    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchWelcome()
    }
}