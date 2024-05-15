package com.jar.app.feature.onboarding.ui.saving_goal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.databinding.FragmentSavingsGoalSelectionV2HolderBinding
import com.jar.app.feature_onboarding.shared.domain.model.GoalsV2

class SavingsGoalSelectionV2Adapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<GoalsV2, SavingsGoalSelectionV2Adapter.SavingGoalV2ViewHolder>(ITEM_CALLBACK) {

    // Define an interface to handle item clicks
    interface OnItemClickListener {
        fun onItemClick(goal: GoalsV2)
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<GoalsV2>() {
            override fun areItemsTheSame(oldItem: GoalsV2, newItem: GoalsV2): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: GoalsV2, newItem: GoalsV2): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingGoalV2ViewHolder {
        val binding =
            FragmentSavingsGoalSelectionV2HolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavingGoalV2ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavingGoalV2ViewHolder, position: Int) {
        getItem(position)?.let { goal->
            holder.bind(goal)

            holder.itemView.setOnClickListener {
                itemClickListener.onItemClick(goal)
            }
        }
    }

    fun getSelectedGoals(): List<String>{
        var selectedGoals : MutableList<String> = emptyList<String>().toMutableList()

        currentList.forEach { goal ->
            if(goal.isSelected == true){
                selectedGoals.add(goal.title)
            }
        }

        return selectedGoals.toList()
    }

    inner class SavingGoalV2ViewHolder(
        private val binding: FragmentSavingsGoalSelectionV2HolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: GoalsV2) {
            Glide.with(binding.root.context)
                .load(goal.icon).into(binding.ivGoalIcon)
            binding.tvGoalName.text = goal.title
            if(goal.isSelected == true){
                binding.ivGoalIcon.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_black_green_border_12dp)
                binding.ivSelectedIcon.visibility = View.VISIBLE
            }else{
                binding.ivGoalIcon.setBackgroundResource(com.jar.app.core_ui.R.drawable.rounded_black_grey_border_12dp)
                binding.ivSelectedIcon.visibility = View.INVISIBLE
            }
        }
    }
}