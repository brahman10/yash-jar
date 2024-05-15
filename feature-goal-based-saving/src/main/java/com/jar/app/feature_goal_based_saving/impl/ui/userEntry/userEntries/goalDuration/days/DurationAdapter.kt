package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration.days

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.DurationLayoutBinding
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DurationAdapter(
    val list: MutableList<GoalRecommendedTime>,
    private val onSelect: (duration: Int, position: Int) -> Unit
): RecyclerView.Adapter<DurationAdapter.DurationViewHolder>() {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var debounceJob: Job? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DurationViewHolder {
        val binding = DurationLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DurationViewHolder(binding, recyclerView, onSelect)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: DurationViewHolder, position: Int) {
        holder.bind(
            list[position]
        )
    }

    override fun getItemCount(): Int = list.size

    inner class DurationViewHolder(
        private val viewBinding: DurationLayoutBinding,
        private val recyclerView: RecyclerView?,
        private val onSelect: (duration: Int, position: Int) -> Unit
    ): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(duration: GoalRecommendedTime) {
            viewBinding.tvDuration.text = viewBinding.root.context.getString(
                R.string.gbs_duration,
                duration.number ?: 0, duration.monthText
            )
            if (duration.isSelected) {
                viewBinding.root.background = ContextCompat.getDrawable(
                    viewBinding.root.context,
                    R.drawable.duration_selected
                )
                viewBinding.ivDone.visibility = View.VISIBLE
                viewBinding.tvDuration.setTypeface(null, Typeface.BOLD)
            } else {
                viewBinding.root.background = ContextCompat.getDrawable(
                    viewBinding.root.context,
                    R.drawable.duration_unselected
                )
                viewBinding.ivDone.visibility = View.GONE
                viewBinding.tvDuration.setTypeface(null, Typeface.NORMAL)
            }
            viewBinding.root.setOnClickListener {
                debounceJob?.cancel()
                debounceJob = coroutineScope.launch {
                    delay(300L) // Delay time 300 milliseconds
                    recyclerView?.let { recyclerView ->
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                        if (position in firstVisibleItemPosition..lastVisibleItemPosition) {
                            // Item is partially visible, scroll it to the center
                            val childView = recyclerView.getChildAt(position - firstVisibleItemPosition)
                            val centerX = (recyclerView.width - childView.width) / 2
                            recyclerView.smoothScrollBy(childView.left - centerX, 0)
                        } else {
                            // Item is not visible, scroll it to the top
                            recyclerView.smoothScrollToPosition(position)
                        }
                    }

                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        duration.number?.let { it1 -> onSelect(it1, position) }
                    }
                }
            }
        }
    }

}