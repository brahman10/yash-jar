package com.jar.app.core_ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.jar.app.core_ui.databinding.ProgressWithTitleAndTickBinding

class ProgressViewWithTick @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    private var binding: ProgressWithTitleAndTickBinding =
        ProgressWithTitleAndTickBinding.inflate(LayoutInflater.from(ctx), this, true)

    fun setData(title: String, inProgress: Boolean, hideSepearator: Boolean = false) {
        binding.tvBankStep.text = title
        binding.ivTick.visibility = if (inProgress) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (inProgress) View.VISIBLE else View.INVISIBLE
        binding.separator.isVisible = hideSepearator.not()
    }

    fun updateStatus(status: String, title: String? = null) {
        when (status) {
            STATUS_DONE -> {
                binding.ivTick.visibility = VISIBLE
                binding.progressBar.visibility = INVISIBLE
                binding.ivError.visibility = INVISIBLE
            }
            STATUS_FAILURE -> {
                binding.ivError.visibility = VISIBLE
                binding.progressBar.visibility = INVISIBLE
                binding.ivTick.visibility = INVISIBLE
            }
        }

        title?.let {
            binding.tvBankStep.text = it
        }
    }

    companion object {
        const val STATUS_DONE = "DONE"
        const val STATUS_FAILURE = "FAILURE"
    }
}