package com.jar.health_insurance.impl.ui.insurance_plan_comparison

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.cast
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.base.BaseViewState
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_health_insurance.R
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class InsurancePlanComparison : BaseComposeFragment() {

    private val viewModel by viewModels<PlanComparisonViewModel> { defaultViewModelProviderFactory }
    private val args by navArgs<InsurancePlanComparisonArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    @Composable
    @Preview
    override fun RenderScreen() {
        val comparisonPageState by viewModel.uiState.collectAsState()
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(Color(0xFF141021))
        when (comparisonPageState) {
            is BaseViewState.Data -> {
                RenderComparisonPage(state = comparisonPageState)
            }

            is BaseViewState.Error -> {
                showErrorState(comparisonPageState)
            }

            else -> {

            }
        }

    }

    private fun showErrorState(uiState: BaseViewState<*>) {
        val errorMessage =
            uiState.cast<BaseViewState.Error<PlanComparisonState>>().value.errorMessage

        if (errorMessage.isNotEmpty()) {
            Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RenderComparisonPage(state: BaseViewState<*>) {
        val plansData by remember {
            mutableStateOf(state.cast<BaseViewState.Data<PlanComparisonState>>().value)
        }
        val builder = rememberBalloonBuilder {
            setArrowSize(10)
            setArrowPosition(1f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowOrientation(ArrowOrientation.START)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setPadding(8)
            //  setMarginHorizontal(12)
            setTextSize(8f)
            setCornerRadius(8f)
            setBackgroundColorResource(com.jar.app.core_ui.R.color.white)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setMinWidth(270)
        }

        Scaffold(
            topBar = {
                RenderBaseToolBar(onBackClick = { popBackStack() }, title = "Benefits")
            },
            backgroundColor = Color(0xFF141021)
        ) { contentPadding ->
            LazyColumn(contentPadding = contentPadding) {
                item {
                    Row(
                        modifier = Modifier
                            .heightIn(max = 60.dp)
                            .fillMaxWidth(),
                    ) {

                        Box(
                            modifier = Modifier
                                .weight(3f)
                                .fillMaxHeight()
                                .background(color = Color(0xFF252039)),
                        ) {


                            Row(
                                modifier = Modifier
                                    .align(Center)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                            }

                        }
                        plansData.planData.entries.forEachIndexed { index, (plan, attributes) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(color = Color(0xFF2E2942))
                            ) {

                                Text(
                                    modifier = Modifier
                                        .align(Center)
                                        .padding(2.dp),
                                    text = plan,
                                    fontSize = 16.sp,
                                    color = Color(0xFFEEEAFF),
                                    fontWeight = FontWeight(700)
                                )
                                if (index < plansData.planData.entries.size - 1) {
                                    Divider(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight()
                                            .background(Color(0x1A54487C))
                                            .align(CenterEnd)
                                    )
                                }
                            }
                        }


                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 60.dp)
                            .background(
                                color = Color(0xFF4A4068)
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Center)
                                .padding(vertical = 14.dp),
                            text = "BENEFITS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFD5CDF2)
                        )
                    }
                }
                plansData.planData.entries.firstOrNull()?.value?.keys?.forEachIndexed { index, attribute ->
                    item {
                        //  InsuranceItem(plansData, attribute)
                        if (index < ((plansData.planData.entries.firstOrNull()?.value?.keys?.size
                                ?: 1) - 1)
                        ) {
                            InsuranceItem(plansData, attribute, builder)
                            if (index < ((plansData.planData.entries.firstOrNull()?.value?.keys?.size
                                    ?: 1) - 2)
                            ) {
                                Divider(
                                    modifier = Modifier
                                        .height(1.dp)
                                        .fillMaxWidth()
                                        .background(color = Color(0x1A54487C))
                                )
                            }
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 60.dp)
                            .background(
                                color = Color(0xFF4A4068)
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Center)
                                .padding(vertical = 14.dp),
                            text = "WAITING PERIOD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFD5CDF2)
                        )
                    }
                }
                item {
                    val attribute = plansData.planData.entries.firstOrNull()?.value?.keys?.last()
                    Row(
                        modifier = Modifier
                            .heightIn(max = 60.dp)
                            .fillMaxWidth(),
                    ) {

                        Box(
                            modifier = Modifier
                                .weight(8f)
                                .fillMaxHeight()
                                .background(color = Color(0xFF252039)),
                        ) {


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Center),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val tooltipState = remember{ RichTooltipState()}
                                val scope = rememberCoroutineScope()

                                Text(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(0.5f, false)
                                        .padding(start = 12.dp),
                                    text = attribute.orEmpty(),
                                    fontSize = 12.sp,
                                    color = Color(0xFFEEEAFF),
                                    textAlign = TextAlign.Start
                                )
                                RichTooltipBox(
                                    title = { Text(attribute.orEmpty()) },
                                    text = { Text(plansData.planMetadata[attribute].orEmpty()) },
                                    tooltipState = tooltipState
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable {
                                                scope.launch {
                                                    tooltipState.show()
                                                }
                                            },
                                        tint = Color(0xFFCCCCCC),
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Localized Description"
                                    )
                                }
                            }

                        }
                        val data =
                            plansData.planData.values.first()[attribute] //{ index, (plan, attributes) ->
                        Box(
                            modifier = Modifier
                                .weight(8f)
                                .fillMaxHeight()
                                .background(color = Color(0xFF2E2942))
                                .background(color = Color(0xFF2E2942))
                        ) {

                            Text(
                                modifier = Modifier
                                    .align(Center)
                                    .padding(2.dp),
                                text = data.orEmpty(),
                                fontSize = 10.sp,
                                color = Color(0xFFEEEAFF),
                            )

                        }


                    }
                }
            }

        }

    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_141021)
        viewModel.onTriggerEvent(PlanComparisonEvent.LoadPlanComparison(args.providerId))

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InsuranceItem(plansData: PlanComparisonState, attribute: String, builder: Balloon.Builder) {
        val tooltipState = remember{RichTooltipState()}
        val scope = rememberCoroutineScope()
        Column {
            Row(
                modifier = Modifier
                    .heightIn(max = 60.dp)
                    .fillMaxWidth(),
            ) {

                Box(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .background(color = Color(0xFF252039)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Center),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.5f, false)
                                .padding(start = 12.dp),
                            text = attribute,
                            fontSize = 12.sp,
                            color = Color(0xFFEEEAFF),
                            textAlign = TextAlign.Start
                        )
                        RichTooltipBox(
                            title = { Text(attribute) },
                            text = { Text(plansData.planMetadata[attribute].orEmpty()) },
                            tooltipState = tooltipState
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable {
                                        scope.launch {
                                            tooltipState.show()
                                        }
                                    },
                                tint = Color(0xFFCCCCCC),
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Localized Description"
                            )
                        }
                    }
                }
                plansData.planData.entries.forEachIndexed { index, (plan, attributes) ->
                    val value = attributes[attribute].toString()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color = Color(0xFF2E2942))
                    ) {
                        val isIncluded = try {
                            when (value.lowercase()) {
                                "true" -> true
                                "false" -> false
                                else -> null
                            }
                        } catch (e: ClassCastException) {
                            null
                        }


                        if (isIncluded != null) {
                            if (isIncluded) {
                                Image(
                                    modifier = Modifier.align(Center),
                                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_tick_green),
                                    contentDescription = "greenTick"
                                )
                            } else {
                                Image(
                                    modifier = Modifier.align(Center),
                                    painter = painterResource(id = R.drawable.ic_not_included),
                                    contentDescription = "not included"
                                )
                            }
                        } else {
                            Text(
                                modifier = Modifier
                                    .align(Center)
                                    .padding(2.dp),
                                text = value,
                                fontSize = 10.sp,
                                color = Color(0xFFEEEAFF)
                            )
                        }
                        if (index < plansData.planData.entries.size - 1) {
                            Divider(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(color = Color(0x1A54487C))
                                    .align(CenterEnd)
                            )
                        }
                    }
                }


            }
        }
    }
}
