package com.jar.app.feature_lending.impl.ui.host_container.back_pressed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingBackPressedBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class LendingBackPressedBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentLendingBackPressedBinding>(){

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<LendingBackPressedBottomSheetFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingBackPressedBinding
        get() = FragmentLendingBackPressedBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    override fun setup() {
        analyticsHandler.postEvent(
            LendingEventKey.Lending_CROSS_BOTTOMSHEET_SHOWN,
            mapOf(
                LendingEventKeyV2.screen_name to args.currentScreenName
            )
        )
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text = getCustomString(MR.strings.feature_lending_are_you_sure_you_want_to_leave)
//        binding.tvDescription.text = args.des
    }

    private fun initClickListeners() {
        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingEventKey.Lending_CrossBSCancelClicked,
                mapOf(
                    LendingEventKeyV2.screen_name to args.currentScreenName
                )
            )
            dismissAllowingStateLoss()
        }

        binding.btnDoItLater.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingEventKeyV2.Lending_I_WillDoItLaterButtonClicked,
                mapOf(
                    LendingEventKeyV2.screen_name to args.currentScreenName
                )
            )
            EventBus.getDefault().post(GoToHomeEvent("Lending"))
        }
    }
}