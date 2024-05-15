package com.jar.app.feature_jar_duo.impl.ui.duo_list

import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPendingDuoHeaderBinding

internal class DuoPendingRequestHeaderViewHolder(
    private val binding: FeatureDuoPendingDuoHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind() {
        binding.tvYourDuos.text =
            binding.tvYourDuos.context.getString(R.string.feature_duo_duo_requests)
    }
}