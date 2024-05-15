package com.jar.app.feature_sell_gold.impl.ui.bottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.shared.CoreBaseMR
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.Withdrawal_ZeroBalanceDS_Clicked
import com.jar.app.feature_kyc.shared.util.KycConstants.AnalyticsKeys.Withdrawal_ZeroBalanceDS_Popup
import com.jar.app.feature_sell_gold.R
import com.jar.app.feature_sell_gold.databinding.FeatureSellGoldBottomsheetBinding
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawHelpData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawalLimitBottomSheet
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.base.util.dp as intDp

@OptIn(ExperimentalGlideComposeApi::class)
@AndroidEntryPoint
internal class WithdrawBottomSheet :
    BaseBottomSheetDialogFragment<FeatureSellGoldBottomsheetBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var adapter: WithdrawBottomSheetAdapter? = null

    private val viewModelProvider by viewModels<WithdrawLimitBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { viewModelProvider.getInstance() }

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    private var withdrawHelpDataResponse: WithdrawHelpData? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureSellGoldBottomsheetBinding
        get() = FeatureSellGoldBottomsheetBinding::inflate

    private val spaceItemDecoration = SpaceItemDecoration(0.intDp, 6.intDp)

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        observeLiveData()
        setupListeners()
    }

    fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.withdrawBottomSheetFlow.collect(
                    onLoading = {
                        binding.shimmerPlaceholder.isVisible = true
                        binding.clContainer.isVisible = false
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = { withdrawHelpData ->
                        withdrawHelpDataResponse = withdrawHelpData
                        binding.clContainer.isVisible =
                            (withdrawHelpData?.quickActionWithdraw?.withdrawalLimitBottomSheet == null)
                        binding.shimmerPlaceholder.isVisible = false
                        binding.shimmerPlaceholder.stopShimmer()
                        withdrawHelpData?.quickActionWithdraw?.withdrawalLimitBottomSheet
                            ?.let(::setWithdrawLimitData)
                            ?: run { setData(withdrawHelpData) }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(weakReference.get()!!)
                        binding.shimmerPlaceholder.isVisible = false
                        binding.clContainer.isVisible = true
                        binding.shimmerPlaceholder.stopShimmer()
                    }
                )
            }
        }
    }

    private fun setWithdrawLimitData(data: WithdrawalLimitBottomSheet) {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LaunchedEffect(Unit) {
                    viewModel.postWithdrawLimitBottomSheetShownEvent()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = CoreBaseMR.colors.color_2E2942.resourceId),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            dialog?.cancel()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                            .align(Alignment.End)
                    ) {
                        Image(
                            painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_cross_outline),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = data.title.orEmpty(),
                        style = JarTypography.h5,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = data.description.orEmpty().generateAnnotatedFromHtmlString(),
                        style = JarTypography.body2,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        JarImage(
                            imageUrl = data.iconLink.orEmpty(),
                            contentDescription = null,
                            modifier = Modifier.size(160.dp)
                        )
                        Text(
                            text = data.timerText.orEmpty().generateAnnotatedFromHtmlString(),
                            style = JarTypography.h2,
                            modifier = Modifier
                                .padding(bottom = 22.dp)
                                .align(Alignment.BottomCenter),
                            textAlign = TextAlign.Center
                        )
                    }
                    JarSecondaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 36.dp),
                        text = data.buttonText.orEmpty(),
                        onClick = {
                            viewModel.postViewWithdrawalHistoryButtonClickedEvent()
                            dialog?.cancel()
                            EventBus.getDefault().post(
                                GoToHomeEvent(
                                    "TransactionFragment",
                                    BaseConstants.HomeBottomNavigationScreen.TRANSACTION
                                )
                            )
                        },
                        isAllCaps = false
                    )
                }
            }
        }

    }

    private fun setData(data: WithdrawHelpData?) {
        data ?: return

        analyticsHandler.postEvent(Withdrawal_ZeroBalanceDS_Popup)

        binding.tvHeading.text = data.quickActionWithdraw.title

        binding.btnStartSaving.setText(data.quickActionWithdraw.footerButtonText.orEmpty())

        binding.overlappingView.submitProfilePics(data.quickActionWithdraw.profilePics.orEmpty())

        binding.tvOverlappingView.text = data.quickActionWithdraw.footerText

        binding.ivEmptyLocker.isVisible = data.quickActionWithdraw.imageUrl.isNullOrEmpty().not()
        binding.rvNeverUsedList.isVisible = data.quickActionWithdraw.stepsList.isNullOrEmpty().not()

        if (data.quickActionWithdraw.stepsList.isNullOrEmpty().not()) {
            binding.root.background = ContextCompat.getDrawable(
                requireContext(),
                com.jar.app.core_ui.R.drawable.core_ui_bg_top_rounded_corner
            )
            adapter = WithdrawBottomSheetAdapter()
            binding.rvNeverUsedList.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNeverUsedList.addItemDecorationIfNoneAdded(spaceItemDecoration)
            binding.rvNeverUsedList.adapter = adapter
            adapter?.submitList(data.quickActionWithdraw.stepsList)
        } else if (data.quickActionWithdraw.imageUrl.isNullOrEmpty().not()) {
            binding.root.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.featue_sell_gold_bg_empty_locker
            )
            Glide.with(this)
                .load(data.quickActionWithdraw.imageUrl)
                .into(binding.ivEmptyLocker)
        }
    }

    fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.ClickedWithdraw_BSClicked,
                mapOf(EventKey.Button_Type to EventKey.Close)
            )
            dismissAllowingStateLoss()
        }

        binding.btnStartSaving.setDebounceClickListener {
            analyticsHandler.postEvent(
                Withdrawal_ZeroBalanceDS_Clicked,
                mapOf(EventKey.Button_Type to binding.btnStartSaving.getText())
            )
            withdrawHelpDataResponse?.let {
                EventBus.getDefault().post(
                    HandleDeepLinkEvent(
                        deepLink = it.quickActionWithdraw.deepLink.orEmpty(),
                        fromScreen = SellGoldEvent.WithdrawalZeroBalanceDSPopup
                    )
                )
            }
        }
    }
}