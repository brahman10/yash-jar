package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePageCouponLayoutBinding
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.CouponViewHolder
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.CouponCodeResponseForMandateScreenItem
import kotlinx.coroutines.CoroutineScope

internal class CouponAdapterDelegate(
    private val uiScope: CoroutineScope
) : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is CouponCodeResponseForMandateScreenItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePageCouponLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponViewHolder(binding, uiScope)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as CouponViewHolder).checkAndCancelTimerJob()
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as CouponViewHolder).bind(items[position] as CouponCodeResponseForMandateScreenItem)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
    }
}