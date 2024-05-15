package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentCouponListPageBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold.BuyGoldV2FragmentViewModelAndroid
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_buy_gold_v2.shared.util.ScreenName
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import com.jar.app.feature_coupon_api.util.CouponOrderUtil
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CouponListPageFragment : BaseBottomSheetDialogFragment<FragmentCouponListPageBinding>() {


    private val viewModelProvider by hiltNavGraphViewModels<BuyGoldV2FragmentViewModelAndroid>(R.id.buy_gold_v2_navigation)

    private var couponCodeAdapter: CouponCodeVariantTwoAdapter? = null

    @Inject
    lateinit var couponOrderUtil: CouponOrderUtil

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var couponListJob: Job? = null
    var couponCode: String? = null

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCouponListPageBinding
        get() = FragmentCouponListPageBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            shouldShowFullHeight = true,
            isCancellable = true,
            isDraggable = false
        )

    override fun setup() {
        setupUI()
        setupListeners()
        observeCouponCodesLiveData()
        observeApplyCouponFlow()
    }

    private fun observeApplyCouponFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.applyCouponCodeFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        it?.let { it1 ->
                            clearFocus()
                            dismissAllowingStateLoss()
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        binding.errorLayout.isVisible = true
                        binding.tvError.text = errorMessage
                        removeAllCouponCode()
                    },
                )
            }
        }
    }

    private fun setupListeners() {

    }

    private fun setupUI() {
        setupCouponCodeListAdapter()
        setupToolBar()
        setupManualCouponCodeView()
        setupCouponView()
        handleBackPress()
    }

    private fun setupManualCouponCodeView() {

        binding.etCouponCode.doAfterTextChanged {
            if (it != null) {
                binding.etCouponCode.typeface = if (it.isNotEmpty()) {
                    Typeface.DEFAULT_BOLD
                } else {
                    Typeface.DEFAULT
                }
            }
            couponCode = it.toString()
            if (!couponCode.isNullOrBlank() && couponCode!!.length <= 3) {
                binding.btnApply.apply {
                    isClickable = false
                    isEnabled = false
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.jar.app.core_ui.R.color.color_776e94
                        )
                    )
                }
            } else {
                binding.btnApply.apply {
                    isClickable = true
                    isEnabled = true
                    alpha = 1f
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.jar.app.core_ui.R.color.color_D5CDF2
                        )
                    )
                }
            }
        }


        binding.btnApply.setDebounceClickListener {
            if (!couponCode.isNullOrBlank() && couponCode!!.length > 3) {

                viewModel.applyManuallyEnteredCouponCode(
                    couponCode!!,
                    ScreenName.Buy_Gold_Coupons_Screen.name
                )
            } else {
                binding.errorLayout.isVisible = true
            }
        }
    }

    private fun setupCouponView() {
        viewModel.couponCodeResponse =
            viewModel.couponCodeResponse?.couponCodes?.sortedByDescending { it.isSelected }
                ?.let { viewModel.couponCodeResponse?.copy(couponCodes = it) }
        setupFirstCoupon()
        viewModel.couponCodeResponse?.couponCodes?.let { couponCodes ->
            analyticsHandler.postEvent(
                BuyGoldV2EventKey.BuyGoldScreen_AllCoupons_Shown,
                mapOf(
                    BuyGoldV2EventKey.Coupon_Title to couponCodes.joinToString { it.title.orEmpty() },
                )
            )

            couponCodeAdapter?.submitList(couponCodes.subList(1, couponCodes.size))
        }
    }

    private fun setupFirstCoupon() {
        viewModel.couponCodeResponse?.couponCodes?.first()
            ?.let {
                CouponCodeVariantTwoBinder(
                    binding.bestCoupon,
                    requireContext(),
                    uiScope,
                    onApplyClick = { couponCode, _, _ -> onCouponCodeClicked(couponCode) },
                    onCouponExpired = { expiredCoupon ->
                        viewModel.couponCodeResponse?.let {
                            it.couponCodes?.find { it.couponCode == expiredCoupon.couponCode }
                                ?.let {
                                    it.couponState =
                                        com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState.INACTIVE.name
                                    it.isCouponAmountEligible = false
                                    it.validityInMillis = -1L
                                }
                            if (expiredCoupon.isSelected) {
                                removeAllCouponCode()
                            } else {
                                sortCouponListAndSetAdapter(it)
                            }
                        }
                    },
                    getCurrentAmount = {
                        viewModel.buyAmount
                    },
                    screenName = ScreenName.Buy_Gold_Coupons_Screen.name
                ).bind(
                    it
                )
            }
    }

    private fun setupCouponCodeListAdapter() {

        binding.rvCouponList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        couponCodeAdapter = CouponCodeVariantTwoAdapter(
            uiScope = uiScope,
            onApplyClick = { couponCode, pos, screenName ->

                analyticsHandler.postEvent(
                    BuyGoldV2EventKey.ClickedApply_CouponTextbox_OrderPreviewScreen,
                    mapOf(
                        BuyGoldV2EventKey.POSITION to pos + 1,
                        BuyGoldV2EventKey.couponTitle to couponCode.title.orEmpty(),
                        BuyGoldV2EventKey.moneySavedByCoupon to couponCode.getMaxRewardThatCanBeAvailed(
                            viewModel.buyAmount
                        ).orZero(),
                        BuyGoldV2EventKey.couponDiscountPercentage to couponCode.rewardPercentage.orZero(),
                        BuyGoldV2EventKey.isWinningsCoupon to if (couponCode.getCouponType() == CouponType.WINNINGS) BuyGoldV2EventKey.Buy_Gold_YES else BuyGoldV2EventKey.Buy_Gold_NO,
                        BuyGoldV2EventKey.Amount to viewModel.buyAmount,
                        BuyGoldV2EventKey.CouponCode to couponCode.couponCode.orEmpty(),
                        BuyGoldV2EventKey.Screen to screenName,

                        )
                )
                clearFocus()
                onCouponCodeClicked(couponCode)
            },
            onRemoveCouponClick = { couponCode, pos ->
                clearFocus()
                onRemoveCouponClicked()
                showCouponRemovedSnackbar(couponCode)

            },
            onCouponExpired = { expiredCoupon ->
                viewModel.couponCodeResponse?.let {
                    it.couponCodes?.find { it.couponCode == expiredCoupon.couponCode }?.let {
                        it.couponState = CouponState.INACTIVE.name
                        it.isCouponAmountEligible = false
                        it.validityInMillis = -1L
                    }
                }
            },
            getCurrentAmount = {
                viewModel.buyAmount
            }
        )
        binding.rvCouponList.adapter = couponCodeAdapter

    }

    private fun clearFocus() {
        binding.etCouponCode.clearFocus()
    }

    private fun setupToolBar() {
        with(binding.toolbar) {
            lottieView.isVisible = false
            ivEndImage.isVisible = false
            ivTitleImage.isVisible = false
            ivTitleImage.isVisible = false
            tvTitle.text = "Offers"
            btnBack.setDebounceClickListener {
                dismissAllowingStateLoss()
            }

        }
    }

    private fun handleBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismissAllowingStateLoss()
                }
            })
    }

    private fun onCouponCodeClicked(couponCode: CouponCode) {
        // shouldSetCouponsWithoutLoading = true
        if (couponCode.isSelected) {
            removeAllCouponCode()
        } else {
            if (viewModel.canApplyCoupon(couponCode)) {
                viewModel.applyCouponCode(
                    couponCode,
                    ScreenName.Buy_Gold_Coupons_Screen.name
                )
                clearFocus()
                dismissAllowingStateLoss()
            } else {
                viewModel.getApplyCouponErrorMessage(
                    couponCode
                )?.let {
                    getCustomStringFormatted(
                        it,
                        couponCode.minimumAmount.toInt()
                    ).snackBar(
                        binding.root,
                        com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                        progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                        duration = 2000,
                        translationY = 0f
                    )
                }
            }
        }
    }

    private fun onRemoveCouponClicked() {
        removeAllCouponCode()
    }

    private fun removeAllCouponCode() {
        viewModel.deselectAllCoupons()
    }

    private fun showCouponRemovedSnackbar(couponCode: CouponCode) {
        val removeMessage =
            if (couponCode.getCouponType() == CouponType.WINNINGS) getCustomString(MR.strings.feature_buy_gold_v2_applied_winnings_was_removed) else getCustomString(
                MR.strings.feature_buy_gold_v2_applied_coupon_was_removed
            )
        removeMessage.snackBar(
            binding.root,
            com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
            progressColor = com.jar.app.core_ui.R.color.color_016AE1,
            duration = 2000,
            translationY = 0f
        )
    }

    private fun observeCouponCodesLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.couponCodesFlow.collectUnwrapped(
                    onSuccess = {
                        it?.let { it1 -> sortCouponListAndSetAdapter(it1) }
                    }
                )
            }
        }
    }

    private fun sortCouponListAndSetAdapter(couponCodeResponse: CouponCodeResponse) {
        couponListJob?.cancel()
        couponListJob = uiScope.launch {
            setupCouponView()
        }
    }

}