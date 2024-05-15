package com.jar.app.feature_jar_duo.impl.ui.duo_list

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.spaceBeforeUpperCaseChar
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_jar_duo.databinding.FeatureDuoYourDuoCellBinding
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData

internal class DuoYourDuosViewHolder(private val binding:FeatureDuoYourDuoCellBinding,
                                     private val onClick: (groupData: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData) -> Unit,
                                     private val onRename: (groupDataID: String, groupName: String,  imageView : AppCompatImageView) -> Unit
                        ): BaseViewHolder(binding.root) {

    fun onBind(groupData: com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData) {

        if (groupData.groupProfilePictures?.getOrNull(1).isNullOrEmpty()) {
            binding.ivUser2Thumbnail.isVisible = false
            binding.tvUser2Initials.isVisible = true
            binding.tvUser2Initials.text =
                groupData.groupUserNames?.getOrNull(1)?.spaceBeforeUpperCaseChar()?.asInitials() ?: ""
        } else {
            binding.tvUser2Initials.isVisible = false
            binding.ivUser2Thumbnail.isVisible = true
            groupData.groupProfilePictures?.getOrNull(1)?.let {
                Glide.with(binding.root).load(it).circleCrop()
                    .into(binding.ivUser2Thumbnail)
            }
        }
        if (groupData.groupProfilePictures?.getOrNull(0).isNullOrEmpty()) {
            binding.ivUser1Thumbnail.isVisible = false
            binding.tvUser1Initials.isVisible = true

            binding.tvUser1Initials.text =
                groupData.groupUserNames?.getOrNull(0)?.spaceBeforeUpperCaseChar()?.asInitials() ?: ""
        } else {
            binding.tvUser1Initials.isVisible = false
            binding.ivUser1Thumbnail.isVisible = true
            groupData.groupProfilePictures?.getOrNull(0)?.let {
                Glide.with(binding.root).load(it).circleCrop()
                    .into(binding.ivUser1Thumbnail)
            }
        }
            binding.tvGroupName.text = groupData.groupName

            binding.root.setDebounceClickListener {
                onClick.invoke(groupData)
            }

            binding.ivInfo.setDebounceClickListener {
                onRename.invoke(
                    groupData.groupId.toString(),
                    groupData.groupName.toString(),
                    binding.ivInfo
                )
            }
        }
    }