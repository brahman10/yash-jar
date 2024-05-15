package com.jar.app.feature_goal_based_saving.impl.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_ui.R
import com.jar.app.feature_goal_based_saving.impl.ui.goalSetting.GoalSettingFragmentActions
import com.jar.app.feature_goal_based_saving.impl.ui.goalSetting.GoalSettingFragmentViewModel
import com.jar.app.feature_goal_based_saving.shared.data.model.AutoSaveDetails
import com.jar.app.feature_goal_based_saving.shared.data.model.GBSSettingResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.ProgressStatus

@Composable
internal fun GBSSettingsUI(viewModel: GoalSettingFragmentViewModel) {
    val state by viewModel.state.collectAsState()
    val data = state.onData
    if (data != null) {
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        ProvideTextStyle(defaultTextStyle()) {
            Scaffold (
                scaffoldState = scaffoldState,
                content = {
                    Body(data, viewModel, it)
                },
                bottomBar = {
                    val status = ProgressStatus.fromString(data.progressStatus)
                    when(status) {
                        ProgressStatus.ACTIVE -> {
                            BottomButtons(viewModel, data)
                        }
                        ProgressStatus.IN_PROGRESS -> {
                            ButtonContactUs(viewModel)
                        }
                        else -> Unit
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun Body(
    data: GBSSettingResponse,
    viewModel: GoalSettingFragmentViewModel,
    paddingValues: PaddingValues
) {
    val goalState = ProgressStatus.fromString(data.progressStatus)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor("#272239")),
                        Color(android.graphics.Color.parseColor("#272239"))
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (goalState == ProgressStatus.IN_PROGRESS) {
            val topBannerDetails = data.progressResponse?.banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(android.graphics.Color.parseColor("#D3A25F")),
                                    Color(android.graphics.Color.parseColor("#D3A25F"))
                                )
                            )
                        )
                        .clickable {
                            viewModel.handleAction(
                                GoalSettingFragmentActions.OnClickOnPendingBanner(
                                    topBannerDetails?.deeplink ?: ""
                                )
                            )
                        }
                ) {
                    GlideImage(
                        model = "${topBannerDetails?.iconLink}",
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = "${topBannerDetails?.text}",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {

                            }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Aligning Image and Image
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = "Forward",
                        modifier = Modifier
                            .padding(end = 10.dp, top = 5.dp)
                            .align(Alignment.CenterVertically),
                        tint = Color.Black,
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp), content = {
                val setupDetails = data.progressResponse?.setupDetails
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = setupDetails?.header ?: "",
                        modifier = Modifier
                            .padding(start = 16.dp, top = 10.dp)
                            .align(Alignment.CenterStart),
                        style = TextStyle(fontSize = 20.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                    )
                    if (goalState == ProgressStatus.ACTIVE) {
                        Text(
                            text = "${setupDetails?.status}",
                            modifier = Modifier
                                .padding(end = 16.dp, top = 10.dp)
                                .align(Alignment.CenterEnd)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(android.graphics.Color.parseColor("#273442")),
                                            Color(android.graphics.Color.parseColor("#273442"))
                                        )
                                    ),
                                    shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)
                                )
                                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                            style = TextStyle(fontSize = 20.sp),
                            color = Color(android.graphics.Color.parseColor("#58DDC8")),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 17.sp,
                        )
                    } else {
                        Text(
                            text = "${setupDetails?.status}",
                            modifier = Modifier
                                .padding(end = 16.dp, top = 10.dp)
                                .align(Alignment.CenterEnd)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(android.graphics.Color.parseColor("#3B313E")),
                                            Color(android.graphics.Color.parseColor("#3B313E"))
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                                )
                                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                            style = TextStyle(fontSize = 20.sp),
                            color = Color(android.graphics.Color.parseColor("#EBB46A")),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Text(
                        text = "${setupDetails?.description}",
                        modifier = Modifier
                            .padding(start = 16.dp, top = 13.dp)
                            .align(Alignment.Start),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#ACA1D3")),
                            lineHeight = 17.sp
                        ),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(android.graphics.Color.parseColor("#2E2942")),
                                        Color(android.graphics.Color.parseColor("#2E2942"))
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)
                            )
                    ) {
                        Text(
                            text = setupDetails?.goalDetails?.header ?: "",
                            modifier = Modifier
                                .padding(start = 18.dp, top = 16.dp)
                                .align(Alignment.Start),
                            style = TextStyle(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color(android.graphics.Color.parseColor("#D5CDF2")),
                            fontSize = 14.sp,
                            lineHeight = 17.sp,
                        )
                        setupDetails?.goalDetails?.goalDetailList?.forEachIndexed { index, _ ->
                            Box(
                                modifier = if (index == (setupDetails?.goalDetails?.goalDetailList?.size ?: 0) - 1) {
                                    Modifier
                                        .padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                                        .fillMaxWidth()
                                } else if(index == 0){
                                    Modifier
                                        .padding(start = 10.dp, top = 16.dp, end = 10.dp,)
                                        .fillMaxWidth()
                                }else {
                                     Modifier
                                        .padding(start = 10.dp, end = 10.dp)
                                        .fillMaxWidth()
                                }
                            ) {
                                Text(
                                    text = setupDetails?.goalDetails?.goalDetailList?.get(index)?.key ?: "",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.CenterStart),
                                    style = TextStyle(fontSize = 16.sp),
                                    color = Color(android.graphics.Color.parseColor("#ACA1D3")),
                                    fontSize = 14.sp,
                                    lineHeight = 17.sp,
                                )

                                Text(
                                    text = setupDetails?.goalDetails?.goalDetailList?.get(index)?.value ?: "",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.CenterEnd),
                                    style = TextStyle(fontSize = 16.sp),
                                    color = Color(android.graphics.Color.parseColor("#EEEAFF")),
                                    fontSize = 14.sp,
                                    lineHeight = 17.sp,
                                )
                            }
                        }
                    }
                }
            })
        }

        item {
            data.progressResponse?.autoSaveDetails?.let {
                AutoSaveDetails(it, viewModel)
            }
        }

    }
}

@Composable
internal fun BottomButtons(viewModel: GoalSettingFragmentViewModel, data: GBSSettingResponse) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor("#272239")),
                        Color(android.graphics.Color.parseColor("#272239"))
                    )
                )
            )
            .height(170.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        data.progressResponse?.trackGoalButton?.text?.let {
            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                text = "${data.progressResponse?.trackGoalButton?.text}",
                isAllCaps = false,
                onClick = {
                    viewModel.handleAction(GoalSettingFragmentActions.OnClickOnTrackGoal)
                }
            )
        }

        data.progressResponse?.endGoalButton?.text?.let {
            JarSecondaryButton(
                modifier = Modifier
                    .fillMaxWidth() ,
                text = "${data.progressResponse?.endGoalButton?.text}",
                isAllCaps = false,
                onClick = {
                    data.goalId?.let {
                        viewModel.handleAction(GoalSettingFragmentActions.OnClickOnEndGoal(it))
                    }
                })
        }
    }
}

@Composable
internal fun ButtonContactUs(viewModel: GoalSettingFragmentViewModel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Need help?",
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        viewModel?.handleAction(GoalSettingFragmentActions.OnClickOnContactUs)
                    }
            ) {
                Icon(
                    painter = painterResource(com.jar.app.feature_goal_based_saving.R.drawable.message_icon),
                    contentDescription = "Image 1",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(25.dp),
                    tint = Color.White
                )
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp),
                    text = "Contact Support",
                    color = Color(android.graphics.Color.parseColor("#EEEAFF")),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = "Image 1",
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color(android.graphics.Color.parseColor("#EEEAFF"))
                )
            }
        }
}
@Composable
internal fun AutoSaveDetails(gaolAutoSaveDetails: AutoSaveDetails?,viewModel: GoalSettingFragmentViewModel) {
    val isShowTheDetails = remember {
        mutableStateOf(gaolAutoSaveDetails?.isExpanded)
    }
    Column {
        Divider(
            color = Color(android.graphics.Color.parseColor("#3C3357")),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            Text(
                modifier = Modifier
                    .padding(
                        start = 18.dp, top = 24.dp, bottom = 18.dp
                    )
                    .align(Alignment.CenterVertically),
                text = "${gaolAutoSaveDetails?.header}",
                color = Color(
                    android.graphics.Color.parseColor("#EEEAFF")
                ),
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 4.dp,end = 18.dp)
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
                    .rotate(
                        if (isShowTheDetails.value == true)
                            -90f
                        else 90f
                    )
                    .clickable {
                        viewModel.handleAction(
                            GoalSettingFragmentActions.OnClickOnChevron
                        )
                        isShowTheDetails.value = isShowTheDetails.value?.not()
                    },
                tint = Color.White,
            )
        }

        if (isShowTheDetails.value == true) {
            Text(
                text = "${gaolAutoSaveDetails?.description}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                color = Color(android.graphics.Color.parseColor("#ACA1D3")),
                fontWeight = FontWeight.W400,
                lineHeight = 20.sp,

                )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 18.dp, end = 18.dp)
                    .border(
                        border = BorderStroke(
                            2.dp,
                            Color(android.graphics.Color.parseColor("#3C3357"))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(top = 5.dp, start = 16.dp, end = 16.dp, bottom = 5.dp)
            ) {
                gaolAutoSaveDetails?.details?.forEachIndexed { index, it ->
                    Row(
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "${gaolAutoSaveDetails?.details?.get(index)?.key}",
                            fontSize = 14.sp,
                            color = Color(android.graphics.Color.parseColor("#ACA1D3")),
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${gaolAutoSaveDetails?.details?.get(index)?.value}",
                        )
                    }
                }

            }
        }

        Divider(
            color = Color(android.graphics.Color.parseColor("#3C3357")),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
    }

}