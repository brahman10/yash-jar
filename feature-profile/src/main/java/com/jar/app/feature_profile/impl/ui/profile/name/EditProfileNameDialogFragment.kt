package com.jar.app.feature_profile.impl.ui.profile.name

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
import com.jar.app.base.util.areAllCharsSame
import com.jar.app.base.util.hasMoreSpacesThanAlphabets
import com.jar.app.base.util.hasMoreThanXRepeatingChars
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.FragmentDialogEditProfileNameBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_profile.ui.EditProfileNameViewModel
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
class EditProfileNameDialogFragment : BaseDialogFragment<FragmentDialogEditProfileNameBinding>(),
    BaseResources {

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var isNetworkAvailable = false

    private val viewModelProvider by viewModels<EditProfileNameViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditProfileNameBinding
        get() = FragmentDialogEditProfileNameBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        toggleMainButton()
        initClickListeners()
        observeLiveData()
        binding.etName.showKeyboard()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_ChangeName_NamePopUp)
    }

    private fun setupUI() {
        binding.editProfileSuccessLayout.animTick.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
    }

    private fun observeLiveData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkFlow.networkStatus.collect {
                    isNetworkAvailable = it
                    toggleMainButton()
                }
            }
        }

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.etName.setText(it.getFullName())
                binding.etName.setSelection(binding.etName.text?.length.orZero())
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
                        viewModel.updateUserNameLocally(it?.firstName, it?.lastName)
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_name_updated_successfully)
                        binding.cvProfileNameContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_NamePopUp)
                                    delay(3000)
                                    dismissAllowingStateLoss()
                                }
                            }
                        )
                    })
            }
        }
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_NamePopUp)
            dismiss()
        }

        binding.btnSave.setDebounceClickListener {
            val name = binding.etName.text
            if (!name.isNullOrBlank()) {
                viewModel.updateName(name = name.toString().trim())
                binding.etName.clearFocus()
                analyticsHandler.postEvent(
                    ProfileEventKey.Events.Clicked_Save_NamePopUp,
                    mapOf(ProfileEventKey.Props.Name to name)
                )
            } else {
                getCustomString(MR.strings.feature_profile_please_enter_a_valid_name).snackBar(
                    binding.root
                )
            }
        }

        binding.etName.textChanges()
            .debounce(300)
            .onEach {
                when {
                    it?.length.orZero() > 30 -> {
                        binding.tvError.text =
                            getCustomString(MR.strings.feature_profile_name_cant_be_more_than_30)
                        setInputStatus(isValid = false)
                        toggleMainButton(disableAnyway = true)
                        uiScope.launch {
                            delay(1500)
                            if (binding.etName.text?.length.orZero() > 30) {
                                binding.etName.setText(it?.substring(0, 30))
                                binding.etName.setSelection(binding.etName.text?.length.orZero())
                            }
                        }
                    }

                    it.isNullOrBlank() -> {
                        resetInputStatus()
                    }

                    else -> {
                        setInputStatus(verifyName(it))
                    }
                }
            }
            .launchIn(uiScope)

        binding.etName.setDebounceClickListener {
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_EnterName_NamePopUp)
        }

        binding.ivStatus.setDebounceClickListener {
            binding.etName.setText("")
        }
    }

    private fun resetInputStatus() {
        binding.ivStatus.isVisible = false
        binding.tvError.isInvisible = true
        binding.clName.setBackgroundResource(R.drawable.feature_profile_round_dark_bg_16dp)
        toggleMainButton()
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) {
            false
        } else {
            isNetworkAvailable && verifyName(binding.etName.text?.toString() ?: "")
        }
        binding.btnSave.isEnabled = shouldEnable
    }

    private fun verifyName(name: CharSequence): Boolean {
        return when {
            name.length < 3 -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_name_seems_very_unique)
                false
            }

            name.hasMoreSpacesThanAlphabets() -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_name_seems_very_unique)
                false
            }

            name.toString().lowercase().areAllCharsSame() -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_enter_something_real)
                false
            }

            name.toString().lowercase().hasMoreThanXRepeatingChars() -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_enter_something_real)
                false
            }

            else -> true
        }
    }

    private fun setInputStatus(isValid: Boolean) {
        binding.ivStatus.isVisible = true
        binding.ivStatus.setImageResource(if (isValid) R.drawable.feature_profile_ic_tick_green else R.drawable.feature_profile_ic_circle_close)
        binding.clName.setBackgroundResource(if (isValid) R.drawable.feature_profile_round_dark_bg_16dp else R.drawable.feature_profile_bg_error_name)
        binding.tvError.isInvisible = isValid
        toggleMainButton()
    }
}