package com.jar.app.feature_mandate_payment_common.impl.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_mandate_payment_common.impl.util.PackageManagerUtil
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchEnabledPaymentMethodsUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandateEducationUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchPreferredBankUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_mandate_payments_common.shared.ui.payment_page.PaymentPageFragmentViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentPageFragmentViewModelAndroid @Inject constructor(
    private val verifyUpiAddressUseCase: VerifyUpiAddressUseCase,
    private val fetchPreferredBankUseCase: FetchPreferredBankUseCase,
    private val fetchMandateEducationUseCase: FetchMandateEducationUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val initiateMandatePaymentUseCase: InitiateMandatePaymentUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchEnabledPaymentMethodsUseCase: FetchEnabledPaymentMethodsUseCase,
    private val analyticsHandler: AnalyticsApi,
    private val remoteConfigApi: RemoteConfigApi,
    private val packageManagerUtil: PackageManagerUtil,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    mApp: Application
) : AndroidViewModel(mApp) {

    private val packageManager = mApp.packageManager

    private val viewModel by lazy {
        PaymentPageFragmentViewModel(
            verifyUpiAddressUseCase = verifyUpiAddressUseCase,
            fetchPreferredBankUseCase = fetchPreferredBankUseCase,
            fetchMandateEducationUseCase = fetchMandateEducationUseCase,
            initiateMandatePaymentUseCase = initiateMandatePaymentUseCase,
            fetchEnabledPaymentMethodsUseCase = fetchEnabledPaymentMethodsUseCase,
            fetchInstalledUpiApps = {
                getInstalledUPIApps()
            },
            fetchAppNameFromPackageName = { packageName ->
                packageManagerUtil.getAppNameFromPkgName(packageName)
            },
            fetchCurrentGoldPriceUseCase = fetchCurrentGoldPriceUseCase,
            fetchCouponCodeUseCase = fetchCouponCodeUseCase,
            remoteConfigApi = remoteConfigApi,
            analyticsApi = analyticsHandler,
            coroutineScope = viewModelScope,
            applyCouponUseCase = applyCouponUseCase,
            fetchExitSurveyQuestionsUseCase = fetchExitSurveyQuestionsUseCase,
            )
    }

    fun getInstance() = viewModel

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledUPIApps(): List<String> {
        val upiList = ArrayList<String>()
        val uri = Uri.parse(String.format("%s://%s", "upi", "mandate"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
            packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfoList) {
            upiList.add(resolveInfo.activityInfo.packageName)
        }

        return upiList
    }
}