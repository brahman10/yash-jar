package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.bank_selection

import android.os.Bundle
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.ui.common_component.OutlinedTextFieldWithIcon
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.common_component.mokoStringResource
import com.jar.app.feature_lending.impl.ui.common_component.shimmerBrush
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.RealTimeFlowSteps
import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.bank_selection.RealtimeBankSelectionViewModel
import com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.bank_selection.SelectBankUiState
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class RealtimeSelectBankFragment : BaseComposeFragment() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModelProvider by viewModels<RealtimeBankSelectionViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchBankDataForRealtime()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.sdkDataSharedFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            //TODO : Ankur revert and Open SDK
                            openProcessingScreen()
                        }
                    },
                    onError = { errorMsg, _ ->
                        dismissProgressBar()
                        view?.rootView?.let {
                            errorMsg.snackBar(it)
                        }
                    }
                )
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    override fun RenderScreen() {
        val annotatedString = buildAnnotatedString {
            // Append the text  with different styles
            withStyle(
                style = SpanStyle(
                    fontSize = 10.sp, fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(700),
                )
            ) {
                append(" 12,000 ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = 10.sp, fontFamily = jarInterFontFamily,
                )
            ) {
                append(mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_users_improved_thier_credit_score))
            }

        }

        SelectBankScreenComposable(
            viewModel = viewModel,
            subTitle = annotatedString,
            onBankSelected = { bankData: RealtimeBankData, isPrimary: Boolean ->
                viewModel.onBankSelected(fipId = bankData.fipId.orEmpty(), isPrimary = isPrimary)
            },
            onSearchQueryTyped = {
                viewModel.onSearchQuery(query = it)
            },
            onBankNotListedButtonClicked = {
                openBankNotListedScreen()
            },
            onBankDownTimeNotifyButtonClicked = {
                viewModel.scheduleBankUptimeNotification(viewModel.uiStateFlow.value.selectedBank?.fipId.orEmpty())
            },
            onToolbarBackButtonClick = {
                popBackStack()
            },
            onToolbarNeedHelpClicked = {
                openHelpSection()
            }
        )
    }

    private fun openHelpSection() {
        requireContext().openWhatsapp(
            remoteConfigApi.getWhatsappNumber(), ""
        )
    }

    private fun openBankNotListedScreen() {
        navigateTo(
            RealtimeSelectBankFragmentDirections.actionRealtimeSelectBankFragmentToRealtimeBankNotSupportedFragment()
        )
    }

    private fun openProcessingScreen() {
        navigateTo(
            "android-app://com.jar.app/processingStateFragment"
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@ExperimentalMaterialApi
private fun SelectBankScreenComposable(
    viewModel: RealtimeBankSelectionViewModel,
    subTitle: AnnotatedString,
    onBankSelected: (bankData: RealtimeBankData, isPrimary: Boolean) -> Unit,
    onSearchQueryTyped: (query: String) -> Unit,
    onBankNotListedButtonClicked: () -> Unit,
    onBankDownTimeNotifyButtonClicked: () -> Unit,
    onToolbarBackButtonClick: () -> Unit,
    onToolbarNeedHelpClicked: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val bankState = viewModel.uiStateFlow.collectAsState()

    val selectedBankIndexState = rememberSaveable { mutableStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()

    var currentBottomSheet: BottomSheetType? by remember {
        mutableStateOf(null)
    }

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val closeSheet = {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    val openSheet = {
        coroutineScope.launch { bottomSheetState.show() }
    }
//
//    LaunchedEffect(bottomSheetState) {
//        snapshotFlow { bottomSheetState.isVisible }.collect { isVisible ->
//            if (isVisible) {
//                // Sheet is visible
//            } else {
//                keyboardController?.hide()
//            }
//        }
//    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        scrimColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = JarColors.color_2E2942,
        sheetContent = {
            currentBottomSheet?.let {
                when (it) {
                    BottomSheetType.TYPE_BANK_LIST -> {
                        BankSelectionBottomSheetContent(
                            bankState = bankState,
                            onCloseClicked = { closeSheet() },
                            onSecondaryBankSelected = onBankSelected,
                            onSearchQueryTyped = onSearchQueryTyped,
                            onBankNotListedButtonClicked = {
                                onBankNotListedButtonClicked.invoke()
                                closeSheet()
                            }
                        )
                    }
                    BottomSheetType.TYPE_BANK_DOWNTIME -> {
                        BankNotRespondingBottomSheetContent(
                            onBankDowntimeCloseClicked = { closeSheet() },
                            bankData = bankState.value.selectedBank!!,
                            onNotifyButtonClick = onBankDownTimeNotifyButtonClicked
                        )
                    }
                }
            }
        }) {
        Scaffold(
            topBar = {
                ToolbarWithHelpButton(
                    onBackButtonClick = { onToolbarBackButtonClick() },
                    title = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_select_your_bank),
                    onHelpButtonClick = { onToolbarNeedHelpClicked() }
                )
            },
            content = {
                BankSelectionMainContent(
                    modifier = Modifier
                        .padding(it),
                    bankState = bankState,
                    subTitle = subTitle,
                    onSelectOtherBankClicked = {
                        currentBottomSheet = BottomSheetType.TYPE_BANK_LIST
                        openSheet()
                    },
                    onPrimaryBankSelected = onBankSelected
                )
            },
            bottomBar = {
                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
                    iconPadding = 0.dp,
                    text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_continue),
                    isAllCaps = false,
                    onClick = {
                        if (bankState.value.selectedBank?.isDown.orFalse()) {
                            currentBottomSheet = BottomSheetType.TYPE_BANK_DOWNTIME
                            openSheet()
                        } else {
                            viewModel.fetchSdkRedirectionData(bankState.value.selectedBank?.fipId.orEmpty())
                        }
                    },
                    isEnabled = bankState.value.selectedBank != null
                )
            },
            backgroundColor = JarColors.bgColor
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun BankSelectionMainContent(
    modifier: Modifier = Modifier,
    bankState: State<SelectBankUiState>,
    subTitle: AnnotatedString,
    onPrimaryBankSelected: (bankData: RealtimeBankData, isPrimary: Boolean) -> Unit,
    onSelectOtherBankClicked: () -> Unit
) {

    Column(modifier = modifier.background(color = JarColors.bgColor)) {

        RealTimeFlowSteps(
            modifier = Modifier
                .background(Color(0xFF3E3953))
                .padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.lightBgColor),
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.feature_lending_arrow),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorResource(id = com.jar.app.core_ui.R.color.color_58DDC8))
            )

            Text(
                text = subTitle,
                textAlign = TextAlign.Center,
                color = Color.White,
                lineHeight = 17.sp
            )
        }

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_connect_your_bank_account),
            fontSize = 20.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = Color.White
        )

        BankListWithShimmer(
            isLoading = bankState.value.isLoading,
            contentAfterLoading = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(top = 4.dp, end = 12.dp),
                    content = {
                        bankState.value.topBanks?.let { list ->
                            items(list.size) { index ->
                                val bankData = list[index]

                                val shouldShowSelected = ((bankData.isPrimary.orFalse() && bankState.value.selectedBank == null)
                                        || bankState.value.selectedBank?.fipId == bankData.fipId)   //either primary or selected by user
                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 12.dp)
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = if (shouldShowSelected) Color(0xFFC5B0FF) else Color(
                                                0xFF776E94
                                            ),
                                            shape = RoundedCornerShape(size = 8.dp)
                                        )
                                        .background(
                                            color = if (shouldShowSelected) Color(0xFF3C3357) else Color.Transparent,
                                            shape = RoundedCornerShape(size = 8.dp)
                                        )
                                        .padding(12.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() } // To remove click ripple in compose both parameter required.
                                        ) {
                                            onPrimaryBankSelected.invoke(bankData, true)
                                        }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    GlideImage(
                                        model = bankData.url, contentDescription = "", modifier = Modifier
                                            .width(28.dp)
                                            .height(28.dp)
                                            .clip(shape = CircleShape)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        modifier = Modifier.wrapContentHeight(),
                                        text = bankData.bankName.orEmpty(),
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            fontFamily = jarInterFontFamily,
                                            fontWeight = FontWeight(600),
                                            color = Color(0xFFEEEAFF),
                                            textAlign = TextAlign.Center,
                                        ),
                                        minLines = 2,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    })
            })

        Spacer(modifier = Modifier.height(16.dp))

        bankState.value.secondaryBanks?.let {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF776E94),
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .clickable {
                        onSelectOtherBankClicked.invoke()
                    }
                    .padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_select_other_bank),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = jarInterFontFamily,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFEEEAFF),
                    ),
                    modifier = Modifier
                        .weight(1f, fill = true),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(JarColors.color_EEEAFF)
                )
            }
        }
    }
}

@Composable
fun BankListWithShimmer(
    isLoading: Boolean = true,
    contentAfterLoading: @Composable () -> Unit = {},
) {
    if (isLoading) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(top = 4.dp, end = 12.dp),
            content = {
                items(6) {
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp, top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = shimmerBrush(
                                        showShimmer = true
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(10.dp)
                                .height(8.dp)
                                .background(
                                    brush = shimmerBrush(
                                        showShimmer = true
                                    )
                                )
                        )
                    }
                }
            })
    } else {
        contentAfterLoading()
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun BankSelectionBottomSheetContent(
    bankState: State<SelectBankUiState>,
    onCloseClicked: () -> Unit,
    onSecondaryBankSelected: (bankData: RealtimeBankData, isPrimary: Boolean) -> Unit,
    onSearchQueryTyped: (query: String) -> Unit,
    onBankNotListedButtonClicked: () -> Unit,
) {
    val searchFieldState = rememberSaveable { mutableStateOf("") }
    val selectedBankIndexState = rememberSaveable { mutableStateOf(-1) }
    val bankList = bankState.value.secondaryBanks
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(color = JarColors.bgColor)
            .padding(bottom = 20.dp)
    ) {

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_select_your_bank),
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFEEEAFF),
                ),
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f, fill = true),
            )

            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_close),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.clickable {
                    searchFieldState.value = ""
                    selectedBankIndexState.value = -1
                    focusManager.clearFocus()
                    onCloseClicked.invoke()
                }
            )
        }

        OutlinedTextFieldWithIcon(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            value = searchFieldState.value,
            onValueChange = {
                if (it.length < 20) {
                    searchFieldState.value = it
                    onSearchQueryTyped.invoke(it)
                }
            },
            placeholderText = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_search_bank_hint),
            cursorColor = colorResource(id = com.jar.app.core_ui.R.color.color_C5B0FF),
            leadingIconView = {
                if (searchFieldState.value.isEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.feature_lending_ic_search),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                } else {
                    null
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (bankList.isNullOrEmpty()) {
            NoResultFoundBottomSheetContent(
                searchFieldState.value
            ) {
                searchFieldState.value = ""
                selectedBankIndexState.value = -1
                onBankNotListedButtonClicked.invoke()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(bankList.size) { index ->
                    val bankData = bankList[index]

                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() } // To remove click ripple in compose both parameter required.
                            ) {
                                selectedBankIndexState.value = index
                            }
                            .animateItemPlacement(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            model = bankData.url,
                            contentDescription = "",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(shape = CircleShape),
                            loading = placeholder(R.drawable.feature_lending_bank_icon_36dp),
                            failure = placeholder(R.drawable.feature_lending_bank_icon_36dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            modifier = Modifier.weight(1f),
                            text = bankData.bankName.orEmpty(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontFamily = jarInterFontFamily,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFEEEAFF),
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Image(
                            painter = painterResource(id = if (selectedBankIndexState.value == index) R.drawable.feature_lending_ic_radio_selected_v2 else R.drawable.feature_lending_ic_radio_unselected),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Divider(color = JarColors.color_3C3357, thickness = 1.dp)

            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                iconPadding = 0.dp,
                text = stringResource(com.jar.app.core_ui.R.string.core_ui_confirm),
                isAllCaps = false,
                onClick = {
                    bankList.getOrNull(selectedBankIndexState.value)?.let {
                        onSecondaryBankSelected(it, false)
                        onCloseClicked.invoke()
                    }
                },
                isEnabled = selectedBankIndexState.value != -1
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun BankNotRespondingBottomSheetContent(
    onBankDowntimeCloseClicked: () -> Unit = {},
    bankData: RealtimeBankData,
    onNotifyButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_close),
            contentDescription = "",
            modifier = Modifier
                .clickable {
                    onBankDowntimeCloseClicked.invoke()
                }
                .align(Alignment.End),
            colorFilter = ColorFilter.tint(Color.White)
        )

        Box(
            modifier = Modifier
                .padding(top = 62.dp)
        ) {
            GlideImage(
                model = bankData.url,
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 8.dp)
                    .size(56.dp)
                    .clip(CircleShape),
                contentDescription = ""
            )

            Image(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.feature_lending_ic_error_i),
                contentDescription = ""
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = bankData.bankName.orEmpty(),
            fontSize = 16.sp,
            fontFamily = jarInterFontFamily,
            color = Color.White
        )

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_your_selected_bank_is_not_responding_at_this_moment),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = com.jar.app.core_ui.R.color.white)
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_we_will_notify_you_once_it_is_working_again),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontFamily = jarInterFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.white)
        )

        JarSecondaryButton(
            modifier = Modifier
                .padding(top = 64.dp, bottom = 24.dp)
                .defaultMinSize(minWidth = 196.dp),
            isAllCaps = false,
            text = stringResource(com.jar.app.core_ui.R.string.core_ui_okay),
            onClick = {
                onNotifyButtonClick.invoke()
                onBankDowntimeCloseClicked.invoke()
            },
            iconPadding = 0.dp
        )
    }
}

@Composable
private fun NoResultFoundBottomSheetContent(
    bankName: String = "",
    onBankNotListedButtonClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.padding(top = 46.dp)) {
            Image(
                modifier = Modifier
                    .padding(end = 8.dp, bottom = 12.dp)
                    .size(48.dp),
                painter = painterResource(id = R.drawable.feature_lending_ic_bank),
                contentDescription = ""
            )

            Image(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_cross_filled_red),
                contentDescription = "",
            )
        }

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_no_results_found_for).plus(" \"$bankName\""),
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            fontFamily = jarInterFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_make_sure_you_type_the_correct_bank_name),
            fontSize = 14.sp,
            fontWeight = FontWeight(700),
            fontFamily = jarInterFontFamily,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        JarSecondaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            isAllCaps = false,
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_my_bank_is_not_listed),
            onClick = onBankNotListedButtonClicked,
            iconPadding = 0.dp
        )
    }
}

enum class BottomSheetType {
    TYPE_BANK_LIST, TYPE_BANK_DOWNTIME
}