package com.jar.gold_redemption.impl.ui.brand_catalogue

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VoucherClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VouchersScreenLaunched
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VoucherTitle
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.feature_gold_redemption.shared.data.network.model.BrandCatalogoueApiData
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherProducts
import com.jar.gold_redemption.impl.ui.common_ui.GoldButton
import com.jar.gold_redemption.impl.ui.common_ui.GoldText
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.SOURCE_SCREEN
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.goldBenefitPercentage
import com.jar.gold_redemption.impl.ui.abandon_screen.AbandonScreenBottomSheet
import com.jar.app.feature_gold_redemption.shared.data.network.model.ProductFilter
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.minimumVoucherAmount
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.voucherTab
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import me.onebone.toolbar.rememberCollapsingToolbarState
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class BrandCatalougeFragment : BaseComposeFragment() {

    private val args by navArgs<BrandCatalougeFragmentArgs>()
    private val viewModel by viewModels<BrandCatalogueViewModel>  { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    private val MAX_SCROLL = 120.dp + 70.dp
    private val SHEET_PEEK_HEIGHT = 80.dp

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun CategoryDetailsCollapsingToolbar(
        brandCatalogApidata: State<BrandCatalogoueApiData?>,
        isCollapsed: State<Boolean>
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = if (!isCollapsed.value) com.jar.app.core_ui.R.color.color_8C272B else com.jar.app.core_ui.R.color.color_272239))
                .padding(start = 16.dp, end = 5.dp)
                .height(MAX_SCROLL),
//            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.fillMaxWidth(1f)) {
                GoldText(
                    text = brandCatalogApidata?.value?.discountHeader.orEmpty(),
                    modifier = Modifier.fillMaxWidth(0.5f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                GoldText(
                    text = brandCatalogApidata?.value?.description.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                )
            }
            GlideImage(
                brandCatalogApidata?.value?.imageUrl,
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = "",
                alignment = Alignment.CenterEnd,
                contentScale = ContentScale.Fit
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalToolbarApi::class)
    @Composable
    @Preview
    override fun RenderScreen() {
        val voucherCategoryList = viewModel.voucherCategoryList.observeAsState(listOf())
        val voucherCategoryTitleList =
            remember(voucherCategoryList) { derivedStateOf { voucherCategoryList.value?.map { it?.title.orEmpty() } } }

        val brandCatalogApidata = viewModel.brandCatalogApidata.observeAsState(null)
        val myOrdersText = viewModel.myOrdersText.observeAsState("")
        val pendingOrdersLD = viewModel.pendingOrdersLD.observeAsState()
        val shouldAddContentPadding = remember { derivedStateOf { !pendingOrdersLD.value?.list.isNullOrEmpty() } }
        val voucherList = viewModel.voucherList.observeAsState(listOf())
        val paymentHistoryList = viewModel.paymentHistoryList.observeAsState()
        val shouldRenderCollapsible = viewModel.shouldRenderCollapsible.observeAsState(false)
        val state = rememberCollapsingToolbarScaffoldState(rememberCollapsingToolbarState(0))
        val scrollState = rememberLazyGridState()
        val isCollapsed =
            remember(state.toolbarState) {
                derivedStateOf {
                    if (!shouldRenderCollapsible.value) {
                        false
                    } else {
                        Math.abs(state.offsetY) == state.toolbarState.maxHeight
                    }
                }
            }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = brandCatalogApidata, block = {
            if (brandCatalogApidata.value?.imageUrl.isNullOrBlank()) {
                uiScope.launch {
                    setStatusBarColor(com.jar.app.core_ui.R.color.color_272239)
                }
                delay(1000)
                state.toolbarState.expand()
            } else {
                setStatusBarColor(com.jar.app.core_ui.R.color.color_8C272B)
            }
        })
        LaunchedEffect(key1 = isCollapsed.value, block = {
            if (!isCollapsed.value && !brandCatalogApidata.value?.imageUrl.isNullOrBlank()) {
                setStatusBarColor(com.jar.app.core_ui.R.color.color_8C272B)
            } else {
                setStatusBarColor(com.jar.app.core_ui.R.color.color_272239)
            }
        })

        val scope = rememberCoroutineScope()
        val whichBottomSheet = viewModel.whichBottomSheet.observeAsState()
        val bottomSheetState = rememberBottomSheetScaffoldState()
        BottomSheetScaffold(
            modifier = Modifier.navigationBarsPadding(),
            scaffoldState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetPeekHeight = if (paymentHistoryList.value.isNullOrEmpty() || whichBottomSheet.value == BrandCatalogueViewModel.WhichBottomSheet.ABANDON) 0.dp else SHEET_PEEK_HEIGHT,
            sheetContent = {
                when (whichBottomSheet.value) {
                    BrandCatalogueViewModel.WhichBottomSheet.ABANDON, null -> RenderAbandonSheet(
                        bottomSheetState
                    )

                    BrandCatalogueViewModel.WhichBottomSheet.VOUCHER_PROCESSING -> {
                        val sheetState = remember { derivedStateOf { bottomSheetState.bottomSheetState.currentValue == BottomSheetValue.Expanded } }
                        RenderOrderProcessingBottomSheet(
                            modifier = Modifier, sheetState = sheetState, pendingOrdersLD = pendingOrdersLD.value, {
                            scope.launch {
                                if (!it) {
                                    bottomSheetState.bottomSheetState.collapse()
                                } else {
                                    bottomSheetState.bottomSheetState.expand()
                                }
                            }
                        }) { orderId, voucherId ->
                            navigateTo(BrandCatalougeFragmentDirections.actionBrandCatalougeFragmentToVoucherDetail(voucherId, orderId))
                        }
                    }
                }
            }) {
            RenderMainScreen(
                isCollapsed,
                state,
                brandCatalogApidata,
                voucherCategoryTitleList,
                voucherList,
                scrollState,
                voucherCategoryList,
                myOrdersText,
                shouldRenderCollapsible,
                shouldAddContentPadding
            )
        }
        BackHandler {
            coroutineScope.launch {
                viewModel.setWhichBottomSheet(BrandCatalogueViewModel.WhichBottomSheet.ABANDON)
                bottomSheetState.bottomSheetState.expand()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun RenderAbandonSheet(sheetState: BottomSheetScaffoldState) {
        val coroutineScope = rememberCoroutineScope()
        viewModel.abandonScreenData.value?.let {
            AbandonScreenBottomSheet(abandonScreenData = it, continuePressed = {
                coroutineScope.launch {
                    if (!viewModel.pendingOrdersLD.value?.list.isNullOrEmpty()) {
                        viewModel.setWhichBottomSheet(BrandCatalogueViewModel.WhichBottomSheet.VOUCHER_PROCESSING)
                    }
                    sheetState.bottomSheetState.collapse()
                }
            }, notNowPressed = {
                coroutineScope.launch {
                    sheetState.bottomSheetState.collapse()
                    findNavController().navigateUp()
                }
            })
        } ?: run {
            Row(Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp))
            }
        }
    }

    @Composable
    fun RenderMainScreen(
        isExpanded: State<Boolean>,
        state: CollapsingToolbarScaffoldState,
        brandCatalogApidata: State<BrandCatalogoueApiData?>,
        voucherCategoryTitleList: State<List<String>?>,
        voucherList: State<List<VoucherProducts?>>,
        scrollState: LazyGridState,
        voucherCategoryList: State<List<ProductFilter?>>,
        myOrdersText: State<String>,
        shouldRenderCollapsible: State<Boolean>,
        shouldAddContentPadding: State<Boolean>
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            RenderBaseToolBar(
                modifier = Modifier
                    .background(
                        color = colorResource(
                            id = if (shouldRenderCollapsible.value == true && !isExpanded.value) com.jar.app.core_ui.R.color.color_8C272B else com.jar.app.core_ui.R.color.color_272239
                        )
                    )
                    .zIndex(10f),
                onBackClick = { findNavController().navigateUp() },
                title = " ",
                RightSection = {
                    if (shouldRenderCollapsible.value == true && !isExpanded.value) {
                        GoldButton(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .debounceClickable { navigateToMyVouchersFragment() },
                            text = myOrdersText.value,
                            backgroundColor = R.color.color_480B10,
                            fontSize = JarTypography.body1.fontSize,
                            fontFamily = jarFontFamily
                        )
                    } else {
                        RenderImagePillButton(
                            modifier = Modifier
                                .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .debounceClickable { navigateToMyVouchersFragment() },
                            text = myOrdersText.value,
                            bgColor = com.jar.app.core_ui.R.color.color_3C3357,
                            textColor = com.jar.app.core_ui.R.color.white,
                            cornerRadius = 8.dp,
                            biggerVerticalPadding = true,
                            maxLines = 1
                        )
                    }

                })

            CollapsingToolbarScaffold(
                modifier = Modifier.background(color = colorResource(id = com.jar.app.core_ui.R.color.color_121127)),
                state = state,
                enabled = shouldRenderCollapsible.value,
                scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
                toolbar = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                    ) {
                        if (shouldRenderCollapsible.value)
                            CategoryDetailsCollapsingToolbar(brandCatalogApidata, isExpanded)
                        else
                            Row (
                                Modifier
                                    .fillMaxWidth()
                                    .height(0.dp)
                                    .background(Color.Green)) {}
                    }
                }) {
                val selectedIndex = remember { mutableStateOf<Int>(1) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127))
                ) {
                    RenderHorizontalList(selectedIndex, voucherCategoryTitleList, voucherList.value.size ) {
                        selectedIndex.value = it
                        viewModel.fetchAllVouchers(
                            voucherCategoryList.value.getOrNull(
                                it
                            )?.type
                        )
                    }
                    RenderMainList(voucherList, scrollState, shouldAddContentPadding)
                }
            }
        }
    }

    @Composable
    fun RenderMainList(
        voucherList: State<List<VoucherProducts?>>,
        scrollState: LazyGridState,
        shouldAddContentPadding: State<Boolean>
    ) {
        if (!voucherList.value.isNullOrEmpty()) {
            LazyVerticalGrid(
                state = scrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_121127)),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 0.dp, start = 16.dp, end = 16.dp, bottom = if (shouldAddContentPadding.value) (SHEET_PEEK_HEIGHT + 20.dp) else 0.dp)
            ) {
                items(voucherList.value) {
                    RenderVoucherItem(Modifier, it) {
                        navigateToProductPurchase(it)
                    }
                }
            }
        } else {
            RenderEmptySection()
        }
    }

    private fun navigateToProductPurchase(it: VoucherProducts?) {
        analyticsHandler.postEvent(
            Redemption_VoucherClicked,
            mapOf<String, String>(
                VoucherTitle to it?.title.orEmpty(),
                goldBenefitPercentage to it?.discountText.orEmpty(),
                minimumVoucherAmount to it?.startingAmountText.orEmpty(),
                voucherTab to viewModel.tabName.orEmpty(),
            )
        )
        navigateTo(BrandCatalougeFragmentDirections.actionBrandCatalougeFragmentToVoucherPurchase(it?.id.orEmpty()))
    }

    private fun navigateToMyVouchersFragment() {
        analyticsHandler.postEvent(
            GoldRedemptionAnalyticsKeys.Redemption_MyOrdersTabClicked
        )
        navigateTo(BrandCatalougeFragmentDirections.actionBrandCatalougeFragmentToMyVouchersFragment())
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
        viewModel.showToast.observe(viewLifecycleOwner) { string ->
            view?.takeIf { !string.isNullOrEmpty() }?.let {
                string.toast(it)
            }
        }
    }

    private fun getData() {
        viewModel.fetchAbandonScreenData()
        viewModel.fetchIntroScreen()
        viewModel.fetchPendingOrders()
        viewModel.fetchAllVouchers("GOLD")
    }

    private fun setupUI() {
        analyticsHandler.postEvent(Redemption_VouchersScreenLaunched, SOURCE_SCREEN, args.sourceScreen.orEmpty())
    }
}