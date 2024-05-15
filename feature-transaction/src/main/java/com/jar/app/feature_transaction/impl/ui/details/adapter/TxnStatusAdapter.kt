package com.jar.app.feature_transaction.impl.ui.details.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionCardTransactionStatusBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnDetailsCardView
import com.jar.app.feature_transactions_common.shared.CommonTransactionValueType

class TxnStatusAdapter : AdapterDelegate<List<TxnDetailsCardView>>() {

    override fun isForViewType(items: List<TxnDetailsCardView>, position: Int): Boolean {
        return items[position] is com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            FeatureTransactionCardTransactionStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TxnStatusVH(binding)
    }

    override fun onBindViewHolder(
        items: List<TxnDetailsCardView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData && holder is TxnStatusVH) {
            holder.bindData(item)
        }
    }

    inner class TxnStatusVH(
        private val binding: FeatureTransactionCardTransactionStatusBinding,
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: com.jar.app.feature_transaction.shared.domain.model.CommonTxnWinningData) {
            Glide.with(context)
                .load(data.iconLink)
                .into(binding.ivTransaction)

            binding.tvTitle.text = data.title
            binding.clStatusContainer.isVisible = data.statusInfo != null
            data.statusInfo?.let {
                binding.clStatusContainer.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(it.bgColor))
                binding.tvStatus.setTextColor(ColorStateList.valueOf(Color.parseColor(it.textColor)))
                binding.tvStatus.text = it.statusTxt
                binding.ivStatusIcon.isVisible = it.iconUrl.isNullOrEmpty().not()
                it.iconUrl?.let {
                    Glide.with(binding.root.context).load(it).into(binding.ivStatusIcon)
                }
            }
            val value = when (data.getValueType()) {
                CommonTransactionValueType.AMOUNT -> {
                    itemView.context.getString(
                        R.string.feature_transaction_rs_value,
                        data.amount.orZero()
                    )
                }
                CommonTransactionValueType.VOLUME -> {
                    getCustomStringFormatted(
                        itemView.context,
                        MR.strings.feature_buy_gold_v2_x_gm,
                        data.volume.orZero()
                    )
                }
                CommonTransactionValueType.AMOUNT_AND_VOLUME -> {
                    itemView.context.getString(
                        R.string.feature_transaction_f_amount_and_f_volume,
                        data.amount.orZero(), data.volume.orZero()
                    )
                }
            }

            binding.tvAmount.text = value
            binding.tvQuantity.text = data.date
        }
    }
}





