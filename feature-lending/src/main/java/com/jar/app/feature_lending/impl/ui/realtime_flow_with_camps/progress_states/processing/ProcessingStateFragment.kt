package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.progress_states.processing

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class ProcessingStateFragment : BaseComposeFragment() {
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        ProcessingStateScreen()
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        //TODO : Ankur revert
        uiScope.launch {
            delay(3000)
            navigateTo(
                "android-app://com.jar.app/setupSuccessStateFragment",
                popUpTo = com.jar.app.feature_lending.R.id.realtimeSelectBankFragment, inclusive = false
            )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}

@Composable
private fun ProcessingStateScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .size(88.dp)
                .align(Alignment.CenterHorizontally),
            color = colorResource(id = R.color.color_EEEAFF),
            strokeWidth = 8.dp,
            backgroundColor = colorResource(id = R.color.color_776e94),
            strokeCap = StrokeCap.Round
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_processing_your_request.resourceId),
            fontSize = 18.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = R.color.white)
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_it_may_take_a_few_seconds.resourceId),
            fontSize = 14.sp,
            fontFamily = jarInterFontFamily,
            color = colorResource(id = R.color.color_D5CDF2)
        )
    }
}

@Preview
@Composable
private fun ProcessingStateScreenPreview() {
    ProcessingStateScreen()
}