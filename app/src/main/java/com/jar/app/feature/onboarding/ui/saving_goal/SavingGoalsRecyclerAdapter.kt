package com.jar.app.feature.onboarding.ui.saving_goal

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.R
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.CellSavingGoalSelectionBinding
import com.jar.app.feature_onboarding.shared.domain.model.ReasonForSavings

class SavingGoalsRecyclerAdapter :
    RecyclerView.Adapter<SavingGoalsRecyclerAdapter.SavingGoalViewHolder>() {

    private val goals = ArrayList<ReasonForSavings>()
    private var listener: ((ReasonForSavings) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingGoalViewHolder {
        return SavingGoalViewHolder(
            CellSavingGoalSelectionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    fun getSelectedGoals() = goals.filter { it.isSelected }

    fun setGoalsClickListener(listener: (ReasonForSavings) -> Unit) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: SavingGoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount() = goals.size

    fun submitList(list: List<ReasonForSavings>) {
        this.goals.clear()
        this.goals.addAll(list)
        notifyDataSetChanged()
    }

    fun updateSelectedGoals(list: List<String>) {
        this.goals.forEach { it.isSelected = list.contains(it.title) }
        notifyDataSetChanged()
    }

    inner class SavingGoalViewHolder(
        private val binding: CellSavingGoalSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reason: ReasonForSavings) {
            binding.tvSavingGoalTitle.text = reason.title
            reason.description?.let {
                binding.tvSavingGoalDescription.isVisible = true
                binding.tvSavingGoalDescription.text = it
            } ?: run {
                binding.tvSavingGoalDescription.isGone = true
            }
            reason.icon?.let {
                binding.tvSavingGoalTitle.updatePadding(top = 0.dp, bottom = 0.dp)
                binding.ivIcon.isVisible = true
                binding.ivIconBg.isVisible = true
                Glide.with(binding.root).load(it).into(binding.ivIcon)
            } ?: run {
                binding.ivIcon.isVisible = false
                binding.ivIconBg.isVisible = false
                binding.tvSavingGoalTitle.updatePadding(top = 24.dp, bottom = 24.dp)
            }

            binding.checkbox.isChecked = reason.isSelected
            val background = if (reason.isSelected) GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor(reason.bgColors?.getOrNull(0) ?: "#272239"),
                    Color.parseColor(reason.bgColors?.getOrNull(1) ?: "#272239"),
                )
            ).apply { cornerRadius = 16.dp.toFloat() } else AppCompatResources.getDrawable(
                binding.root.context,
                R.drawable.bg_cell_saving_goal_selection
            )
            binding.clCellHolder.background = background
            binding.clParent.setDebounceClickListener {
                goals[bindingAdapterPosition].isSelected = !goals[bindingAdapterPosition].isSelected
                notifyItemChanged(bindingAdapterPosition)
                listener?.invoke(reason)
            }
        }
    }
}