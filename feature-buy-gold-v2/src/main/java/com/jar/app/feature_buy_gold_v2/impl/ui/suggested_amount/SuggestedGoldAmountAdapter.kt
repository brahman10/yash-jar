package com.jar.app.feature_buy_gold_v2.impl.ui.suggested_amount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.CellSuggestedBuyGoldAmountBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_user_api.domain.model.SuggestedAmount

class SuggestedGoldAmountAdapter(
    private val onSuggestedAmountClicked: (suggestedAmount: SuggestedAmount) -> Unit
): ListAdapter<SuggestedAmount, SuggestedGoldAmountAdapter.SuggestedGoldAmountViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<SuggestedAmount>(){
            override fun areItemsTheSame(
                oldItem: SuggestedAmount,
                newItem: SuggestedAmount
            ): Boolean {
                return oldItem.amount == newItem.amount
            }

            override fun areContentsTheSame(
                oldItem: SuggestedAmount,
                newItem: SuggestedAmount
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


    private var highlightNumber: Float? = null
    private var lastHighlightedPosition: Int? = null

    fun setHighlightNumber(number: Float?) {
        val oldHighlightedPosition = lastHighlightedPosition
        this.highlightNumber = number
        val newHighlightedPosition = currentList.indexOfFirst {
            it.amount == number
        }
        notifyDataSetChanged()
        /*if (oldHighlightedPosition != null && oldHighlightedPosition != -1) {
            notifyItemChanged(oldHighlightedPosition)
        }
        if (newHighlightedPosition != -1) {
            notifyItemChanged(newHighlightedPosition)
        }*/

        lastHighlightedPosition = newHighlightedPosition
    }

    inner class SuggestedGoldAmountViewHolder(
        private val binding: CellSuggestedBuyGoldAmountBinding,
        private val onSuggestedAmountClicked: (suggestedAmount: SuggestedAmount) -> Unit
    ) : BaseViewHolder(binding.root) {
        private var suggestedAmount: SuggestedAmount? = null

        init {
            binding.root.setDebounceClickListener {
                suggestedAmount?.let {
                    onSuggestedAmountClicked.invoke(it)
                }
            }
        }

        fun bind(data: SuggestedAmount) {
            this.suggestedAmount = data
            if (data.unit != null && data.unit!!.contains(BuyGoldV2Constants.UNIT_GM))
                binding.tvSuggestedAmount.text = "${data.amount} ${data.unit}"
            else
                binding.tvSuggestedAmount.text =
                    getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_rupees_x_int, data.amount.toInt()
                    )

            binding.tvPopularTag.isInvisible = data.isBestTag.orFalse().not()
            binding.tvSuggestedAmount.setBackgroundResource(
                if (data.isBestTag.orFalse()) R.drawable.feature_buy_gold_v2_bg_suggested_amount_upper_rounded else R.drawable.feature_buy_gold_v2_bg_suggested_amount
            )
            if (data.amount == highlightNumber) {
                binding.tvSuggestedAmount.setBackgroundResource(
                    if (data.isBestTag.orFalse()) R.drawable.feature_buy_gold_v2_bg_suggested_amount_upper_rounded_blue_outline else R.drawable.feature_buy_gold_v2_bg_suggested_amount_blue_outline)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestedGoldAmountViewHolder {
        val binding = CellSuggestedBuyGoldAmountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestedGoldAmountViewHolder(binding, onSuggestedAmountClicked)
    }

    override fun onBindViewHolder(holder: SuggestedGoldAmountViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}