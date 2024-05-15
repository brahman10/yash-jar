package com.jar.app.feature_lending.impl.ui.bank.enter_account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.CellBankChecklistBinding

internal class BankCheckListAdapter : ListAdapter<String, BankCheckListAdapter.BankCheckListVH>(
    DIFF_UTIL
) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    private var textColor:Int?= null
        fun setTextColor(color:Int) {
            textColor = color
        }

    internal inner class BankCheckListVH(
        private val binding: CellBankChecklistBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: String, position: Int) {
            binding.tvCheck.setPlotlineViewTag(BaseConstants.PLOTLINE_BANK_CHECK_LIST_ITEM+position)
            binding.tvCheck.text = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textColor?.let {
                binding.tvCheck.setTextColor(it)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankCheckListVH {
        val binding = CellBankChecklistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BankCheckListVH(binding)
    }

    override fun onBindViewHolder(holder: BankCheckListVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }
}