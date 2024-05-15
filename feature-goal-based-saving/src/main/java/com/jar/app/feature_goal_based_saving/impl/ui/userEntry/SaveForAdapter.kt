package com.jar.app.feature_goal_based_saving.impl.ui.userEntry

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.LayoutSaveForBinding
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem

internal class SaveForAdapter(
    private val data: List<GoalRecommendedItem>,
    private val onClick: (position: Int, title: String) -> Unit,
): RecyclerView.Adapter<SaveForAdapter.SaveForViewHolder>(){
    private var recyclerView: RecyclerView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveForViewHolder {
        val binding = LayoutSaveForBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaveForViewHolder(binding,recyclerView, onClick)
    }

    override fun onBindViewHolder(holder: SaveForViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setBackgroundColor(Color.TRANSPARENT)
        this.recyclerView = recyclerView
    }

    class SaveForViewHolder(private val binding: LayoutSaveForBinding, private val recyclerView: RecyclerView?, private val onClick: (pos: Int, title: String) -> Unit): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun bind(data: GoalRecommendedItem) {
            binding.tvTitle.text = data.name
            Glide.with(binding.ivIcon).load(data.image).into(binding.ivIcon)
            if (data.isSelected) {
                binding.tvTitle.setTextColor(ContextCompat.getColor(binding.ivIcon.context, com.jar.app.core_ui.R.color.white))
                binding.iconContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.goal_selected)
            } else {
                binding.tvTitle.setTextColor(Color.parseColor("#ACA1D3"))
                binding.iconContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.goal_unselected)
            }
            if (data.isCustomInput) {
                val params = binding.ivIcon.layoutParams
                params.width = binding.ivIcon.context.resources.getDimensionPixelSize(R.dimen.new_dimen_width)
                params.height = binding.ivIcon.context.resources.getDimensionPixelSize(R.dimen.new_dimen_height)
                binding.ivIcon.layoutParams = params
            }
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    data.name?.let { it1 -> onClick.invoke(position, it1) }
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
}
