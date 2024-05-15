package com.jar.app.feature_mandate_payment.impl.ui.payment_page

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.CouponCodeResponseForMandateScreenItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.DescriptionPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiCollectPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.MandateEducationPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SeparatorPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SpacePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.TitlePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiAppPaymentPageItem

internal class PaymentPageAdapter(delegates: List<AdapterDelegate<List<BasePaymentPageItem>>>) :
    AsyncListDifferDelegationAdapter<BasePaymentPageItem>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<BasePaymentPageItem>() {
            override fun areItemsTheSame(
                oldItem: BasePaymentPageItem,
                newItem: BasePaymentPageItem
            ): Boolean {
                return if (oldItem is TitlePaymentPageItem && newItem is TitlePaymentPageItem)
                    (oldItem.title == newItem.title) && (oldItem.titleString == newItem.titleString)
                else if (oldItem is DescriptionPaymentPageItem && newItem is DescriptionPaymentPageItem)
                    oldItem.description == newItem.description
                else if (oldItem is MandateEducationPageItem && newItem is MandateEducationPageItem)
                    oldItem.mandateEducationList == newItem.mandateEducationList
                else if (oldItem is SeparatorPaymentPageItem && newItem is SeparatorPaymentPageItem)
                    oldItem == newItem
                else if (oldItem is SpacePaymentPageItem && newItem is SpacePaymentPageItem)
                    oldItem == newItem
                else if (oldItem is UpiAppPaymentPageItem && newItem is UpiAppPaymentPageItem)
                    oldItem.upiAppPackageName == newItem.upiAppPackageName
                else if (oldItem is UpiCollectPaymentPageItem && newItem is UpiCollectPaymentPageItem)
                    oldItem.errorMessage == newItem.errorMessage
                else if(oldItem is CouponCodeResponseForMandateScreenItem && newItem is CouponCodeResponseForMandateScreenItem)
                    oldItem.couponCode.couponCodeId == newItem.couponCode.couponCodeId
                else false
            }

            override fun areContentsTheSame(
                oldItem: BasePaymentPageItem,
                newItem: BasePaymentPageItem
            ): Boolean {
                return if (oldItem is TitlePaymentPageItem && newItem is TitlePaymentPageItem)
                    oldItem == newItem
                else if (oldItem is DescriptionPaymentPageItem && newItem is DescriptionPaymentPageItem)
                    oldItem == newItem
                else if (oldItem is MandateEducationPageItem && newItem is MandateEducationPageItem)
                    oldItem == newItem
                else if (oldItem is SeparatorPaymentPageItem && newItem is SeparatorPaymentPageItem)
                    oldItem == newItem
                else if (oldItem is SpacePaymentPageItem && newItem is SpacePaymentPageItem)
                    oldItem == newItem
                else if (oldItem is UpiAppPaymentPageItem && newItem is UpiAppPaymentPageItem)
                    oldItem == newItem
                else if (oldItem is UpiCollectPaymentPageItem && newItem is UpiCollectPaymentPageItem)
                    oldItem == newItem
                else if(oldItem is CouponCodeResponseForMandateScreenItem && newItem is CouponCodeResponseForMandateScreenItem)
                    oldItem == newItem
                else false
            }
        }
    }

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
    }
}