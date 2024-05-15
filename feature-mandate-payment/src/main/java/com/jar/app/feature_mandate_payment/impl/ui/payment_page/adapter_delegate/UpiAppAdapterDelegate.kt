package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.R
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageUpiAppBinding
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiAppPaymentPageItem

internal class UpiAppAdapterDelegate(
    private val onItemSelected: (pos: Int, upiApp: UpiApp, upiAppPaymentPageItem: UpiAppPaymentPageItem) -> Unit,
    private val onPayClick: (upiApp: UpiApp) -> Unit,
) : AdapterDelegate<List<BasePaymentPageItem>>() {

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is UpiAppPaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageUpiAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpiAppViewHolder(binding, onItemSelected)
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as UpiAppViewHolder).setUpiApp(items[position] as UpiAppPaymentPageItem)
    }

    inner class UpiAppViewHolder(
        private val binding: FeatureMandatePaymentCellPaymentPageUpiAppBinding,
        onItemSelected: (pos: Int, upiApp: UpiApp, upiAppPaymentPageItem: UpiAppPaymentPageItem) -> Unit
    ) :
        BaseViewHolder(binding.root) {

        private val packageManager = context.packageManager

        private var upiAppItem: UpiAppPaymentPageItem? = null

        private var upiApp: UpiApp? = null

        init {
            binding.clTopView.setOnClickListener {
                if (upiApp != null && upiAppItem != null) {
                    onItemSelected.invoke(bindingAdapterPosition, upiApp!!, upiAppItem!!)
                }
            }

            binding.btnProceed.setDebounceClickListener {
                if (upiApp != null) {
                    onPayClick.invoke(upiApp!!)
                }
            }
        }

        fun setUpiApp(upiAppItem: UpiAppPaymentPageItem) {
            this.upiAppItem = upiAppItem
            this.upiApp = getUpiAppFromPackageName(
                upiAppItem.upiAppPackageName,
                packageManager
            )
            binding.tvAppName.text = upiApp?.appName
            Glide.with(itemView).load(upiApp?.icon).into(binding.ivAppIcon)
            updateSelectedState(upiAppItem.isSelected)
        }

        private fun updateSelectedState(isSelected: Boolean) {
            val selectedStateRes =
                if (isSelected)
                    R.drawable.feature_mandate_payment_ic_radio_button_selected
                else
                    R.drawable.feature_mandate_payment_ic_radio_button_unselected

            binding.ivSelectedState.setImageResource(selectedStateRes)

            binding.expandableLayout.setExpanded(isSelected, true)
        }
    }

    private fun getUpiAppFromPackageName(
        packageName: String,
        packageManager: PackageManager
    ): UpiApp {
        return UpiApp(
            packageName = packageName,
            icon = packageManager.getApplicationIcon(packageName),
            appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
        )
    }
}