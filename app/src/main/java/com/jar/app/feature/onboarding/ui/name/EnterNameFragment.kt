package com.jar.app.feature.onboarding.ui.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.areAllCharsSame
import com.jar.app.base.util.hasMoreSpacesThanAlphabets
import com.jar.app.base.util.hasMoreThanXRepeatingChars
import com.jar.app.base.util.hideKeyboard
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.databinding.FragmentNewEnterNameBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_profile.shared.MR
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterNameFragment : BaseFragment<FragmentNewEnterNameBinding>() {

    @Inject
    lateinit var onboardingStateMachine: com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    private val newOnboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        newOnboardingViewModelProvider.getInstance()
    }

    private val timeInit = System.currentTimeMillis()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewEnterNameBinding
        get() = FragmentNewEnterNameBinding::inflate

    private var startTime: Long? = null

    private var isNetworkAvailable = true

    private val homeViewModel: HomeActivityViewModel by activityViewModels()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        // Need to access newOnboardingViewModel here first..
        // If we directly access in onDestroy() then we are getting below error"
        // Error -> {SavedStateProvider with the given key is already registered}
        // https://console.firebase.google.com/u/1/project/changejarprod/crashlytics/app/android:com.jar.app/issues/0cf3e608eb098b37ca379b234cc2966b?time=last-seven-days&versions=5.5.0%20(340)&sessionEventKey=640A8D4B004F000141B28A77C81C0756_1787175236159059635
        newOnboardingViewModel.timeSpentMap

        startTime = System.currentTimeMillis()
        setupUI()
        setUpListeners()
        observeLiveData()
        analyticsApi.postEvent(EventKey.Shown_EnterNameScreen_Onboarding)
    }

    private fun setupUI() {
        binding.etFirstName.showKeyboard()
        binding.btnNext.setDisabled(binding.etFirstName.text.toString().isEmpty())
    }

    private fun setUpListeners() {
        binding.etFirstName.doAfterTextChanged {
            binding.btnNext.setDisabled(it.toString().isEmpty())
            resetInputStatus()
        }
        binding.btnNext.setDebounceClickListener {
            it.context.hideKeyboard(it)
            val firstName = binding.etFirstName.text?.toString() ?: ""
            if (checkForErrorStatesForFirstName()) {
                newOnboardingViewModel.updateName(
                    firstName = firstName.trim(),
                    lastName = ""
                )
                prefsApi.setUserName(firstName)
                onboardingStateMachine.navigateAhead()
            }
            analyticsApi.postEvent(
                EventKey.ClickedNext_EnterNameScreen_Onboarding,
                mapOf(EventKey.NAME to binding.etFirstName.text.toString())
            )
        }

        view?.setOnTouchListener { view, _ ->
            view.hideKeyboard()
            false
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    isNetworkAvailable = it
                }
            }
        }
        userLiveData.observe(viewLifecycleOwner) {
            val firstName = it?.firstName
            val lastName = it?.lastName
            if (firstName != null) {
                binding.etFirstName.setText(firstName)
                binding.etFirstName.setSelection(binding.etFirstName.text?.length.orZero())
                setInputStatus(verifyName(firstName))
            }
        }
    }

    private fun verifyName(name: CharSequence): Boolean {
        return when {
            name.length < 3 -> {
                binding.tvError.text = getString(R.string.name_seems_very_unique)
                false
            }

            name.hasMoreSpacesThanAlphabets() -> {
                binding.tvError.text = getString(R.string.name_seems_very_unique)
                false
            }

            name.toString().lowercase().areAllCharsSame() -> {
                binding.tvError.text = getString(R.string.enter_something_real)
                false
            }

            name.toString().lowercase().hasMoreThanXRepeatingChars() -> {
                binding.tvError.text = getString(R.string.enter_something_real)
                false
            }

            else -> true
        }
    }

    private fun setInputStatus(isValid: Boolean) {
        binding.clName.setBackgroundResource(if (isValid) R.drawable.bg_rounded_272239_20dp else R.drawable.bg_error_pin_code)
        binding.tvError.isVisible = isValid.not()
        if (binding.tvError.isVisible)
            analyticsApi.postEvent(
                EventKey.ShownErrorMessage_EnterNameScreen_Onboarding,
                mapOf(EventKey.MESSAGE to binding.tvError.text.toString())
            )
    }

    private fun resetInputStatus() {
        binding.tvError.isVisible = false
        binding.clName.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.bg_rounded_272239_20dp
        )
    }

    override fun onDestroyView() {
        analyticsApi.postEvent(
            EventKey.Exit_EnterNameScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        val endTime = System.currentTimeMillis()
        newOnboardingViewModel.updateScreenTime(
            screenName = com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = startTime.orZero().minus(endTime)
        )
        super.onDestroyView()
    }

    private fun checkForErrorStatesForFirstName(): Boolean {
        val firstName = binding.etFirstName.text?.toString()
        if (firstName.isNullOrBlank()) {
            resetInputStatus()
            return false
        } else if (firstName.length.orZero() > 30) {
            binding.tvError.text =
                getCustomString(MR.strings.feature_profile_name_cant_be_more_than_30)
            setInputStatus(isValid = false)
            if (firstName.length.orZero() > 30) {
                val trimmedText = firstName.substring(0, 30)
                binding.etFirstName.setText(trimmedText)
                binding.etFirstName.setSelection(trimmedText.length.orZero())
            }
            return false
        } else {
            setInputStatus(verifyName(firstName))
            return verifyName(firstName)
        }
    }

}