package com.jar.app.feature_lending.impl.ui.common

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.CellKeyValueBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.v2.KeyValueData

internal class KeyValueAdapter(
    private val areValuesAmount: Boolean = false,
    private val skipPadding: Boolean = false,
) : ListAdapter<KeyValueData, KeyValueAdapter.KeyValueViewHolder>(DIFF_UTIL) {

    @ColorRes
    private var keyColor: Int? = null

    @ColorRes
    private var valueColor: Int? = null

    private var fontSizeInSp: Float? = null

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<KeyValueData>() {
            override fun areItemsTheSame(oldItem: KeyValueData, newItem: KeyValueData): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: KeyValueData, newItem: KeyValueData): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun setColor(@ColorRes keyColor: Int, @ColorRes valueColor: Int) {
        this.keyColor = keyColor
        this.valueColor = valueColor
    }

    fun setFontSize(fontSizeInSp: Float) {
        this.fontSizeInSp = fontSizeInSp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = KeyValueViewHolder(
        CellKeyValueBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        areValuesAmount,
        skipPadding
    )

    override fun onBindViewHolder(holder: KeyValueViewHolder, position: Int) {
        getItem(position)?.let { holder.setData(it) }
    }

    inner class KeyValueViewHolder(
        private val binding: CellKeyValueBinding,
        private val areValuesAmount: Boolean,
        private val skipPadding: Boolean
    ) : BaseViewHolder(binding.root) {

        init {
            if (skipPadding)
                binding.root.setPadding(0, 0, 0, 0)
        }

        fun setData(keyValueData: KeyValueData) {
            keyColor?.let {
                binding.tvKey.setTextColor(ContextCompat.getColor(context, it))
            }

            valueColor?.let {
                binding.tvValue.setTextColor(ContextCompat.getColor(context, it))
            }

            fontSizeInSp?.let {
                binding.tvKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
                binding.tvValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
            }

            binding.tvKey.text = keyValueData.key
            binding.tvValue.text = if (areValuesAmount)
                getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, keyValueData.value?.toFloatOrNull()?.toInt().orZero().getFormattedAmount())
            else
                keyValueData.value
        }
    }
}