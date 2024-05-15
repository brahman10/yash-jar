package com.jar.app.feature_jar_duo.impl.ui.duo_list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPendingDuoHeaderBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData

internal class DuoHeaderViewHolder(
    private val binding: FeatureDuoPendingDuoHeaderBinding,
    ) : BaseViewHolder(binding.root) {

    fun onBind(headerData: com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData, onViewAllClick: () -> Unit, showViewAll: Boolean) {


        if (headerData.itemCount != null) {
            binding.tvYourDuos.text = String.format(
                binding.root.context.resources.getString(headerData.headerTextResource),
                headerData.itemCount
            )
            if (headerData.itemCount.orZero() > 3 && showViewAll) {
                binding.viewAllButton.visibility = View.VISIBLE
                binding.viewAllButton.setDebounceClickListener {
                    onViewAllClick()
                }
            } else {
                binding.viewAllButton.visibility = View.GONE
            }
        }

    }
}