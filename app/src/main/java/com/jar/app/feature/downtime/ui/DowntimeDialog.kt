package com.jar.app.feature.downtime.ui

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.R
import com.jar.app.base.data.event.RecreateAppEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.databinding.FragmentDialogDowntimeBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class DowntimeDialog : BaseBottomSheetDialogFragment<FragmentDialogDowntimeBinding>() {

    private val args by navArgs<DowntimeDialogArgs>()
    private var timerFinished = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogDowntimeBinding
        get() = FragmentDialogDowntimeBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = false,
        isDraggable = false
    )

    override fun setup() {
        init()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnOk.setDebounceClickListener {
            dismiss()
        }
    }

    private fun init(){
        startTimer(args.timeLeftTillDownTimeEndsInMillis)
    }

    private fun startTimer(timeLeftInMillis: Long) {
        uiScope.countDownTimer(
            timeLeftInMillis,
            onInterval = {
                binding.textBackIn.text = getString(
                    R.string.it_will_be_back_in,
                    it.milliSecondsToCountDown()
                )
            },
            onFinished = {
                timerFinished = true
            })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (timerFinished)
            EventBus.getDefault().post(RecreateAppEvent())
        else
            requireActivity().finishAffinity()
    }

}
