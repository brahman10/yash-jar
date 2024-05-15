package com.jar.app.feature_gold_sip.impl.ui.pause_sip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellPauseDurationBinding

internal class PauseGoldSipAdapter(private val onClick: (pauseGoldSipOption: PauseGoldSipOption, position: Int) -> Unit) :
    RecyclerView.Adapter<PauseGoldSipViewHolder>() {

    private var optionList = ArrayList<PauseGoldSipOption>()

    fun submitList(list: ArrayList<PauseGoldSipOption>) {
        this.optionList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PauseGoldSipViewHolder(
        FeatureGoldSipCellPauseDurationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onClick
    )

    override fun onBindViewHolder(holder: PauseGoldSipViewHolder, position: Int) {
        if (optionList.isNotEmpty()) {
            holder.setPauseOption(optionList[position])
        }
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    fun currentList() = optionList
}