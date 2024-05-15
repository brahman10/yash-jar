package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.dp
import com.jar.app.base.util.isValidUpiAddress
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.CellUpiCollectPaymetMethodSectionBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiCollectPaymentSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class UpiCollectPaymentMethodSectionAdapterDelegate(
    private val uiScope: CoroutineScope,
    private val onFocus: (position: Int) -> Unit,
    private val onVerifyClick: (upiAddress: String) -> Unit
) :
    AdapterDelegate<List<PaymentSection>>() {

    override fun isForViewType(items: List<PaymentSection>, position: Int): Boolean {
        return items[position] is UpiCollectPaymentSection
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = CellUpiCollectPaymetMethodSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpiCollectPaymentMethodSectionViewHolder(binding, uiScope)
    }

    override fun onBindViewHolder(
        items: List<PaymentSection>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is UpiCollectPaymentSection && holder is UpiCollectPaymentMethodSectionViewHolder)
            holder.setSection(item)
    }

    inner class UpiCollectPaymentMethodSectionViewHolder(
        private val binding: CellUpiCollectPaymetMethodSectionBinding,
        private val uiScope: CoroutineScope
    ) : BaseViewHolder(binding.root) {

        fun setSection(upiCollectPaymentSection: UpiCollectPaymentSection) {
            Glide.with(itemView).load(upiCollectPaymentSection.appLogoUrl)
                .into(binding.ivUpiApps)

            binding.etUpiAddress.textChanges()
                .debounce(500)
                .onEach {
                    updateButtonVisibility(it)
                }
                .launchIn(uiScope)

            binding.etUpiAddress.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    onFocus.invoke(bindingAdapterPosition)
                    //Scroll To this item inn recyclerview..
                }
            }

            binding.btnVerifyAndPay.setDebounceClickListener {
                it.hideKeyboard()
                val upi = binding.etUpiAddress.text
                if (upi.isValidUpiAddress()) {
                    onVerifyClick.invoke(upi.toString())
                } else {
                    context.getString(com.jar.app.core_ui.R.string.feature_payment_please_enter_a_valid_upi_id)
                        .snackBar(
                            binding.root,
                            translationY = -4.dp.toFloat()
                        )
                }
            }

            binding.clTopView.setOnClickListener {
                binding.ivAdd.animate()
                    .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
                binding.expandableLayout.toggle()
            }

            if (upiCollectPaymentSection.errorMessage.isNullOrBlank())
                binding.tvError.visibility = View.INVISIBLE
            else
                binding.tvError.visibility = View.VISIBLE
        }

        private fun updateButtonVisibility(text: CharSequence?) {
            if (text.isValidUpiAddress()) {
                binding.btnVerifyAndPay.alpha = 1f
                binding.tvError.visibility = View.INVISIBLE
            } else {
                binding.btnVerifyAndPay.alpha = 0.25f
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}