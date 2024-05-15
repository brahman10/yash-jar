package com.jar.app.feature_profile.impl.ui.profile.number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_analytics.EventKey.LogoutEditProfileCelebration
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_network.event.LogoutEvent
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_profile.databinding.FragmentEditProfileCelebrationBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileCelebrationFragment : BaseFragment<FragmentEditProfileCelebrationBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var timerJob: Job? = null

    private val args by navArgs<EditProfileCelebrationFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileCelebrationBinding
        get() = FragmentEditProfileCelebrationBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fireLogoutEvent()
                isEnabled = false
            }
        }

    companion object {
        private const val SCREEN_TIMER = 3000L
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupClickListener()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_PhoneNumberPopUp)
    }

    private fun setupUI() {
        binding.animationView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
        val durationInMillis = Duration.ofMillis(SCREEN_TIMER).toMillis()

        binding.tvTitle.text = args.title
        binding.tvSubTitle.text = args.subTitle

        timerJob?.cancel()

        timerJob = uiScope.countDownTimer(
            durationInMillis,
            onFinished = {
                fireLogoutEvent()
            }
        )
    }

    private fun fireLogoutEvent() {
        EventBus.getDefault().post(LogoutEvent(flowContext = LogoutEditProfileCelebration, message = args.subTitle))
    }

    private fun setupClickListener() {}

    override fun onDestroyView() {
        backPressCallback.isEnabled = true
        super.onDestroyView()
    }
}