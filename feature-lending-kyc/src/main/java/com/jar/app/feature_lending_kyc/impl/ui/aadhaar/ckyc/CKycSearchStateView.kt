package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.ckyc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingCkycStateViewBinding

internal class CKycSearchStateView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: FeatureLendingCkycStateViewBinding

    private var state = State.PROGRESS

    init {
        removeAllViews()
        binding = FeatureLendingCkycStateViewBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
        attrs?.let {
            val typedArray =
                context.theme.obtainStyledAttributes(
                    it,
                    R.styleable.CKycSearchStateView,
                    defStyleAttr,
                    0
                )
            val stateTitleText =
                typedArray.getString(R.styleable.CKycSearchStateView_stateTitleText)
            binding.tvStateTitle.text = stateTitleText
            typedArray.recycle()
        }
        updateViewState(state)
    }

    fun updateViewState(state: State) {
        this.state = state
        when (state) {
            State.PROGRESS -> {
                binding.tvProgressBar.isVisible = true
                binding.ivTickIcon.isVisible = false
                binding.tvNotFound.isVisible = false
                binding.stateContainer.isVisible = true
            }
            State.SUCCESSFUL -> {
                binding.tvProgressBar.isVisible = false
                binding.ivTickIcon.isVisible = true
                binding.tvNotFound.isVisible = false
                binding.stateContainer.isVisible = true
            }
            State.NOT_FOUND -> {
                binding.tvNotFound.isVisible = true
                binding.stateContainer.isVisible = false
            }
        }
        invalidate()
    }

    enum class State {
        PROGRESS, SUCCESSFUL, NOT_FOUND
    }

}