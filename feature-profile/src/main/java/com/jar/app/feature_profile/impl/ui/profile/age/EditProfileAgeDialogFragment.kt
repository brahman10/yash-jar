package com.jar.app.feature_profile.impl.ui.profile.age

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.textChanges
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.FragmentDialogEditProfileAgeBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.impl.ui.profile.EditProfileFragmentViewModelAndroid
import com.jar.app.feature_profile.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileAgeDialogFragment : BaseDialogFragment<FragmentDialogEditProfileAgeBinding>(),
    BaseResources {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var sharedPreferencesUserLiveData: SharedPreferencesUserLiveData

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditProfileAgeBinding
        get() = FragmentDialogEditProfileAgeBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private val viewModelProvider by viewModels<EditProfileAgeViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val profileFragmentViewModel by viewModels<EditProfileFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private var adapter: AgeAdapter? = null

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var isNetworkAvailable = false

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
        getData()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_ChangeAge_AgePopUp)
    }

    private fun setupUI() {
        binding.editProfileSuccessLayout.animTick.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
        binding.etAge.post {
            binding.etAge.showKeyboard()
        }
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_AgePopUp)
        }

        binding.btnSave.setDebounceClickListener {
            viewModel.currentlySelected?.let {
                viewModel.updateAge(it)
                analyticsHandler.postEvent(
                    ProfileEventKey.Events.Clicked_Save_AgePopUp, mapOf(
                        ProfileEventKey.Props.Age to it
                    )
                )
            }
                ?: run { getCustomString(MR.strings.feature_profile_select_age).snackBar(binding.root) }
        }

        binding.etAge.textChanges()
            .debounce(100)
            .onEach {
                when {
                    it.isNullOrBlank() -> {
                        resetInputStatus()
                    }

                    else -> {
                        setInputStatus(verifyAge(it))
                    }
                }
            }
            .launchIn(uiScope)

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        networkFlow.networkStatus
            .onEach {
                isNetworkAvailable = it
                toggleMainButton()
            }
            .launchIn(uiScope)

        sharedPreferencesUserLiveData.observe(viewLifecycleOwner) {
            it?.age?.let {
                binding.etAge.setText(it.toString())
                binding.etAge.setSelection(binding.etAge.text?.length.orZero())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.ageListFlow.collectLatest {
                    adapter?.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.updateUserFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onError = { errorMessage, errorCode ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.updateUserAgeLocally(it?.age)
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_age_updated_successfully)
                        binding.cvProfileAgeContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_AgePopUp)
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

    private fun verifyAge(age: CharSequence): Boolean {
        val ageIntegerValue = age.toString().toInt()
        return when {
            ageIntegerValue <= 17 -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_you_must_be_above_18)
                false
            }

            ageIntegerValue > 100 -> {
                binding.tvError.text =
                    getCustomString(MR.strings.feature_profile_maximum_age_limit_reached)
                false
            }

            else -> true
        }
    }

    private fun setInputStatus(isValid: Boolean) {
        viewModel.currentlySelected = binding.etAge.text.toString().toInt()
        binding.ivStatus.isVisible = true
        binding.ivStatus.setImageResource(if (isValid) R.drawable.feature_profile_ic_tick_green else com.jar.app.core_ui.R.drawable.ic_close_filled)
        binding.clAge.setBackgroundResource(if (isValid) R.drawable.feature_profile_round_dark_bg_16dp else R.drawable.feature_profile_bg_error_name)
        binding.tvError.isVisible = isValid.not()
        toggleMainButton(!isValid)
        if (binding.tvError.isVisible)
            analyticsHandler.postEvent(
                EventKey.ShownErrorMessage_EnterNameScreen_Onboarding,
                mapOf(EventKey.MESSAGE to binding.tvError.text.toString())
            )
    }

    private fun resetInputStatus() {
        binding.ivStatus.isVisible = false
        binding.tvError.isVisible = false
        binding.clAge.setBackgroundResource(R.drawable.feature_profile_round_dark_bg_16dp)
        toggleMainButton()
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false else (
                isNetworkAvailable
                        && viewModel.currentlySelected != null
                        && viewModel.currentlySelected!! > 17
                        && viewModel.currentlySelected!! < 100)
        binding.btnSave.isEnabled = shouldEnable
    }

    private fun getData() {
        viewModel.fetchAgeList()
    }
}