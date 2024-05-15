package com.jar.health_insurance.impl.ui.benefits_page

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.views.CircularLayout
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_health_insurance.shared.data.models.benefits.Benefit
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_BenefitsClicked
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class BenefitsPage : BaseComposeFragment() {

    private val benefitsPageViewModel by viewModels<BenefitsScreenViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    private val args by navArgs<BenefitsPageArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
        benefitsPageViewModel.onTriggerEvent(BenefitsEvent.LoadBenefits(insuranceId = args.insuranceId))
    }

    @Preview
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun RenderScreen() {
        val uiState by benefitsPageViewModel.uiState.collectAsStateWithLifecycle()
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(Color(0xFF141021))
        RenderBenefitsPage(uiState = uiState)
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun RenderBenefitsPage(
        uiState: BenefitsPageState
    ) {

        uiState.errorMessage?.let { errorMessage ->
            ErrorToastMessage(errorMessage = errorMessage) {
                benefitsPageViewModel.onTriggerEvent(BenefitsEvent.ErrorMessageDisplayed)
            }
        }
        Scaffold(topBar = {
            Column {
                uiState.toolBarTitle?.let {
                    RenderBaseToolBar(
                        onBackClick = { popBackStack() },
                        title = it
                    )
                }
                Divider(
                    color = Color(0x1AACA1D3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }, containerColor = Color(0xFF141021)) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 25.dp)
            ) {

                itemsIndexed(uiState.benefitsList) { index, item ->
                    BenefitCard(
                        modifier = Modifier,
                        benefit = item,
                        position = index,
                        listSize = uiState.benefitsList.size,
                        isInitiallyExpanded = index == 0,
                    )
                }
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_141021)
    }

    @Composable
    fun BenefitCard(
        modifier: Modifier = Modifier,
        position: Int,
        listSize: Int,
        benefit: Benefit,
        isInitiallyExpanded: Boolean,
    ) {

        val cardModifier = when (position) {
            0 -> {
                Modifier
                    .background(
                        color = Color(0xFF2E2942),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
            }

            listSize - 1 -> {
                Modifier
                    .background(
                        color = Color(0xFF2E2942),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
            }

            else -> {
                Modifier
                    .background(
                        color = Color(0xFF2E2942)
                    )
            }
        }
        Box(
            modifier = cardModifier
        ) {
            Column(modifier = modifier) {
                CardContent(benefit)
                if (position != listSize - 1) {
                    Divider(
                        color = Color(0xFF463C69),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }
        }
    }


    @Composable
    @Preview
    fun CardContent(
        benefit: Benefit = Benefit(
            header = "title",
            subText = "subtext",
            id = "1",
            isExpanded = false
        ),
    ) {

        var isExpanded by remember {
            mutableStateOf(benefit.isExpanded)
        }

        val interactionSource = remember { MutableInteractionSource() }
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        isExpanded = !isExpanded
                        benefitsPageViewModel.onTriggerEvent(
                            BenefitsEvent.OnCardExpanded(
                                benefit.id
                            )
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                CircularLayout(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = benefit.id,
                        color = Color(0xFFACA1D3),
                        textAlign = TextAlign.Center

                    )
                }


                Text(
                    modifier = Modifier
                        .weight(8f)
                        .padding(start = 14.dp),
                    text = benefit.header,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )



                Icon(
                    modifier = Modifier
                        .weight(1f),
                    tint = Color.White,
                    imageVector = if (benefit.isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (benefit.isExpanded) {
                        "SHOW LESS"
                    } else {
                        "SHOW MORE"
                    },
                )

            }
            Row {
                Spacer(modifier = Modifier.weight(1f))
                if (isExpanded) {

                    LaunchedEffect(Unit) {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                HealthInsuranceEvents.Tab to benefit.header,
                                HealthInsuranceEvents.EVENT_NAME to Insurance_BenefitsClicked
                            )
                        )
                    }
                    Text(
                        modifier = Modifier
                            .weight(8f)
                            .padding(start = 14.dp),
                        text = benefit.subText,
                        color = Color(0xFFACA1D3),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(400)

                    )
                }
                Column(modifier = Modifier.weight(1f)) {}
            }

        }
    }
}
