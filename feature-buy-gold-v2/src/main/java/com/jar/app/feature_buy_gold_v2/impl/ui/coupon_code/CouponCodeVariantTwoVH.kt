package com.jar.app.feature_buy_gold_v2.impl.ui.coupon_code

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.CellCouponCodeVariant2Binding
import com.jar.app.feature_buy_gold_v2.shared.util.ScreenName
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import kotlinx.coroutines.CoroutineScope

internal class CouponCodeVariantTwoVH(
    private val binding: CellCouponCodeVariant2Binding,
    private val uiScope: CoroutineScope,
    private val onApplyClick: (couponCode: CouponCode, position: Int, screenName: String) -> Unit,
    private val onRemoveCouponClick: (couponCode: CouponCode, position: Int) -> Unit,
    private val onCouponExpired: (couponCode: CouponCode) -> Unit,
    private val getCurrentAmount: () -> Float
) : BaseViewHolder(binding.root) {

    private var couponCode: CouponCode? = null

    init {
        binding.root.setDebounceClickListener {
            couponCode?.let {
                if (it.getCouponState() == CouponState.ACTIVE && it.isCouponAmountEligible) {
                    onApplyClick.invoke(
                        it,
                        bindingAdapterPosition,
                        ScreenName.Buy_Gold_Coupons_Screen.name
                    )
                }
            }
        }
    }

    fun bind(couponCode: CouponCode) {
        CouponCodeVariantTwoBinder(
            binding, context, uiScope,
            onApplyClick = { selectedCouponCode, position, screenName ->
                if (selectedCouponCode.getCouponState() == CouponState.ACTIVE && selectedCouponCode.isCouponAmountEligible) {
                    onApplyClick(selectedCouponCode, position, screenName)
                }
            },
            onCouponExpired = { expiredCoupon ->
                onCouponExpired(couponCode)
            },
            getCurrentAmount = {
                getCurrentAmount()
            },
            screenName = ScreenName.Buy_Gold_Coupons_Screen.name
        ).bind(couponCode)
    }
}