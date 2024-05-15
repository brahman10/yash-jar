package com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.base.util.isValidUpiAddress
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageUpiCollectBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiCollectPaymentPageItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.cachapa.expandablelayout.ExpandableLayout

internal class UpiCollectAdapterDelegate(
    private val uiScope: CoroutineScope,
    private val onFocus: (pos: Int, hasFocus: Boolean) -> Unit,
    private val onExpansionListener: (pos: Int, state: Int) -> Unit,
    private val onItemSelected: (upiCollectPaymentPageItem: UpiCollectPaymentPageItem) -> Unit,
    private val onVerifyAndPayClick: (upiAddress: String) -> Unit
) : AdapterDelegate<List<BasePaymentPageItem>>() {

    private var hasShownErrorOnce = false

    override fun isForViewType(items: List<BasePaymentPageItem>, position: Int): Boolean {
        return items[position] is UpiCollectPaymentPageItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FeatureMandatePaymentCellPaymentPageUpiCollectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpiCollectViewHolder(
            binding,
            uiScope,
            onFocus,
            onExpansionListener,
            onItemSelected,
            onVerifyAndPayClick
        )
    }

    override fun onBindViewHolder(
        items: List<BasePaymentPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as UpiCollectViewHolder).setUpiCollect(items[position] as UpiCollectPaymentPageItem)
    }

    inner class UpiCollectViewHolder(
        private val binding: FeatureMandatePaymentCellPaymentPageUpiCollectBinding,
        uiScope: CoroutineScope,
        private val onFocus: (pos: Int, hasFocus: Boolean) -> Unit,
        private val onExpansionListener: (pos: Int, state: Int) -> Unit,
        private val onItemSelected: (upiCollectPaymentPageItem: UpiCollectPaymentPageItem) -> Unit,
        private val onVerifyAndPayClick: (upiAddress: String) -> Unit
    ) :
        BaseViewHolder(binding.root) {

        private var upiCollectPaymentPageItem: UpiCollectPaymentPageItem? = null

        init {
            binding.expandableLayout.setOnExpansionUpdateListener { _, state ->
                onExpansionListener.invoke(bindingAdapterPosition, state)
                when (state) {
                    ExpandableLayout.State.EXPANDING -> {

                    }

                    ExpandableLayout.State.EXPANDED -> {
                        binding.ivExpand.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down)
                    }

                    ExpandableLayout.State.COLLAPSING -> {

                    }

                    ExpandableLayout.State.COLLAPSED -> {
                        binding.ivExpand.setImageResource(com.jar.app.core_ui.R.drawable.ic_add)
                    }
                }
            }

            binding.etUpiAddress.textChanges()
                .debounce(500)
                .onEach {
                    updateErrorState(it.isValidUpiAddress().not())
                }
                .launchIn(uiScope)

            binding.etUpiAddress.setOnFocusChangeListener { _, hasFocus ->
                onFocus.invoke(bindingAdapterPosition, hasFocus)
            }

            binding.clTopView.setOnClickListener {
                binding.ivExpand.animate()
                    .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
                if (upiCollectPaymentPageItem != null) {
                    onItemSelected.invoke(upiCollectPaymentPageItem!!)
                }
            }

            binding.btnVerifyAndProceed.setDebounceClickListener {
                val upiAddress = binding.etUpiAddress.text
                if (upiAddress.isNullOrBlank().not() && upiAddress.isValidUpiAddress()) {
                    onVerifyAndPayClick.invoke(upiAddress?.toString()!!)
                }
            }
        }

        fun setUpiCollect(upiCollectPaymentPageItem: UpiCollectPaymentPageItem) {
            this.upiCollectPaymentPageItem = upiCollectPaymentPageItem
            Glide.with(itemView).load(upiCollectPaymentPageItem.upiAppsUrl).into(binding.ivUpiApps)
            updateSelectedState(upiCollectPaymentPageItem.isSelected)
            if (hasShownErrorOnce)
                updateErrorState(upiCollectPaymentPageItem.errorMessage.isNullOrBlank().not())
        }

        private fun updateSelectedState(isSelected: Boolean) {
            binding.expandableLayout.setExpanded(isSelected, true)
        }

        private fun updateErrorState(isInErrorState: Boolean) {
            binding.tvError.visibility = if (isInErrorState) View.VISIBLE else View.INVISIBLE
            binding.btnVerifyAndProceed.setDisabled(isInErrorState)
            hasShownErrorOnce = true
        }
    }
}