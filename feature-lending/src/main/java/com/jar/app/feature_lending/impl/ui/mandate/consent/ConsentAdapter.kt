package com.jar.app.feature_lending.impl.ui.mandate.consent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.CellConsentBinding
import com.jar.app.feature_lending.shared.domain.model.v2.ConsentDto

internal class ConsentAdapter(
    private var onItemSelected: (position: Int, isChecked: Boolean) -> Unit
) : ListAdapter<ConsentDto, ConsentAdapter.ConsentViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<ConsentDto>() {
            override fun areItemsTheSame(oldItem: ConsentDto, newItem: ConsentDto): Boolean {
                return oldItem.consentText == newItem.consentText && oldItem.isSelected == newItem.isSelected
            }

            override fun areContentsTheSame(oldItem: ConsentDto, newItem: ConsentDto): Boolean {
                return oldItem.consentText == newItem.consentText
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ConsentViewHolder(
        onItemSelected, CellConsentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ConsentViewHolder, position: Int) {
        getItem(position)?.let { holder.setData(it) }
    }
    internal class ConsentViewHolder(
        private var onItemSelected: (position: Int, isChecked: Boolean) -> Unit,
        private val binding: CellConsentBinding
    ) : BaseViewHolder(binding.root) {

        fun setData(data: ConsentDto) {
            binding.cbTnC.setDebounceClickListener {
                onItemSelected(bindingAdapterPosition, data.isSelected.not())
            }
            binding.tvTnc.setDebounceClickListener {
                onItemSelected(bindingAdapterPosition, data.isSelected.not())
            }
            binding.cbTnC.isChecked = data.isSelected
            binding.tvTnc.text = HtmlCompat.fromHtml(data.consentText.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
}