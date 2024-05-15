@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.app.feature_goal_based_saving.impl.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_goal_based_saving.impl.ui.goalSuccess.GoalSetupSuccessFragmentActions
import com.jar.app.feature_goal_based_saving.impl.ui.goalSuccess.GoalSetupSuccessFragmentViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun GoalCompletionScreen(viewModel: GoalSetupSuccessFragmentViewModel) {
    val state by viewModel.state.collectAsState()
    if (state.OnData != null) {
        val data = state.OnData
        ProvideTextStyle(defaultTextStyle()) {
            Box(modifier = Modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(android.graphics.Color.parseColor("#36276C")),
                                    Color(android.graphics.Color.parseColor("#1A162A")),
                                    Color(android.graphics.Color.parseColor("#1A162A")),
                                    Color(android.graphics.Color.parseColor("#1A162A")),
                                )
                            )
                        )
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                    ) {
                        data?.lottie?.let {
                            LottieAnimation(it)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, top = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        data?.header?.split("\n")?.forEach { line ->
                            Text(
                                text = line,
                                style = JarTypography.h1,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    GlideImage(
                        model = "${data?.goalImage}",
                        contentDescription = "Congratulation screen",
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = data?.goalName ?: "",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = JarTypography.body2,
                        color = Color(0xFFD5CDF2),
                    )

                    Text(
                        text = data?.investmentHeader ?: "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 30.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFFFFFFF),
                    )
                    Text(
                        text = "${data?.investedAmount}",
                        modifier = Modifier.padding(top = 5.dp).align(Alignment.CenterHorizontally),
                        style = JarTypography.h1,
                        color = Color.White
                    )
                    Text(
                        text = data?.timeDesc ?: "",
                        modifier = Modifier.padding(top = 5.dp).align(Alignment.CenterHorizontally),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        lineHeight = 18.sp,
                        color = Color(0xFFFFFFFF),
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    JarSecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        color = Color(android.graphics.Color.parseColor("#272239")),
                        text = data?.withdrawButton?.text ?: "",
                        isAllCaps = false,
                        onClick = {
                            viewModel.handleActions(
                                GoalSetupSuccessFragmentActions.OnClickOnWithdraw
                            )
                        })
                    JarSecondaryButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp), color = Color(
                            android.graphics.Color.parseColor("#6637E4")
                        ), borderColor = Color(
                             android.graphics.Color.parseColor("#845EE9")
                        ),
                        text = data?.newGoalButton?.text ?: "",
                        isAllCaps = false,
                        onClick = {
                            viewModel.handleActions(
                                GoalSetupSuccessFragmentActions.OnClickOnContinue(
                                    data?.newGoalButton?.deeplink ?: ""
                                )
                            )
                        })

                }
            }

        }
    }
}

@Composable
fun LottieAnimation(lottie: String) {
    val composition = rememberLottieComposition(LottieCompositionSpec.Url(lottie))
    val progress = animateLottieCompositionAsState(composition.value, iterations = 1)

    com.airbnb.lottie.compose.LottieAnimation(
        modifier = Modifier
            .fillMaxSize(),
        composition = composition.value,
        progress = { progress.value },
        contentScale = ContentScale.FillWidth,
    )
}