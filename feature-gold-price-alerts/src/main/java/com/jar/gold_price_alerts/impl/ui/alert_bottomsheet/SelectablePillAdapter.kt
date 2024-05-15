package com.jar.gold_price_alerts.impl.ui.alert_bottomsheet

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.applyRoundedRectBackground
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_price_alerts.R
import com.jar.app.feature_gold_price_alerts.databinding.FeatureGoldPriceAlertPillBinding
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendPricePill

class SelectablePillAdapter(
    private val onFilterClicked: (selectablePillData: GoldTrendPricePill) -> Unit
) : ListAdapter<GoldTrendPricePill, SelectablePillAdapter.SelectablePillViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GoldTrendPricePill>() {
            override fun areItemsTheSame(
                oldItem: GoldTrendPricePill,
                newItem: GoldTrendPricePill
            ): Boolean {
                return oldItem.price == newItem.price
            }

            override fun areContentsTheSame(
                oldItem: GoldTrendPricePill,
                newItem: GoldTrendPricePill
            ): Boolean {
                return oldItem == newItem
            }

        }

        const val DEFAULT_PILL_STROKE_COLOR = "#7745FF"
        const val DEFAULT_PILL_SELECTED_COLOR = "#4A4368"
    }

    inner class SelectablePillViewHolder(
        private val binding: FeatureGoldPriceAlertPillBinding,
        private val onFilterClicked: (selectablePillData: GoldTrendPricePill) -> Unit
    ) : BaseViewHolder(binding.root) {
        private var selectablePillData: GoldTrendPricePill? = null

        init {
            binding.root.setDebounceClickListener {
                selectablePillData?.let {
                    if (it.isSelected.not()) {
                        onFilterClicked.invoke(it)
                    }
                }
            }
        }

        fun bind(data: GoldTrendPricePill) {
            this.selectablePillData = data

            data.pillText.takeIf { it.isNullOrEmpty().not() }?.let {
                applyRoundedRectBackground(
                    targetView = binding.tvPillTag,
                    bgColor = Color.parseColor(data.pillColor ?: DEFAULT_PILL_STROKE_COLOR),
                    bottomLeftRadius = 8f.dp,
                    bottomRightRadius = 8f.dp
                )
                binding.tvPillTag.isVisible = true
                binding.tvPillTag.setHtmlText(data.pillText.orEmpty())
            } ?: kotlin.run {
                binding.tvPillTag.isVisible = false
            }

            binding.tvPrice.setTypeface(
                binding.tvPrice.typeface,
                if (data.isSelected) Typeface.BOLD else Typeface.NORMAL
            )
            binding.tvPrice.text = context.getString(R.string.feature_gold_price_alert_x_in_int, data.price.toInt())

            if (data.isSelected) {
                if (data.pillText.isNullOrEmpty().not()) {
                    applyRoundedRectBackground(
                        targetView = binding.tvPrice,
                        bgColor = Color.parseColor(DEFAULT_PILL_SELECTED_COLOR),
                        topRightRadius = 8f.dp,
                        topLeftRadius = 8f.dp,
                        strokeWidth = 1.dp,
                        strokeColor = Color.parseColor(data.pillColor ?: DEFAULT_PILL_STROKE_COLOR)
                    )
                } else {
                    applyRoundedRectBackground(
                        targetView = binding.tvPrice,
                        bgColor = Color.parseColor(DEFAULT_PILL_SELECTED_COLOR),
                        radius = 8f.dp,
                        strokeWidth = 1.dp,
                        strokeColor = Color.parseColor(data.pillColor ?: DEFAULT_PILL_STROKE_COLOR)
                    )
                }
            } else {
                binding.tvPrice.setBackgroundResource(
                    if (data.pillText.isNullOrEmpty().not()) com.jar.app.feature_gold_price_alerts.R.drawable.feature_gold_price_alert_bg_alert_pill_upper_rounded else com.jar.app.feature_gold_price_alerts.R.drawable.feature_gold_price_alert_bg_alert_pill
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectablePillViewHolder {
        val binding = FeatureGoldPriceAlertPillBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectablePillViewHolder(binding, onFilterClicked)
    }

    override fun onBindViewHolder(holder: SelectablePillViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}