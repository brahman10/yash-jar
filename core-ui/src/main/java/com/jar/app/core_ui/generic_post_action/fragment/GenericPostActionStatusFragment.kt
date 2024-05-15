package com.jar.app.core_ui.generic_post_action.fragment

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.databinding.CoreUiFragmentGenericPostActionStatusBinding
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
class GenericPostActionStatusFragment :
    BaseBottomSheetDialogFragment<CoreUiFragmentGenericPostActionStatusBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiFragmentGenericPostActionStatusBinding
        get() = CoreUiFragmentGenericPostActionStatusBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    private var animation: ObjectAnimator? = null

    private val args: GenericPostActionStatusFragmentArgs by navArgs()

    private val genericPostActionStatusData by lazy {
        serializer.decodeFromString<GenericPostActionStatusData>(
            decodeUrl(args.genericPostActionStatusData)
        )
    }

    private val postActionStatus by lazy {
        PostActionStatus.valueOf(genericPostActionStatusData.postActionStatus)
    }

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            shouldShowFullHeight = true,
            isCancellable = false,
            isDraggable = false,
        )

    override fun setup() {
        setupViewAccToStatus()
        setIllustrationOrLottie()
        setTextualContent()
        if (genericPostActionStatusData?.shouldShowTopProgress.orFalse())
            setLinearProgressWithAnimation()
    }

    private fun setupViewAccToStatus() {
        when (postActionStatus) {
            PostActionStatus.ENABLED -> {
                binding.animationView.isVisible = true
                binding.animationView.playLottieWithUrlAndExceptionHandling(
                    requireContext(),
                    BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                )
            }
            PostActionStatus.DISABLED -> {
                binding.animationView.isVisible = false
                binding.progressHorizontal.isVisible = false
            }
            PostActionStatus.RESUMED -> {
                binding.progressHorizontal.isVisible = false
                binding.animationView.isVisible = true
                binding.animationView.playLottieWithUrlAndExceptionHandling(
                    requireContext(),
                    BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                )
            }
        }
    }

    private fun setIllustrationOrLottie() {
        if (genericPostActionStatusData.lottieUrl.isNullOrEmpty().not()) {
            binding.smallerLottie.isVisible = true
            binding.smallerLottie.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                genericPostActionStatusData.lottieUrl!!
            )
        } else if (genericPostActionStatusData.imageUrl.isNullOrEmpty().not()) {
            binding.ivIllustration.isVisible = true
            Glide.with(requireContext()).load(genericPostActionStatusData.imageUrl!!)
                .into(binding.ivIllustration)
        } else if (genericPostActionStatusData.imageRes != null) {
            binding.ivIllustration.isVisible = true
            Glide.with(requireContext()).load(genericPostActionStatusData.imageRes!!)
                .into(binding.ivIllustration)
        }
        binding.animationView.isVisible = genericPostActionStatusData.shouldShowConfettiFromTop
    }

    private fun setTextualContent() {
        binding.tvHeader.textSize = genericPostActionStatusData.headerTextSize
        binding.tvTitle.textSize = genericPostActionStatusData.titleTextSize
        binding.tvDescription.textSize = genericPostActionStatusData.descTextSize
        binding.tvHeader.setTextColor(
            ContextCompat.getColor(requireContext(), genericPostActionStatusData.headerColorRes)
        )
        binding.tvTitle.setTextColor(
            ContextCompat.getColor(requireContext(), genericPostActionStatusData.titleColorRes)
        )
        binding.tvDescription.setTextColor(
            ContextCompat.getColor(
                requireContext(), genericPostActionStatusData.descriptionColorRes
            )
        )
        binding.tvHeader.isVisible =
            genericPostActionStatusData.header.isNullOrEmpty().not()
        binding.tvTitle.isVisible =
            genericPostActionStatusData.title.isNullOrEmpty().not()
        binding.tvDescription.isVisible =
            genericPostActionStatusData.description.isNullOrEmpty().not()
        binding.tvHeader.text = genericPostActionStatusData.header
        binding.tvTitle.text = genericPostActionStatusData.title
        binding.tvDescription.text = genericPostActionStatusData.description
    }

    private fun setLinearProgressWithAnimation() {
        val durationInMillis = Duration.ofSeconds(genericPostActionStatusData.screenTime).toMillis()
        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                BaseConstants.ON_GENERIC_SCREEN_DISMISSED,
                true
            )
            popBackStack()
        }
        animation?.start()
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }

}