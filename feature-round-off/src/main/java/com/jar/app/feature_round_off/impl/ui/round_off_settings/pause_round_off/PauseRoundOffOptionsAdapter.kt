package com.jar.app.feature_round_off.impl.ui.round_off_settings.pause_round_off

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffCellPauseRoundOffTimeBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption

internal class PauseRoundOffOptionsAdapter(private val onClick: (pauseOption: PauseRoundOffOption, position: Int) -> Unit) :
    ListAdapter<PauseRoundOffOption, PauseRoundOffOptionsAdapter.PauseRoundOffOptionViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PauseRoundOffOption>() {
            override fun areItemsTheSame(
                oldItem: PauseRoundOffOption,
                newItem: PauseRoundOffOption
            ): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(
                oldItem: PauseRoundOffOption,
                newItem: PauseRoundOffOption
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PauseRoundOffOptionViewHolder {
        val binding = FeatureRoundOffCellPauseRoundOffTimeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PauseRoundOffOptionViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PauseRoundOffOptionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setPauseOption(it)
        }
    }

    internal class PauseRoundOffOptionViewHolder constructor(
        private val binding: FeatureRoundOffCellPauseRoundOffTimeBinding,
        onClick: (pauseOption: PauseRoundOffOption, position: Int) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var isSelected = false

        private var pauseOption: PauseRoundOffOption? = null

        init {
            binding.root.setOnClickListener {
                if (pauseOption != null) {
                    onClick.invoke(pauseOption!!, bindingAdapterPosition)
                }
            }
        }

        fun setPauseOption(pauseOption: PauseRoundOffOption) {
            this.pauseOption = pauseOption
            binding.tvNumDay.text = pauseOption.number.toString()
            binding.tvDayHeader.text =
                if (pauseOption.number == 1)
                    getCustomString(MR.strings.feature_round_off_day)
                else
                    getCustomString(MR.strings.feature_round_off_days)
        }

        fun select() {
            isSelected = true
            binding.root.setBackgroundResource(R.drawable.feature_round_off_bg_round_off_to_on)
        }

        fun deselect() {
            if (isSelected) {
                isSelected = false
                binding.root.setBackgroundResource(R.drawable.feature_round_off_bg_round_off_to_off)
            }
        }
    }
}