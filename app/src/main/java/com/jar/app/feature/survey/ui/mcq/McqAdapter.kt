package com.jar.app.feature.survey.ui.mcq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellMcqBinding
import com.jar.app.feature.survey.domain.model.ChoiceWrapper

class McqAdapter(
    private val onClick: (Int,ChoiceWrapper) -> Unit
) : ListAdapter<ChoiceWrapper, McqViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ChoiceWrapper>() {
            override fun areItemsTheSame(oldItem: ChoiceWrapper, newItem: ChoiceWrapper): Boolean {
                return oldItem.choice == newItem.choice
            }

            override fun areContentsTheSame(oldItem: ChoiceWrapper, newItem: ChoiceWrapper): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = McqViewHolder(
        CellMcqBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: McqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setMcq(it)
        }
    }
}