package com.jar.app.feature_daily_investment.impl.ui.ds_setup_state

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.mask
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentDsSuccessStateBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class DSSuccessStateFragment : BaseFragment<FeatureDailyInvestmentFragmentDsSuccessStateBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentDsSuccessStateBinding
        get() = FeatureDailyInvestmentFragmentDsSuccessStateBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: DSSuccessStateFragmentArgs by navArgs()
    private val viewModel: DSSuccessStateViewModel by viewModels()
    private var currentDSAmount = 0
    private val labelAndValueAdapter = LabelAndValueAdapter()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 9.dp)

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        viewModel.fetchUserDailySavingsDetails()
        EventBus.getDefault().post(RefreshDailySavingEvent())
    }

    private fun setupListener() {
        binding.btnDSSetupAction.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to currentDSAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.TrackMySavings,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.SUCCESS.name
                )
            )
            EventBus.getDefault()
                .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.POST_SETUP_DETAILS))
            popBackStack(id = R.id.dSSuccessStateFragment, inclusive = true)
        }

        binding.btnGoToHomePage.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to currentDSAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.GoToHome,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.SUCCESS.name
                )
            )
            EventBus.getDefault().post(GoToHomeEvent(DSSuccessStateFragment::javaClass.name))
        }
    }

    private fun observeLiveData() {
        viewModel.dailySavingsDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.tvTitle.text =
                    getString(R.string.feature_daily_investment_setting_yay_daily_saving_updated_successfully)
                binding.successLottie.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.SMALL_CHECK
                )
                binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                )
                binding.lottieCelebration.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) {
                        binding.lottieCelebration.isVisible = false
                    }
                })
                createListAndSetAdapterData(it)
            },
            onError = { dismissProgressBar() }
        )
    }

    private fun createListAndSetAdapterData(userSavingsDetails: UserSavingsDetails) {
        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvDetails.adapter = labelAndValueAdapter

        userSavingsDetails.bankLogo?.let {
            binding.ivBankLogo.isVisible = true
            Glide.with(binding.root).load(it).into(binding.ivBankLogo)
        }
        userSavingsDetails.bankName?.let {
            binding.tvBankName.isVisible = true
            binding.tvBankName.text = it
        }

        binding.tvBankAccount.isVisible =
            userSavingsDetails.bankLogo.isNullOrEmpty()
                .not() || userSavingsDetails.bankName.isNullOrEmpty().not()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        val localStartDate =
            Instant.ofEpochMilli(userSavingsDetails.updateDate ?: 0)
                .atOffset(ZoneOffset.UTC)
        val list = ArrayList<LabelAndValue>()

        if (userSavingsDetails.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                    userSavingsDetails.provider.orEmpty(),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )

        if (userSavingsDetails.upiId.isNullOrEmpty())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_subscription_id),
                    userSavingsDetails.subscriptionId.orEmpty().mask(7, 5),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        else
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                    userSavingsDetails.upiId.orEmpty(),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )


        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_per_day_limit),
                getString(
                    com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                    userSavingsDetails.subscriptionAmount.toInt()
                ),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_frequency),
                getString(R.string.feature_daily_investment_daily),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_start_date),
                localStartDate.format(formatter),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        labelAndValueAdapter.submitList(list)
    }
}