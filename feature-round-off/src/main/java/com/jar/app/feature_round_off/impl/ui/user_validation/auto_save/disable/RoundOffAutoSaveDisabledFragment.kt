package com.jar.app.feature_round_off.impl.ui.user_validation.auto_save.disable

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentAutoSaveDisabledBinding
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Duration

@AndroidEntryPoint
class RoundOffAutoSaveDisabledFragment :
    BaseBottomSheetDialogFragment<FeatureRoundOffFragmentAutoSaveDisabledBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentAutoSaveDisabledBinding
        get() = FeatureRoundOffFragmentAutoSaveDisabledBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            isCancellable = false,
            isDraggable = false,
            shouldShowFullHeight = true
        )

    private var animation: ObjectAnimator? = null

    private val viewModel: RoundOffAutoSaveDisabledViewModel by viewModels()

    override fun setup() {
        viewModel.disableUserRoundOffAutoSave()
        setupUI()
    }

    private fun setupUI() {
        binding.tvRsValue.text = getString(com.jar.app.core_ui.R.string.core_ui_rs_x_int, 30)
        val durationInMillis = Duration.ofSeconds(3).toMillis()

        animation = ObjectAnimator.ofInt(binding.lpiProgress, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            findNavController().getBackStackEntry(R.id.roundOffDetailsFragment).savedStateHandle[com.jar.app.feature_round_off.shared.util.RoundOffConstants.ROUND_OFF_AUTO_SAVE_DISABLED] =
                true
            dismiss()
        }
        animation?.start()
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }

}