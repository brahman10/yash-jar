package com.jar.app.feature_homepage.impl.ui.homepage

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.LocalContact
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchGoldSavingUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.ClaimBonusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.DismissUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchAppWalkthroughUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchBottomNavStickyCardDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchDailySavingsCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureViewUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinHomeScreenDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedImagesUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomePageExperimentsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeScreenBottomSheetPromptUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchInAppReviewStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchPartnerBannerUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchQuickActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateDailySavingAmountInfoUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchVibaCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateAppWalkthroughCompletedUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateLockerViewShownUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateUserInteractionUseCase
import com.jar.app.feature_homepage.shared.ui.HomeFragmentViewModel
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsMetaDataUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchDetectedSpendInfoUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserMetaUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import contacts.core.Contacts
import contacts.core.util.phoneList
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeFragmentViewModelAndroid @Inject constructor(
    private val groupsUseCase: FetchGroupListUseCase,
    private val claimBonusUseCase: ClaimBonusUseCase,
    private val addContactsUseCase: AddContactsUseCase,
    private val fetchUserMetaUseCase: FetchUserMetaUseCase,
    private val fetchGoldSavingUseCase: FetchGoldSavingUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val fetchHelpVideosUseCase: FetchHelpVideosUseCase,
    private val fetchFeatureViewUseCase: FetchFeatureViewUseCase,
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchQuickActionsUseCase: FetchQuickActionsUseCase,
    private val fetchSpinsMetaDataUseCase: FetchSpinsMetaDataUseCase,
    private val fetchPartnerBannerUseCase: FetchPartnerBannerUseCase,
    private val fetchVibaHorizontalCardUseCase: FetchVibaCardUseCase,
    private val fetchHomeFeedImagesUseCase: FetchHomeFeedImagesUseCase,
    private val fetchPendingInvitesUseCase: FetchPendingInvitesUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchIsSavingPausedUseCase: FetchIsSavingPausedUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchHomeFeedActionsUseCase: FetchHomeFeedActionsUseCase,
    private val fetchDailySavingsCardUseCase: FetchDailySavingsCardUseCase,
    private val updateUserInteractionUseCase: UpdateUserInteractionUseCase,
    private val updateLockerViewShownUseCase: UpdateLockerViewShownUseCase,
    private val fetchInAppReviewStatusUseCase: FetchInAppReviewStatusUseCase,
    private val fetchDetectedSpendInfoUseCase: FetchDetectedSpendInfoUseCase,
    private val fetchHomePageExperimentsUseCase: FetchHomePageExperimentsUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchDailyInvestmentStatusUseCase: FetchDailyInvestmentStatusUseCase,
    private val fetchUpcomingPreNotificationUseCase: FetchUpcomingPreNotificationUseCase,
    private val fetchFirstCoinHomeScreenDataUseCase: FetchFirstCoinHomeScreenDataUseCase,
    private val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val dismissUpcomingPreNotificationUseCase: DismissUpcomingPreNotificationUseCase,
    private val fetchUpdateDailySavingAmountInfoUseCase: FetchUpdateDailySavingAmountInfoUseCase,
    private val fetchHomeScreenBottomSheetPromptUseCase: FetchHomeScreenBottomSheetPromptUseCase,
    private val fetchAppWalkthroughUseCase: FetchAppWalkthroughUseCase,
    private val updateAppWalkthroughCompletedUseCase: UpdateAppWalkthroughCompletedUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchBottomNavStickyCardDataUseCase: FetchBottomNavStickyCardDataUseCase,
    private val prefs: PrefsApi,
    private val remoteConfigApi: RemoteConfigApi,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsApi: AnalyticsApi,
    private val mApp: Application,
) : AndroidViewModel(mApp) {

    var scrollState: Parcelable? = null

    private val viewModel by lazy {
        HomeFragmentViewModel(
            groupsUseCase,
            claimBonusUseCase,
            fetchUserMetaUseCase,
            fetchGoldSavingUseCase,
            fetchCouponCodeUseCase,
            fetchHelpVideosUseCase,
            fetchFeatureViewUseCase,
            fetchKycProgressUseCase,
            fetchContactListUseCase,
            fetchQuickActionsUseCase,
            fetchSpinsMetaDataUseCase,
            fetchPartnerBannerUseCase,
            fetchVibaHorizontalCardUseCase,
            fetchHomeFeedImagesUseCase,
            fetchPendingInvitesUseCase,
            fetchGoldSipDetailsUseCase,
            fetchIsSavingPausedUseCase,
            fetchUserGoldBalanceUseCase,
            fetchHomeFeedActionsUseCase,
            fetchDailySavingsCardUseCase,
            updateUserInteractionUseCase,
            updateLockerViewShownUseCase,
            fetchInAppReviewStatusUseCase,
            fetchDetectedSpendInfoUseCase,
            fetchHomePageExperimentsUseCase,
            isAutoInvestResetRequiredUseCase,
            fetchDailyInvestmentStatusUseCase,
            fetchUpcomingPreNotificationUseCase,
            fetchFirstCoinHomeScreenDataUseCase,
            fetchWeeklyChallengeMetaDataUseCase,
            dismissUpcomingPreNotificationUseCase,
            fetchUpdateDailySavingAmountInfoUseCase,
            fetchHomeScreenBottomSheetPromptUseCase,
            fetchAppWalkthroughUseCase,
            updateAppWalkthroughCompletedUseCase,
            prefs = prefs,
            remoteConfigApi = remoteConfigApi,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope,
            buyGoldUseCase = buyGoldUseCase,
            fetchCurrentGoldPriceUseCase = fetchCurrentGoldPriceUseCase,
            fetchBottomNavStickyCardDataUseCase =  fetchBottomNavStickyCardDataUseCase
        )
    }

    fun getInstance() = viewModel


    fun fetchLocalContactsAndUploadToServer() {
        viewModelScope.launch(dispatcherProvider.io) {
            val finalList = ArrayList<LocalContact>()
            val contacts = Contacts(mApp.applicationContext).query().find()
            contacts.forEach { contact ->
                if (contact.hasPhoneNumber.orFalse()) {
                    contact.phoneList().forEachIndexed { index, phone ->
                        if (!phone.normalizedNumber.isNullOrBlank()) {
                            try {
                                val numberData = phoneNumberUtil.parse(
                                    phone.normalizedNumber,
                                    BaseConstants.REGION_CODE
                                )

                                if (numberData.nationalNumber.toString().length == 10 && numberData.countryCode == 91) {
                                    val contactData =
                                        LocalContact(
                                            name = contact.displayNamePrimary ?: "Unknown",
                                            countryCode = numberData.countryCode.toString(),
                                            phoneNumber = numberData.nationalNumber.toString()

                                        )
                                    finalList.add(contactData)
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }
                }
            }

            addContactsUseCase.addContacts(
                AddContactRequest(
                    finalList
                )
            ).collectLatest {

            }
        }
    }

}