package com.jar.app.feature_profile.impl.ui.profile.pic

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.CellProfilePicItemBinding
import com.jar.app.feature_profile.domain.model.AvatarInfo

class ProfilePicAdapter(
    private val user: User?,
    private val onChangeClicked: (position: Int) -> Unit,
    private val onUploadClicked: () -> Unit
) : ListAdapter<AvatarInfo, ProfilePicAdapter.ProfilePicViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AvatarInfo>() {
            override fun areItemsTheSame(
                oldItem: AvatarInfo,
                newItem: AvatarInfo
            ): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(
                oldItem: AvatarInfo,
                newItem: AvatarInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfilePicViewHolder {
        val binding =
            CellProfilePicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfilePicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfilePicViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class ProfilePicViewHolder(
        private val binding: CellProfilePicItemBinding
    ) : BaseViewHolder(binding.root) {

        private var profilePicData: AvatarInfo? = null

        init {
            binding.tvChange.setDebounceClickListener {
                binding.tvChange.isInvisible = bindingAdapterPosition == 0
                onChangeClicked.invoke(bindingAdapterPosition)
            }
        }

        fun bindData(data: AvatarInfo) {
            profilePicData = data

            if (data.imageBitmap != null || data.image.isNotEmpty() || data.resourceId == 0) {
                binding.ivProfile.setUserImage(user)
                binding.tvChange.isInvisible = bindingAdapterPosition == 0
            } else {
                data.resourceId?.let {
                    binding.ivProfile.setImageDrawable(
                        ContextCompat.getDrawable(
                            context, it
                        )
                    )
                }
                binding.tvChange.isInvisible = true
                binding.ivProfile.setDebounceClickListener {
                    onUploadClicked.invoke()
                }
            }
        }

        fun setSelected(isMiddleElement: Boolean, imageBitmap: Bitmap?) {
            binding.ivStatus.isVisible = isMiddleElement
            binding.ivProfile.setBackgroundResource(if (isMiddleElement) R.drawable.feature_profile_bg_green_border_circle else 0)
            binding.ivStatus.isVisible = bindingAdapterPosition != 0
            binding.tvChange.isVisible = imageBitmap != null || bindingAdapterPosition != 0
        }

        fun setUploadImage(bitmap: Bitmap?) {
            bitmap.let {
                if(bindingAdapterPosition == 0) {
                    binding.ivProfile.setBackgroundResource(R.drawable.feature_profile_bg_green_border_circle)
                    binding.tvChange.isVisible = true
                    Glide.with(itemView)
                        .load(bitmap)
                        .circleCrop()
                        .into(binding.ivProfile)
                }
            }
        }
    }
}