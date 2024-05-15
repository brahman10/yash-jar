package com.jar.app.feature.rate_us.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.openAppInPlayStore
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.databinding.FragmentRateUsDialogBinding
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.AppRatingUtil
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

@AndroidEntryPoint
internal class RateUsDialog : BaseBottomSheetDialogFragment<FragmentRateUsDialogBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRateUsDialogBinding
        get() = FragmentRateUsDialogBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = false,
        isCancellable = false
    )

    companion object {
        private const val MIN_ACCEPTABLE_RATING = 4
    }

    private val args by navArgs<RateUsDialogArgs>()

    private val viewModel by viewModels<RateUsDialogViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var pref: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var appRatingUtil: AppRatingUtil

    override fun setup() {
        setupUI()
        setupListener()
    }

    private fun setupListener() {
        pref.incrementRatingDialogShownCount()
        analyticsHandler.postEvent(EventKey.SHOWN_RATE_US_DIALOG_PS)

        binding.ratingBar.setOnRatingChangeListener { _, _, _ ->
            binding.txtVeryGood.isVisible =
                binding.ratingBar.rating.toInt() == binding.ratingBar.numStars
            binding.commentGroup.isVisible = binding.ratingBar.rating < MIN_ACCEPTABLE_RATING
        }

        binding.commentGroup.isVisible = false

        binding.txtWillDoLater.setDebounceClickListener {
            dismiss()
        }

        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.btnSubmit.setDebounceClickListener {
            submitRating()
        }
    }

    private fun setupUI() {
        binding.txtHeading.text = args.title ?: getString(R.string.rate_our_app)
        binding.txtSubheading.text = args.subtitle ?: getString(R.string.rate_our_app_subheading)
    }

    private fun submitRating() {
        viewModel.submitUserRating(
            JsonObject(
                mapOf(
                    Pair("rating", JsonPrimitive(binding.ratingBar.rating)),
                    Pair("comment", JsonPrimitive(binding.etComment.text?.toString()))
                )
            )
        )
        navigateToNext()
    }

    private fun navigateToNext() {
        if (binding.ratingBar.rating >= MIN_ACCEPTABLE_RATING) {
            if (remoteConfigApi.isRatingDialogInApp() == "in_app") appRatingUtil.openRatingDialog(
                requireActivity()
            )
            else context?.openAppInPlayStore(BaseConstants.PLAY_STORE_URL)
        }
        getString(R.string.thank_you_for_feedback).snackBar(binding.root)
        dismiss()
    }
}