package com.jar.app.feature_kyc.impl.ui.kyc_faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_kyc.databinding.CellKycFaqBinding
import com.jar.app.feature_kyc.shared.domain.model.Faq

internal class KycFaqAdapter : ListAdapter<Faq, KycFaqAdapter.KycFaqViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Faq>() {
            override fun areItemsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KycFaqViewHolder {
        val binding = CellKycFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KycFaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KycFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setFaq(it)
        }
    }

    internal inner class KycFaqViewHolder(private val binding: CellKycFaqBinding) : BaseViewHolder(binding.root) {

        init {
            binding.root.setDebounceClickListener {
                binding.ivExpand.animate()
                    .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
                binding.expandableLayout.toggle()
            }
        }

        fun setFaq(faq: Faq) {
            binding.ivExpand.rotation = if (binding.expandableLayout.isExpanded) 180f else 0f
            binding.tvQuestion.text = faq.question
            binding.tvAnswer.text = faq.answer
        }
    }
}