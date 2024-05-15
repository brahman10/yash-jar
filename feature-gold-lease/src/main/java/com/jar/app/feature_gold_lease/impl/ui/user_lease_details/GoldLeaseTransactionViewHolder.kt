package com.jar.app.feature_gold_lease.impl.ui.user_lease_details

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.FeatureGoldLeaseCellTransactionWinningBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

internal class GoldLeaseTransactionViewHolder(
    val binding: FeatureGoldLeaseCellTransactionWinningBinding,
    private val onTransactionClicked: (goldLeaseTransaction: GoldLeaseTransaction) -> Unit
) : BaseViewHolder(binding.root) {
    private var goldLeaseTransaction: GoldLeaseTransaction? = null

    init {
        binding.root.setDebounceClickListener {
            goldLeaseTransaction?.let(onTransactionClicked)
        }
    }

    fun bind(data: GoldLeaseTransaction) {
        goldLeaseTransaction = data

        binding.tvQuantity.isVisible = false
        binding.tvTitle.text = data.title
        binding.tvAmount.text = context.getString(
            com.jar.app.feature_gold_lease.R.string.feature_gold_lease_x_gm_round_to_4,
            data.volume
        )
        Glide.with(itemView.context)
            .load(data.iconLink)
            .into((binding.ivTransaction))
        val formatter = DateTimeFormatter.ofPattern("dd MMM'' yy | hh:mm a")
        val localDate =
            Instant.ofEpochMilli(data.date ?: 0)
                .atZone(ZoneId.systemDefault())
        binding.tvDate.text = formatter.format(localDate)
        binding.tvTransactionStatus.text = data.currentStatus
        binding.tvTransactionStatus.setTextColor(data.getColorForStatus().getColor(context))
    }
}