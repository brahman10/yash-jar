package com.jar.app.feature_jar_duo.impl.ui.duo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jar.app.feature_jar_duo.databinding.FeatureDuoYourDuoCellBinding
import com.jar.app.feature_jar_duo.databinding.FeatureDuoYourDuoHeaderBinding
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData

internal class DuoYourDuosAdapter(
    private val onClick: (groupData: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData) -> Unit,
    private val onRename: (groupDataID: String, groupName: String, imageView: AppCompatImageView) -> Unit,
) : ListAdapter<com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData, DuoYourDuosViewHolder>(ITEM_CALLBACK) {
    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData,
                newItem: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
            ): Boolean {
                return oldItem.groupId == newItem.groupId && oldItem.groupName == newItem.groupName
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData,
                newItem: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuoYourDuosViewHolder {
        val binding =
            FeatureDuoYourDuoCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DuoYourDuosViewHolder(binding, onClick, onRename)
    }

    override fun onBindViewHolder(holder: DuoYourDuosViewHolder, position: Int) {
        getItem(position).let {
            holder.onBind(it)
        }
    }

}