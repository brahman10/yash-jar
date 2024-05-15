package com.jar.app.core_ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.domain.model.Faq
import com.jar.app.core_ui.databinding.ItemMilestoneFaqBinding

class FaqAdapter : ListAdapter<Faq, FaqViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemMilestoneFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setFaq(it)
        }
    }
}