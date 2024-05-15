package com.jar.app.feature_promo_code.impl.ui.promo_transaction_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieDrawable
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.isAlreadyInflated
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FeaturePendingFailureV2Binding
import com.jar.app.feature_buy_gold_v2.databinding.FeatureSuccessTransactionV2Binding
import com.jar.app.feature_buy_gold_v2.databinding.FragmentOrderStatusV2Binding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionStatus
import com.jar.app.feature_promo_code.shared.domain.event.PromoCodeEvents
import com.jar.app.feature_promo_code.shared.domain.event.PromoCodeEvents.Clicked_ViewInvoice_PromoSuccessScreen
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class PromoCodeStatusFragment : BaseFragment<FragmentOrderStatusV2Binding>() {


    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val viewModelProvider by viewModels<PromoCodeStatusViewModelAndroid> { defaultViewModelProviderFactory }

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var successBinding: FeatureSuccessTransactionV2Binding? = null
    private var failureBinding: FeaturePendingFailureV2Binding? = null

    private val args by navArgs<PromoCodeStatusFragmentArgs>()

    private val orderId by lazy {
        args.orderId
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack()
            }
        }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOrderStatusV2Binding
        get() = FragmentOrderStatusV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }


    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        getData()
        observeFlows()
    }

    private fun observeFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.promoCodeTransactionResponseFlow.collect(
                    onLoading = {
                        showProgressBar()

                    },
                    onSuccess = { response ->
                        dismissProgressBar()
                        response?.let {
                            renderTransactionStatus(it)
                        }

                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun renderTransactionStatus(promoCodeTransactionResponse: PromoCodeTransactionResponse) {
        when (promoCodeTransactionResponse.getTransactionStatus()) {
            PromoCodeTransactionStatus.SUCCESS -> setOrderSuccessView(promoCodeTransactionResponse)
            PromoCodeTransactionStatus.PENDING -> setOrderProcessingView(
                promoCodeTransactionResponse
            )
        }
    }

    private fun setOrderSuccessView(promoCodeTransactionResponse: PromoCodeTransactionResponse) {
        with(binding) {
            // check if it is a refresh result, we can do it by checking pending view is inflared or not
            if (failureBinding != null) {
                failureBinding?.root?.visibility = View.GONE
            }
            if (successScreen.isAlreadyInflated().not()) {
                val view = successScreen.inflate()
                successBinding = FeatureSuccessTransactionV2Binding.bind(view)
                successBinding?.apply {
                    downloadIn.paint.isUnderlineText = true

                    lineSeprator.isVisible = false
                    yourReward.isVisible = false
                    rewardLL.isVisible = false
                    successLottie.apply {
                        repeatCount = LottieDrawable.INFINITE
                        playLottieWithUrlAndExceptionHandling(
                            requireContext(),
                            BaseConstants.LottieUrls.SMALL_CHECK
                        )
                    }

                    animConfetti.apply {
                        playLottieWithUrlAndExceptionHandling(
                            requireContext(),
                            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                        )
                    }

                    val amountGramString = getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_amount_and_quantity,
                        promoCodeTransactionResponse.volume.orZero(),
                        promoCodeTransactionResponse.amount.orZero()
                    )

                    successBinding?.gm?.text = amountGramString

                    setSuccessListeners(promoCodeTransactionResponse)
                }
            }
        }
    }

    private fun FeatureSuccessTransactionV2Binding.setSuccessListeners(
        promoCodeTransactionResponse: PromoCodeTransactionResponse
    ) {
        //setting up the download invoice
        downloadView.setDebounceClickListener {
            viewModel.postAnalyticsEvent(Clicked_ViewInvoice_PromoSuccessScreen)
            promoCodeTransactionResponse.invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
            }
        }

        btnGoToHome.setDebounceClickListener {
            handleBackPress()
        }
    }

    private fun setOrderProcessingView(promoCodeTransactionResponse: PromoCodeTransactionResponse) {
        with(binding) {
            if (nonSuccessScreen.isAlreadyInflated().not()) {
                val view = nonSuccessScreen.inflate()
                failureBinding = FeaturePendingFailureV2Binding.bind(view)
                failureBinding?.apply {
                    viewModel.postAnalyticsEvent(PromoCodeEvents.Shown_PromoProcessingScreen)
                    buttonRefreshOrTryAgain.setText(
                        getCustomString(MR.strings.feature_buy_gold_refresh)
                    )
                    tvContactUs.paint.isUnderlineText = true
                    itIsTakin.text =
                        getCustomString(MR.strings.feature_buy_gold_v2_processing_reason_sub_message)

                    successLottie.setImageResource(R.drawable.feature_buy_gold_v2_ic_payment_processing)
                    val amount = promoCodeTransactionResponse.amount.orZero()
                    val amountGramString = getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_we_are_trying_message, amount.toFloat()
                    )
                    weAreTryi.text = amountGramString

                    setProcessingViewListener()
                }
            }
        }
    }

    private fun FeaturePendingFailureV2Binding.setProcessingViewListener() {
        btnGoToHome.setDebounceClickListener {
            handleBackPress()
        }

        buttonRefreshOrTryAgain.setDebounceClickListener {
            viewModel.postAnalyticsEvent(PromoCodeEvents.Clicked_Refresh_PromoProcessingScreen)
            getData()
        }

        contactSup.setDebounceClickListener {
            contactUs()
        }
    }


    private fun handleBackPress() {
        popBackStack()
    }

    private fun contactUs() {
        viewModel.postAnalyticsEvent(PromoCodeEvents.Clicked_ContactUs_PromoProcessingScreen)
        val number = remoteConfigManager.getWhatsappNumber()
        requireContext().openWhatsapp(
            number,
            getCustomStringFormatted(
                MR.strings.feature_buy_gold_v2_im_having_issues_buying_gold_for_x_transactionId,
                orderId
            )
        )
    }

    private fun getData() {
        viewModel.fetchPromoCodeTransactionStatus(orderId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        successBinding = null
        failureBinding = null

    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }
}