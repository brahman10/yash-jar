package com.jar.app.feature_gold_sip.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.api.GoldSipApi
import com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection.SipTypeSelectionScreenData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GoldSipApiImpl @Inject constructor(
    navControllerRef: Lazy<NavController>,
    private val serializer: Serializer,
    private val appScope: CoroutineScope,
    private val dispatcher: DispatcherProvider,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase
) : GoldSipApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    private var sipDetailsJob: Job? = null

    override fun setupGoldSip() {
        sipDetailsJob?.cancel()
        sipDetailsJob = appScope.launch(dispatcher.main) {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails().collect(
                onSuccess = {
                    if (it.subscriptionStatus == null)
                    //Here sending success as a status to SetupGoldSipFragment means that the flow has not been initiated yet
                        navController.navigate(
                            Uri.parse(
                                "android-app://com.jar.app/setupGoldSip/${
                                    encodeUrl(serializer.encodeToString(MandatePaymentProgressStatus.SUCCESS))
                                }"
                            ),
                            getNavOptions(true)
                        )
                    else if (it.subscriptionStatus != MandatePaymentProgressStatus.SUCCESS.name) {
                        val data =
                            it.subscriptionStatus ?: MandatePaymentProgressStatus.SUCCESS.name
                        navController.navigate(
                            Uri.parse(
                                "android-app://com.jar.app/setupGoldSip/${
                                    encodeUrl(serializer.encodeToString(data))
                                }"
                            ),
                            getNavOptions(true)
                        )
                    } else
                        openGoldSipDetails()
                }
            )
        }
    }

    override fun openGoldSipIntro() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/goldSipIntro"),
            getNavOptions(true)
        )
    }

    override fun openGoldSipDetails(isUpdateFlow: Boolean) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/goldSipDetails"),
            getNavOptions(
                true,
                popUpToId = if (isUpdateFlow) R.id.goldSipAutoPaySuccessFragment else R.id.goldSipDetailsFragment,
                true
            )
        )
    }

    override fun openGoldSipTypeSelectionScreen(sipTypeSelectionScreenData: SipTypeSelectionScreenData?) {
        navController.navigate(
            Uri.parse(
                "android-app://com.jar.app/goldSipTypeSelection/${
                    encodeUrl(
                        serializer.encodeToString(sipTypeSelectionScreenData)
                    )
                }"
            ),
            getNavOptions(true)
        )
    }
}