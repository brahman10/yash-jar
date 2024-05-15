package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.v2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jar.app.base.util.capitaliseFirstChar
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_jar_duo.databinding.FeatureDuoLayoutToggleItemBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2

internal class DuoOptionsAdapter(
    private val isOwner: Boolean,
    private val isSample: Boolean,
    private val onClick: (data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) -> Unit,
) : ListAdapter<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2, DuoOptionsAdapter.UserOptionsViewHolder>(DIFF_UTIL) {

    private var lastClickedPosition = -1

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2, newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2, newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserOptionsViewHolder(
        FeatureDuoLayoutToggleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ), onClick
    )

    override fun onBindViewHolder(holder: UserOptionsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setOptionsInfo(it)
        }
    }

    fun updateCancel() {
        if (lastClickedPosition >= 0)
            notifyItemChanged(lastClickedPosition)
    }

    inner class UserOptionsViewHolder(
        private val binding: FeatureDuoLayoutToggleItemBinding,
        private val onClick: (data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) -> Unit
    ) : BaseViewHolder(binding.root) {

        fun setOptionsInfo(info: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2) {

            if (!isOwner) {
                binding.root.alpha = 0.5f
            } else {
                binding.root.alpha = 1f
            }

            if (isSample) binding.constraintLayout.isEnabled = false


            binding.optionStatusSwitch.isChecked = info.enabled


            Glide.with(context).load(info.iconLink).apply(RequestOptions().override(35))
                .centerCrop().into(binding.optionIcon)
            binding.optionsLabel.text =
                info.displayText.replace(' ', '\n')

            binding.constraintLayout.setDebounceClickListener {
                if (binding.optionStatusSwitch.isChecked) {
                    onClick.invoke(
                        com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle(
                            message = info.displayText.capitaliseFirstChar(),
                            optionName = info.displayText,
                            initialState = binding.optionStatusSwitch.isChecked
                        )
                    )
                    if (isOwner) {
                        binding.optionStatusSwitch.isChecked = true
                    }
                } else {
                    if (isOwner) {
                        binding.optionStatusSwitch.isChecked = true
                    }
                    lastClickedPosition = bindingAdapterPosition
                    onClick.invoke(
                        com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle(
                            deepLink = info.deepLink,
                            optionName = info.displayText,
                            initialState = !(binding.optionStatusSwitch.isChecked)
                        )
                    )
                }
            }
        }
    }
}
