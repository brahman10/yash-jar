package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.android.feature_post_setup.R
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellSetupDetailsBinding
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.UserPostSetupData

internal class SetupDetailsViewHolder(private val binding: FeaturePostSetupCellSetupDetailsBinding) :
    BaseViewHolder(binding.root), BaseResources {

    fun setDetails(userPostSetupData: UserPostSetupData) {
        binding.clNewUserContainer.isVisible = userPostSetupData.newUser.orFalse()
        binding.clOldUserContainer.isVisible = userPostSetupData.newUser.orFalse().not()

        userPostSetupData.imageUrl?.let {
            Glide.with(binding.root.context).load(it)
                .placeholder(R.drawable.feature_post_setup_new_user_calendar_header)
                .into(binding.ivBackground)
        }

        //New DS Setup User
        binding.tvNewHeader.text = userPostSetupData.header
        binding.tvNewTitle.text = userPostSetupData.title

        //Old DS Setup User
        binding.tvOldTitle.text = userPostSetupData.title
        binding.tvActiveDays.text = getCustomStringFormatted(
            binding.root.context,
            PostSetupMR.strings.feature_post_setup_x_days_capital,
            userPostSetupData.noOfDaysActive.orZero()
        )
        binding.tvTotalSavings.text = binding.root.context.getString(
            com.jar.app.core_ui.R.string.core_ui_rs_x_int,
            userPostSetupData.totalAmount?.toInt().orZero()
        )
        binding.tvTotalSpins.text = userPostSetupData.spinsCount.orZero().toString()

        binding.clUserDsDataContainer.isVisible =
            userPostSetupData.postSetupDailySavingsInfo != null

        userPostSetupData.postSetupDailySavingsInfo?.let {
            binding.tvDsDataTitle.text = it.title
            binding.tvDsDataDesc.text = HtmlCompat.fromHtml(
                it.description, HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(binding.root.context).load(it.imageUrl)
                .placeholder(com.jar.app.core_ui.R.drawable.core_ui_ic_gold_bricks_with_star)
                .into(binding.ivDsIcon)

            binding.clUserDsDataContainer.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(it.bgColor))
        }
    }
}