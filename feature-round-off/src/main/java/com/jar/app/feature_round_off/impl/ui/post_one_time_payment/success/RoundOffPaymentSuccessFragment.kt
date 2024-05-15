package com.jar.app.feature_round_off.impl.ui.post_one_time_payment.success

import android.animation.Animator
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentPaymentSuccessBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffPaymentSuccessFragment :
    BaseFragment<FeatureRoundOffFragmentPaymentSuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentPaymentSuccessBinding
        get() = FeatureRoundOffFragmentPaymentSuccessBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var serializer: Serializer

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 10.dp, escapeEdges = false)

    private val labelAndValueAdapter = LabelAndValueAdapter()

    private val args: RoundOffPaymentSuccessFragmentArgs by navArgs()

    private val fetchManualPaymentStatusResponse by lazy {
        serializer.decodeFromString<FetchManualPaymentStatusResponse>(decodeUrl(args.fetchManualPaymentStatusResponse))
    }
    companion object {
        const val RoundOffPaymentSuccessFragment = "RoundOffPaymentSuccessFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        dismissProgressBar()
        binding.tvTitle.text = getCustomStringFormatted(
            MR.strings.feature_round_off_first_payment_of_x_successful,
            fetchManualPaymentStatusResponse.amount.orZero().toInt()
        )

        binding.tvDownloadInvoice.isVisible =
            fetchManualPaymentStatusResponse.oneTimeInvestOrderDetails?.invoiceLink.isNullOrEmpty()
                .not()

        binding.tvDownloadInvoice.movementMethod = LinkMovementMethod()

        binding.tvTransactionId.text =
            fetchManualPaymentStatusResponse.transactionId.orEmpty().mask(7, 5)
        setDetailsAdapterAndPopulateIt()
        animateLottie()
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_SuccessScreen,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        )
    }

    private fun setupListener() {
        binding.tvTransactionId.setDebounceClickListener {
            requireContext().copyToClipboard(
                fetchManualPaymentStatusResponse.transactionId.orEmpty(),
                getString(com.jar.app.core_ui.R.string.copied)
            )
        }

        binding.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_SuccessScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.GoToHomeClicked)
            )
            EventBus.getDefault().post(GoToHomeEvent(RoundOffPaymentSuccessFragment))
        }

        binding.btnAutomateNow.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_SuccessScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomateNowClicked)
            )
            navigateTo(NavigationRoundOffDirections.actionToPreRoundOffAutopaySetupFragment())
        }

        binding.tvDownloadInvoice.setOnClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.ManualRoundoff_SuccessScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.DownloadInvoiceClicked)
            )
            fetchManualPaymentStatusResponse.oneTimeInvestOrderDetails?.invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
            }
        }
    }

    private fun setDetailsAdapterAndPopulateIt() {

        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvDetails.adapter = labelAndValueAdapter

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val localStartDate =
            Instant.ofEpochMilli(fetchManualPaymentStatusResponse.transactionDate ?: 0)
                .atOffset(ZoneOffset.UTC)
        val list = ArrayList<LabelAndValue>()

        list.add(
            LabelAndValue(
                getCustomString(MR.strings.feature_round_off_saved_amount),
                fetchManualPaymentStatusResponse.amount.orZero().toString()
            )
        )

        list.add(
            LabelAndValue(
                getCustomString(MR.strings.feature_round_off_in_gold),
                fetchManualPaymentStatusResponse.oneTimeInvestOrderDetails?.goldVolume.orEmpty()
            )
        )

        list.add(
            LabelAndValue(
                getCustomString(MR.strings.feature_round_off_saved_for),
                localStartDate.format(formatter)
            )
        )
        labelAndValueAdapter.submitList(list)
    }

    private fun animateLottie() {
        binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
        )
        binding.successLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.RUPEE_POST_PURCHASE_SUCCESS
        )
        binding.lottieCelebration.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                binding.lottieCelebration.isVisible = false
            }
        })
    }
}