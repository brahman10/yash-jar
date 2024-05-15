package com.jar.app.core_ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.NetworkStripViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkStripView @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private lateinit var binding: NetworkStripViewBinding

    init {
        binding = NetworkStripViewBinding.inflate(LayoutInflater.from(ctx), this, true)
    }

    fun toggleNetworkLayout(isConnected: Boolean, uiScope: CoroutineScope) {
        binding.clNetworkContainer.isSelected = isConnected
        binding.tvInternetConnectionText.text =
            if (isConnected) ctx.getString(R.string.core_ui_we_are_back_online) else ctx.getString(R.string.core_ui_no_internet_available_please_try_again)
        binding.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isConnected) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off, 0, 0, 0
        )
        if (isConnected) {
            if (binding.networkExpandableLayout.isExpanded) {
                uiScope.launch {
                    delay(500)
                    binding.networkExpandableLayout.collapse(true)
                }
            }
        } else {
            binding.networkExpandableLayout.expand(true)
        }
    }
}