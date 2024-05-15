package com.jar.app.core_ui.label_and_value

import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CoreUiCellLabelAndValueBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder

class LabelAndValueViewHolder(private val binding: CoreUiCellLabelAndValueBinding) :
    BaseViewHolder(binding.root) {

    fun setLabelAndValues(labelAndValue: LabelAndValue, onValueIconCLick: ((x: Any) -> Unit)?) {
        binding.tvLabel.text = labelAndValue.label

        binding.tvValue.isVisible = labelAndValue.isTextualValue
        binding.ivValue.isVisible = labelAndValue.isTextualValue.not()
        Glide.with(binding.root.context).load(labelAndValue.value).into(binding.ivValue)
        binding.tvValue.text = labelAndValue.value
        if (labelAndValue.showCopyToClipBoardIcon) {
            binding.tvValue.setDebounceClickListener {
                onValueIconCLick?.invoke(binding.tvValue.text.toString())
            }
            val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_copy)
            binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            binding.tvValue.compoundDrawablePadding = 8.dp
        } else {
            binding.tvValue.setOnClickListener(null)
            binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        binding.root.setBackgroundColor(
            ContextCompat.getColor(binding.root.context, labelAndValue.backgroundColorRes)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tvLabel.setTextAppearance(labelAndValue.labelTextStyle)
            binding.tvValue.setTextAppearance(labelAndValue.valueTextStyle)
        } else {
            binding.tvLabel.setTextAppearance(binding.root.context, labelAndValue.labelTextStyle)
            binding.tvValue.setTextAppearance(binding.root.context, labelAndValue.valueTextStyle)
        }
        binding.tvLabel.setTextColor(
            ContextCompat.getColor(binding.root.context, labelAndValue.labelColorRes)
        )
        binding.tvValue.setTextColor(
            ContextCompat.getColor(binding.root.context, labelAndValue.valueColorRes)
        )
        labelAndValue.valueColorString?.let {
            binding.tvValue.setTextColor(Color.parseColor(it))
        }
    }
}