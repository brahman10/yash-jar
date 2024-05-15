package com.jar.gold_redemption.impl.ui.intro_screen

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.feature_gold_redemption.R
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.ExpandableFaqCard
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.BACK_BUTTON
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.BRAND_PARTNERS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CARD_NAME
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.FAQS
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.FAQ_TITLE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GET_UP_TO_10_EXTRA_GOLD_IN_YOUR_LOCKER
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.HOW_DO_VOUCHERS_WORK
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_BackClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_CardShown
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ExploreButtonClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_HomeFAQClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_HomeScreenLaunced
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.LAUNCH_SOURCE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_CardShown_Ts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class IntroScreenFragment : BaseComposeFragment() {

    private val viewModel by hiltNavGraphViewModels<IntroScreenViewModel> (R.id.feature_redemption_navigation)
    private val args by navArgs<IntroScreenFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    @Preview
    override fun RenderScreen() {
        val faqList = viewModel.faqList.observeAsState()
        val myOrdersText = viewModel.myOrdersText.observeAsState()
        val brandPartnersList = viewModel.brandPartnersList.observeAsState()
        val goldDiamondTableImageLink = viewModel.goldDiamondTableImageLink.observeAsState()
        val expandadedContentData = viewModel.expandadedContentData.observeAsState()
        val faqSelectedIndex = remember { mutableStateOf<Int>(-1) }
        val state: LazyListState = rememberLazyListState()
        viewModel.apiResponseCount += 1

        val firstVisibleItemIndex = state.firstVisibleItemIndex
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
        ) {
            RenderToolBar(myOrdersText, {
                navigateToMyVouchersScreen()
            }) {
                analyticsHandler.postEvent(Redemption_BackClicked, BACK_BUTTON, "INTRO_SCREEN")
                findNavController().navigateUp()
            }
            Box(Modifier.fillMaxWidth()) {
            LazyColumn(
                Modifier
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_711C21)) // com.jar.app.core_ui.R.color.color_121127 else
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
                    .align(alignment = Alignment.TopStart),
                state = state
            ) {
                item(0) {
                    MainContent(
                        goldDiamondTableImageLink = goldDiamondTableImageLink,
                        fromScreen = args.sourceScreen,
                        startTime = args.clickTime.toLong(),
                        eventName = Redemption_CardShown_Ts,
                        apiResponseCount = viewModel.apiResponseCount,
                        analyticsHandler = analyticsHandler
                    )
                }
//                if (isExpanded.value) {
                    item(1) {
                        brandPartnersList.value?.let { BrandImagesContainer(it) }
                    }
                    item(2) {
                        ExpandedContent1(expandadedContentData?.value?.voucherEducationHeader)
                    }
                    item(3) {
                        ExpandedContent2(expandadedContentData?.value?.vouchersEducationList)
                    }
                    item(4) {
                        ExpandedContent3(expandadedContentData?.value?.footer.orEmpty(), expandadedContentData?.value?.footerImage.orEmpty())
                    }
                    faqList.value?.let {
                        item(5) {
                            Text(
                                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_faqs),
                                style = JarTypography.h2,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                                    .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                            )
                        }
                        renderExpandableFaqList(
                            this, it, faqSelectedIndex,
                            listBackgroundColor = com.jar.app.core_ui.R.color.color_121127,
                            cardBackgroundColor = com.jar.app.core_ui.R.color.color_272239,
                            questionTextColor = com.jar.app.core_ui.R.color.white,
                            answerTextColor = com.jar.app.core_ui.R.color.color_D5CDF2,
                            onClick = {index ->
                                it.getOrNull(index)?.let { faq ->
                                    analyticsHandler.postEvent(Redemption_HomeFAQClicked, FAQ_TITLE, faq.faqHeaderText)
                                    uiScope.launch {
                                        state.scrollBy(100f)
                                    }
                                }
                            }
                        )
                    }
//                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                .padding(20.dp)
                .align(alignment = Alignment.BottomStart)
            ) {
                JarPrimaryButton(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
                    text = expandadedContentData?.value?.footerButtonText ?: stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_buy_vouchers),
                    onClick = {
                        navigateToBrandCatalogueFragment()
                        analyticsHandler.postEvent(Redemption_ExploreButtonClicked)
                              },
                    fontSize = 16.sp,
                )
            }
        }
    }

        val array = remember { arrayOf(false, false, false, false) }

        LaunchedEffect(firstVisibleItemIndex) {
            if (array.getOrNull(firstVisibleItemIndex) == false && expandadedContentData.value != null) {
                array[firstVisibleItemIndex] = true
                analyticsHandler.postEvent(
                    Redemption_CardShown,
                    CARD_NAME, getFromPosition(firstVisibleItemIndex)
                )
            }
        }
    }

    private fun getFromPosition(firstVisibleItemIndex: Int): String {
        // These are analytics strings
        return if (firstVisibleItemIndex >= 4) {
            ""
        } else if (firstVisibleItemIndex >= 3) {
            BRAND_PARTNERS
        } else if (firstVisibleItemIndex >= 2) {
            HOW_DO_VOUCHERS_WORK
        } else if (firstVisibleItemIndex >= 1) {
            GET_UP_TO_10_EXTRA_GOLD_IN_YOUR_LOCKER
        } else if (firstVisibleItemIndex >= 0) {
            FAQS
        } else {
            ""
        }
    }

    private fun navigateToMyVouchersScreen() {
        navigateTo(IntroScreenFragmentDirections.actionIntroScreenFragmentToMyVouchersFragment())
    }

    private fun navigateToBrandCatalogueFragment() {
        navigateTo(IntroScreenFragmentDirections.actionIntroScreenFragmentToBrandCatalougeFragment("INTRO_SCREEN"))
    }


    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            Redemption_HomeScreenLaunced, LAUNCH_SOURCE, args.sourceScreen.orEmpty()
        )
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewModel.isLoadingShown.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
    }

    private fun getData() {
        viewModel.fetchIntroScreen()
        viewModel.fetchIntroScreen2()
        viewModel.fetchFaqs()


    }

    private fun setupUI() {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_480B10)
    }
}