package com.jar.app.feature_lending.impl.ui.downtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentPartnerDownTimeBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.PartnerDownTimeData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class PartnerDownTimeFragment : BaseFragment<FragmentPartnerDownTimeBinding>() {
    @Inject
    lateinit var serializer: Serializer
    private val arguments by navArgs<PartnerDownTimeFragmentArgs>()

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val viewModelProvider by viewModels<PartnerDownTimeViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPartnerDownTimeBinding
        get() = FragmentPartnerDownTimeBinding::inflate

    override fun setupAppBar() {
        binding.lendingToolbar.tvTitle.text =
            getCustomString(MR.strings.feature_lending_back_to_home_page)
        binding.lendingToolbar.btnNeedHelp.isVisible = false
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            EventBus.getDefault().post(GoToHomeEvent("PARTNER_DOWNTIME_SCREEN"))
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        setUpListeners()
        observeLiveData()
        getData()
    }

    private fun setUpListeners() {
        binding.btnAction.setDebounceClickListener {
            if (viewModel.staticContentFlow.asLiveData().value?.data?.data?.downTime?.isNotificationEnabled.orFalse()) {
                viewModel.updateNotifyUsers()
            }
        }
        binding.btnContactUs.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomStringFormatted(MR.strings.feature_lending_under_maintenance_contact_us,
                    prefs.getUserName().orEmpty(),
                    prefs.getUserPhoneNumber().orEmpty()
                )
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.downTime?.let { downTimeData ->
                            setDownTimeData(downTimeData)
                        }
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                    })
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.notifyUserFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()

                        binding.btnAction.isVisible = false
                        binding.llNotified.isVisible = true
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()

                        binding.btnAction.isVisible = false
                        binding.llNotified.isVisible = true
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    })
            }
        }
    }

    private fun setDownTimeData(data: PartnerDownTimeData) {
        binding.tvNotified.text = data.notificationDesc
        data.icon?.let {
            Glide.with(requireContext()).load(it).into(binding.ivIllustration)
        }
        binding.tvScreenTitle.text = data.title

        uiScope.countDownTimer(data.countDownTimeInMillis ?: 0, 1000,
            onInterval = {
                binding.tvTimer.text =
                    it.milliSecondsToCountDown(showZero = true).replace(":", " : ")
            },
            onFinished = {
                EventBus.getDefault().post(GoToHomeEvent("LENDING_DOWN_TIME"))
            })

        binding.tvDescription.text = data.description

        binding.btnAction.isVisible = data.isNotificationEnabled.orFalse()
    }

    private fun getData() {
        viewModel.fetchStaticContentForDownTime(args.loanId.orEmpty())
    }
}