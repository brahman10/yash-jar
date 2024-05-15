package com.jar.app.feature_kyc.impl.ui.alternate_doc.choose_doc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_kyc.databinding.CellKycDocBinding
import com.jar.app.feature_kyc.shared.domain.model.KycDoc

internal class ChooseDocAdapter(
    private val onDocSelected : (KycDoc) -> Unit
): ListAdapter<KycDoc, ChooseDocAdapter.ChooseDocViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<KycDoc>() {
            override fun areItemsTheSame(oldItem: KycDoc, newItem: KycDoc): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: KycDoc, newItem: KycDoc): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseDocViewHolder {
        val binding = CellKycDocBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseDocViewHolder(binding, onDocSelected)
    }

    override fun onBindViewHolder(holder: ChooseDocViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class ChooseDocViewHolder(
        private val binding: CellKycDocBinding,
        onDocSelected : (KycDoc) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var kycDoc: KycDoc? = null

        init {
            binding.clKycDoc.setDebounceClickListener {
                kycDoc?.let {
                    onDocSelected.invoke(it)
                }
            }
        }

        fun bindData(data: KycDoc) {
            kycDoc = data
            binding.tvTitle.text = data.title
            Glide.with(itemView).load(data.icon).into(binding.ivKycDoc)
            binding.viewDimmer.alpha = if (data.disable) 0.5f else 0f
        }
    }
}