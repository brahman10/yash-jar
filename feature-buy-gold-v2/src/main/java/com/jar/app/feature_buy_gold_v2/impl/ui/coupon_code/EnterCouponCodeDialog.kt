package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.textChanges
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.DialogEnterCouponCodeBinding
import com.jar.app.feature_coupon_api.domain.event.CouponCodeEnteredEvent
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterCouponCodeDialog : BaseDialogFragment<DialogEnterCouponCodeBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var couponCode: String? = null
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogEnterCouponCodeBinding
        get() = DialogEnterCouponCodeBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        toggleMainButton()
        binding.etCouponCode.filters = arrayOf(InputFilter.AllCaps())

        binding.etCouponCode.doAfterTextChanged {
            couponCode = it?.toString()
            if (!couponCode.isNullOrBlank() && couponCode!!.length <= 3) {
                binding.tvErrorMessage.isVisible = true
                binding.etCouponCode.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_eb6a6e_8dp
                )
                binding.btnApply.setDisabled(isDisabled = true)
            } else {
                binding.tvErrorMessage.isVisible = false
                binding.etCouponCode.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_buy_gold_v2_bg_rounded_2e2942_outline_789bde_8dp
                )
                binding.btnApply.setDisabled(isDisabled = false)
            }
        }
    }

    private fun setupListener() {
        binding.ivClose.setDebounceClickListener {
            analyticsHandler.postEvent(
                BuyGoldV2EventKey.BuyNow_DiffCouponBSCancelClicked,
                mapOf(
                    BuyGoldV2EventKey.isPopupTyped to if (binding.etCouponCode.text.isNullOrEmpty()) BuyGoldV2EventKey.Buy_Gold_NO else BuyGoldV2EventKey.Buy_Gold_YES
                )
            )
            dismiss()
        }

        binding.btnApply.setDebounceClickListener {
            if (!couponCode.isNullOrBlank() && couponCode!!.length > 3) {
                binding.tvErrorMessage.isVisible = false
                EventBus.getDefault().postSticky(
                    CouponCodeEnteredEvent(
                        couponCode!!
                    )
                )
                dismiss()
            } else {
                binding.tvErrorMessage.isVisible = true
            }
        }

        binding.etCouponCode.textChanges()
            .onEach {
                toggleMainButton()
            }.launchIn(uiScope)
    }

    private fun toggleMainButton() {
        val text = binding.etCouponCode.text?.toString()
        binding.btnApply.setDisabled(isDisabled = text.isNullOrEmpty())
    }
}