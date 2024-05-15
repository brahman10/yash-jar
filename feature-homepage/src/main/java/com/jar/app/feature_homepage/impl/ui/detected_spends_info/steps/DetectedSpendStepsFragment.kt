package com.jar.app.feature_homepage.impl.ui.detected_spends_info.steps

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.core_ui.item_decoration.TextIconItemDecoration
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageFragmentDetectedSpendsPaymentInfoStepBinding
import com.jar.app.feature_homepage.shared.domain.event.detected_spends.ManualPaymentStepsShownEvent
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendPaymentInfoStep
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DetectedSpendStepsFragment :
    BaseBottomSheetDialogFragment<FeatureHomepageFragmentDetectedSpendsPaymentInfoStepBinding>(),
    BaseItemDecoration.SectionCallback {

    @Inject
    lateinit var serializer: Serializer

    private val viewModel by viewModels<DetectedSpendStepsFragmentViewModel> { defaultViewModelProviderFactory }
    private var controller: DetectedSpendStepEpoxyController? = null
    private var animation: ObjectAnimator? = null
    private val textIconItemDecoration = TextIconItemDecoration(this)

    private val args by navArgs<DetectedSpendStepsFragmentArgs>()

    private val initiateManualPaymentRequest by lazy {
        serializer.decodeFromString<com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest>(
            decodeUrl(args.initiateManualPaymentRequest)
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFragmentDetectedSpendsPaymentInfoStepBinding
        get() = FeatureHomepageFragmentDetectedSpendsPaymentInfoStepBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            shouldShowFullHeight = true,
            isCancellable = false,
            isDraggable = false
        )

    override fun setup() {
        setupUI()
        getData()
        observeLiveData()
    }

    private fun setupUI() {
        binding.epoxyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.epoxyRecyclerView.addItemDecorationIfNoneAdded(textIconItemDecoration)
        controller = DetectedSpendStepEpoxyController()
        binding.epoxyRecyclerView.setController(controller!!)

        animation = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100)
        animation?.duration = 2500
        animation?.interpolator = LinearInterpolator()

        animation?.doOnEnd {
            EventBus.getDefault()
                .postSticky(
                    com.jar.app.feature_homepage.shared.domain.event.detected_spends.ManualPaymentStepsShownEvent(
                        initiateManualPaymentRequest
                    )
                )
            dismissAllowingStateLoss()
        }

        animation?.start()
    }

    private fun getData() {
        viewModel.getSteps(WeakReference(context))
    }

    private fun observeLiveData() {
        viewModel.stepsLiveData.observe(viewLifecycleOwner) {
            val list = ArrayList<DetectedSpendPaymentInfoStep>()
            list.addAll(controller?.steps.orEmpty())
            list.add(it)
            controller?.steps = list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animation?.cancel()
    }

    override fun isItemDecorationSection(position: Int): Boolean {
        return true
    }

    override fun getItemDecorationLayoutRes(position: Int): Int {
        return R.layout.feature_homepage_cell_header_arrow_text
    }

    override fun bindItemDecorationData(view: View, position: Int) {
        val ivIcon = view.findViewById<AppCompatImageView>(R.id.ivIcon)
        ivIcon?.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down_2)
        ivIcon?.isVisible = position != 0

        val tvHeader = view.findViewById<AppCompatTextView>(R.id.tvHeader)
        tvHeader?.text = getString(R.string.feature_homepage_what_happens_next)
        tvHeader?.isVisible = position == 0
    }
}