package com.jar.app.feature.onboarding.ui.sms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellSmsFaqBinding
import com.jar.app.core_base.domain.model.Faq

class CommonFaqAdapter : ListAdapter<Faq, CommonFaqAdapter.SmsViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Faq>() {
            override fun areItemsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val binding = CellSmsFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it, position)
        }
    }

    inner class SmsViewHolder(
        private val binding: CellSmsFaqBinding
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: Faq, position: Int) {
            binding.tvQuestion.text = "${position+1}. ${data.question}"
            binding.tvAnswer.text = data.answer
        }
    }
}