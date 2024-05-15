package com.jar.android.feature_post_setup.impl.ui.setup_details

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.android.feature_post_setup.databinding.DsChangeProgressScreenBinding
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_post_setup.shared.PostSetupMR
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Duration
import java.util.Calendar

@AndroidEntryPoint
internal class DailySavingPauseProgressScreen : BaseBottomSheetDialogFragment<DsChangeProgressScreenBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DsChangeProgressScreenBinding
        get() = DsChangeProgressScreenBinding::inflate

    private var animation: ObjectAnimator? = null

    private val args: DailySavingPauseProgressScreenArgs by navArgs()

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            shouldShowFullHeight = true,
            isCancellable = false,
            isDraggable = false,
        )
    override fun setup() {
        setLinearProgressWithAnimation()
        setupUi()
    }

    private fun setupUi() {
        binding.ivHeaderImage.setImageResource(returnImageResourceForProgressScreen(args))
        binding.tvHeading.text = headingText(args.heading.toInt())
        binding.tvSubHeading.text = subHeadingText(args.heading.toInt())
    }

    private fun setLinearProgressWithAnimation() {
        val durationInMillis = Duration.ofSeconds(3).toMillis()
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

    private fun returnImageResourceForProgressScreen(args: DailySavingPauseProgressScreenArgs): Int {
        return (if (args.status == CalendarUtil.DsStatus.PAUSE.name) {
            com.jar.app.core_ui.R.drawable.core_ui_stop_watch_stop
        } else {
            com.jar.app.core_ui.R.drawable.core_ui_stop_watch_stop
        })
    }

    private fun headingText(numberOfDays: Int): String {
        return if (numberOfDays != 1000) {
            getCustomString(PostSetupMR.strings.savings_stopped) + getPauseSavingData(args.heading.toInt())
        } else {
            getCustomString(PostSetupMR.strings.savings_stopped_permanently)
        }
    }

    private fun subHeadingText(numberOfDays: Int): String {
        return if (numberOfDays != 1000) {
            getCustomString(PostSetupMR.strings.your_savings_will_resume_on) + convertDaysDifferenceToDate(args.heading.toInt())
        } else {
            getCustomString(PostSetupMR.strings.you_can_restart_when_you_need)
        }
    }

    private fun getPauseSavingData(numberOfDays: Int): String {
        return when (numberOfDays) {
            1 -> getCustomString(PostSetupMR.strings.till_tomorrow)
            7 -> {
                getCustomString(PostSetupMR.strings.for_1_week)
            }
            else -> {
                getCustomString(PostSetupMR.strings.for_2_week)
            }
        }
    }

    private fun convertDaysDifferenceToDate(numberOfDays: Int): String {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)

        return " " + calendar.time.getFormattedDate("dd MMM''yy")
    }
}