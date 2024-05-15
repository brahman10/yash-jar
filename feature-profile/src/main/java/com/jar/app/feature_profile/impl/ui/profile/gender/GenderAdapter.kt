package com.jar.app.feature_profile.impl.ui.profile.gender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.CellProfileGenderItemBinding
import com.jar.app.feature_profile.domain.model.GenderData

class GenderAdapter(
    private val onGenderSelected: (genderData: GenderData?) -> Unit
) : ListAdapter<GenderData, GenderAdapter.GenderViewHolder>(DIFF_CALLBACK), BaseResources {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GenderData>() {
            override fun areItemsTheSame(oldItem: GenderData, newItem: GenderData): Boolean {
                return oldItem.genderStringId == newItem.genderStringId
            }

            override fun areContentsTheSame(oldItem: GenderData, newItem: GenderData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderViewHolder {
        val binding =
            CellProfileGenderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenderViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class GenderViewHolder(
        private val binding: CellProfileGenderItemBinding
    ) : BaseViewHolder(binding.root) {

        private var genderData: GenderData? = null

        init {
            binding.root.setDebounceClickListener {
                onGenderSelected(genderData)
            }
        }

        fun bindData(data: GenderData) {
            this.genderData = data
            binding.tvGender.text = getCustomString(itemView.context, data.genderStringId)
            binding.ivStatus.isVisible = data.isSelected
            binding.root.setBackgroundResource(
                if (data.isSelected.orFalse()) R.drawable.feature_profile_round_light_dark_white_border_28dp
                else R.drawable.feature_profile_round_dark_bg_28dp
            )
        }
    }
}