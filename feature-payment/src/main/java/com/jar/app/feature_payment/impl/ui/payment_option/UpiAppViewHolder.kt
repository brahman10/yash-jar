package com.jar.app.feature_payment.impl.ui.payment_option

import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.databinding.CellAvailableAppBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp

internal class UpiAppViewHolder(
    private val binding: CellAvailableAppBinding,
    private val onClick: (app: UpiApp) -> Unit
) : BaseViewHolder(binding.root) {

    private var app: UpiApp? = null

    private val packageManager = context.applicationContext.packageManager

    init {
        binding.root.setDebounceClickListener {
            app?.let(onClick)
        }
    }

    fun setApp(upiApp: UpiApp) {
        this.app = upiApp
        val icon = packageManager.getApplicationIcon(upiApp.packageName)
        Glide.with(itemView).load(icon).into(binding.ivAppIcon)
        binding.tvAppName.text = upiApp.appName
    }
}