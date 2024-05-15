package com.jar.app.core_ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jar.app.core_ui.databinding.CoreUiIdentityCardViewBinding

class IdentityCardView @JvmOverloads constructor(
    private val ctx: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private var binding: CoreUiIdentityCardViewBinding

    init {
        binding =
            CoreUiIdentityCardViewBinding.inflate(LayoutInflater.from(ctx), this, true)
        startShimmer()
    }

    fun setIdentityHeading(value: String) {
        binding.tvIdentityHeading.text = value
    }

    fun setIdentity(value: String) {
        binding.tvIdentity.text = value
    }

    fun setName(value: String) {
        binding.tvName.text = value
    }

    fun setDob(value: String) {
        binding.tvDob.text = value
    }

    private fun startShimmer() {
        binding.shimmerPlaceholder.startShimmer()
    }

    private fun stopShimmer() {
        binding.shimmerPlaceholder.stopShimmer()
    }
}