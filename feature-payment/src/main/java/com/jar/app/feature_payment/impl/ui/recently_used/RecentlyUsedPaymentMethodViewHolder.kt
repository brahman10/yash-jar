package com.jar.app.feature_payment.impl.ui.recently_used

import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.CellRecentlyUsedPaymentMethodBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.*

class RecentlyUsedPaymentMethodViewHolder(
    private val binding: CellRecentlyUsedPaymentMethodBinding,
    private val onCardClick: (position: Int, paymentMethod: PaymentMethod) -> Unit,
    private val onPayClick: (paymentMethod: PaymentMethod) -> Unit,
) :
    BaseViewHolder(binding.root) {

    init {
        binding.btnPay.setDebounceClickListener {
            paymentMethod?.let(onPayClick)
        }

        binding.root.setOnClickListener {
            paymentMethod?.let { it1 -> onCardClick.invoke(bindingAdapterPosition, it1) }
        }
    }

    private var paymentMethod: PaymentMethod? = null

    fun setPaymentMethod(paymentMethod: PaymentMethod) {
        this.paymentMethod = paymentMethod
        when (paymentMethod) {
            is PaymentMethodCard -> {
                if (paymentMethod.savedCard != null) {
                    binding.tvPaymentType.text = paymentMethod.savedCard!!.getFormattedCardNumber()

                    Glide.with(itemView)
                        .load(paymentMethod.savedCard!!.getCardBrandImageUrl())
                        .into(binding.ivIcon)
                }
                binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
            }
            is PaymentMethodNB -> {
//                Glide.with(itemView).load(paymentMethod.cardBrand)
                binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
            }
            is PaymentMethodUpiCollect -> {
                Glide.with(itemView).load(R.drawable.feature_payment_ic_upi).into(binding.ivIcon)
                binding.tvPaymentType.text = paymentMethod.payerVpa
                binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
            }
            is PaymentMethodUpiIntent -> {
                val packageManager = context.applicationContext.packageManager
                val icon =
                    packageManager.getApplicationIcon(paymentMethod.payerApp)
                Glide.with(itemView).load(icon).into(binding.ivIcon)
                binding.tvPaymentType.text = packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(
                        paymentMethod.payerApp,
                        PackageManager.GET_META_DATA
                    )
                )
                binding.ivSelected.setImageResource(com.jar.app.core_ui.R.drawable.ic_right_chevron_black_60)
            }
        }
    }

    fun toggle() {
        binding.expandableLayout.toggle(true)
        if (binding.expandableLayout.isExpanded)
            binding.ivSelected.setImageResource(R.drawable.feature_payment_ic_tick_green)
        else
            binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
    }

    fun collapse() {
        binding.expandableLayout.collapse(true)
        binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
    }
}