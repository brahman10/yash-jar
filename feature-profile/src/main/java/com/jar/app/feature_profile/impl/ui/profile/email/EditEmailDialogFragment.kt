package com.jar.app.feature_profile.impl.ui.profile.email

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.isValidEmail
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.FragmentDialogEditEmailBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class EditEmailDialogFragment : BaseDialogFragment<FragmentDialogEditEmailBinding>(), BaseResources{

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider: EditEmailViewModelAndroid by viewModels()

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var isNetworkAvailable = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditEmailBinding
        get() = FragmentDialogEditEmailBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        toggleMainButton()
        initClickListeners()
        observeLiveData()
        binding.etEmail.showKeyboard()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_ChangeEmail_EmailPopUp)
    }

    private fun setupUI() {
        binding.editProfileSuccessLayout.animTick.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
    }

    private fun observeLiveData() {

        networkFlow.networkStatus
            .onEach {
                isNetworkAvailable = it
                toggleMainButton()
            }
            .launchIn(uiScope)

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.etEmail.setText(it.email)
                binding.etEmail.setSelection(binding.etEmail.text?.length.orZero())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateUserLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_email_updated_successfully)
                        binding.cvProfileNameContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_EmailPopUp)
                                    delay(3000)
                                    dismissAllowingStateLoss()
                                }
                            }
                        )
                    }
                )
            }
        }
    }

        fun initClickListeners() {
            binding.tvCancel.setDebounceClickListener {
                analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_EmailPopUp)
                dismiss()
            }

            binding.btnSave.setDebounceClickListener {
                val email = binding.etEmail.text
                if (!email.isNullOrBlank()) {
                    viewModel.updateUserEmail(email = email.toString().trim())
                    binding.etEmail.clearFocus()
                    analyticsHandler.postEvent(
                        ProfileEventKey.Events.Clicked_Save_EmailPopUp,
                        mapOf(ProfileEventKey.Props.Email to email)
                    )
                } else {
                    getCustomString(MR.strings.feature_profile_please_enter_a_valid_email).snackBar(
                        binding.root
                    )
                }
            }

            binding.etEmail.textChanges()
                .debounce(300)
                .onEach {
                    when {
                        it.isNullOrBlank() -> {
                            resetInputStatus()
                        }

                        else -> {
                            setInputStatus(verifyEmail(it))
                        }
                    }
                }
                .launchIn(uiScope)

            binding.etEmail.setDebounceClickListener {
                analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_EnterEmail_EmailPopUp)
            }

            binding.ivStatus.setDebounceClickListener {
                binding.etEmail.setText("")
            }
        }

    private fun resetInputStatus() {
        binding.ivStatus.isVisible = false
        binding.tvError.isInvisible = true
        binding.clEmail.setBackgroundResource(R.drawable.feature_profile_round_dark_bg_16dp)
        toggleMainButton()
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false else (
                isNetworkAvailable
                        && verifyEmail(binding.etEmail.text?.toString() ?: ""))
        binding.btnSave.isEnabled = shouldEnable
    }

    private fun verifyEmail(name: CharSequence): Boolean {
        return if (name.toString().isValidEmail())
            true
        else {
            binding.tvError.text = getCustomString(MR.strings.feature_profile_please_enter_a_valid_email)
            false
        }
    }

    private fun setInputStatus(isValid: Boolean) {
        binding.ivStatus.isVisible = true
        binding.ivStatus.setImageResource(if (isValid) R.drawable.feature_profile_ic_tick_green else R.drawable.feature_profile_ic_circle_close)
        binding.clEmail.setBackgroundResource(if (isValid) R.drawable.feature_profile_round_dark_bg_16dp else R.drawable.feature_profile_bg_error_name)
        binding.tvError.isInvisible = isValid
        toggleMainButton()
    }
}