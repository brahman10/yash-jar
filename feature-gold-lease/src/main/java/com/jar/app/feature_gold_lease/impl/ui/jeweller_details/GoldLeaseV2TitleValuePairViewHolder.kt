package com.jar.app.feature_gold_lease.impl.ui.jeweller_details

import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseTitleValuePairBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2TitleValuePair
import com.jar.app.feature_gold_lease.shared.domain.model.TitleValueCosmetics

internal class GoldLeaseV2TitleValuePairViewHolder(
    private val binding: CellGoldLeaseTitleValuePairBinding,
    private val onClickedCopyTransactionId : (goldLeaseV2TitleValuePair: GoldLeaseV2TitleValuePair) -> Unit,
    private val onWebsiteClicked : (link: String) -> Unit
) : BaseViewHolder(binding.root) {

    private var goldLeaseV2TitleValuePair: GoldLeaseV2TitleValuePair? = null

    init {
        binding.root.setDebounceClickListener {
            goldLeaseV2TitleValuePair?.let {
                if (it.getRowCosmetics() == TitleValueCosmetics.TXN_ID) {
                    onClickedCopyTransactionId.invoke(it)
                } else if (it.getRowCosmetics() == TitleValueCosmetics.WEBSITE) {
                    onWebsiteClicked.invoke(it.value.orEmpty())
                }
            }
        }
    }

    fun bind(data: GoldLeaseV2TitleValuePair) {
        this.goldLeaseV2TitleValuePair = data

        binding.tvTitle.setHtmlText(data.title.orEmpty())
        binding.tvValue.setHtmlText(data.value.orEmpty())

        when(data.getRowCosmetics()) {
            TitleValueCosmetics.HIGHLIGHTED -> {
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_58DDC8
                    )
                )
                binding.tvValue.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_58DDC8
                    )
                )
                binding.root.setBackgroundResource(R.drawable.feature_gold_lease_bg_rounded_8_3c3357)
                binding.root.setPadding(8.dp)
                binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            }
            TitleValueCosmetics.TXN_ID -> {
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                )
                binding.tvValue.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.white
                    )
                )
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.transparent
                    )
                )
                binding.root.setPadding(0.dp)
                binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0, com.jar.app.core_ui.R.drawable.ic_copy_small,0)
                binding.tvValue.text = data.maskTransactionId(data.value.orEmpty())
            }
            TitleValueCosmetics.WEBSITE -> {
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                )
                binding.tvValue.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.white
                    )
                )
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.transparent
                    )
                )
                binding.root.setPadding(0.dp)
                binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                binding.tvValue.setHtmlText("<u>${data.value.orEmpty()}</u>")
            }
            else -> {
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                )
                binding.tvValue.setTextColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.white
                    )
                )
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        context, com.jar.app.core_ui.R.color.transparent
                    )
                )
                binding.root.setPadding(0.dp)
                binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            }
        }
    }
}