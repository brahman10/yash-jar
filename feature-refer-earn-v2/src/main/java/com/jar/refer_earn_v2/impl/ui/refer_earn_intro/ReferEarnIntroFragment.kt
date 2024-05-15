package com.jar.refer_earn_v2.impl.ui.refer_earn_intro

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.jar.app.core_compose_ui.utils.sdp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.shareOnWhatsapp
import com.jar.app.core_base.util.BaseConstants.BASE_EXTERNAL_DEEPLINK
import com.jar.app.core_base.util.BaseConstants.ExternalDeepLinks.TRANSACTIONS
import com.jar.app.core_base.util.BaseConstants.Screen
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.core_utils.data.WhatsAppUtil
import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Back_clicked
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Invite_Contacts
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Invite_via_Whatsapp
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Non_Reward_Screen
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Refer_ScreenClicked
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Refer_ScreenShown
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Reward_Screen
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Share_via
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.View_Referrals
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.buttonclicked
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.refer_intro
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.screen_type
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.screenshown
import com.jar.refer_earn_v2.impl.ui.refer_earn_intro.has_winnings.RenderBottomTimelineTextViews
import com.jar.refer_earn_v2.impl.ui.refer_earn_intro.has_winnings.RenderWithRewards
import com.jar.refer_earn_v2.impl.ui.refer_earn_intro.referral_bottomsheet.RenderBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.Calendar

@AndroidEntryPoint
internal class ReferEarnIntroFragment : BaseComposeFragment() {

    private val viewModelProvider by viewModels<ReferEarnIntroViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var deviceUtils: WhatsAppUtil

    @Inject
    lateinit var contactsSync: ContactsSyncApi

    @Inject
    lateinit var appsFlyerInviteUtil: AppsFlyerInviteUtil

    private var glide: RequestManager? = null
    private var target: CustomTarget<Bitmap>? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterialApi::class)
    @Composable
    @Preview
    override fun RenderScreen() {
        val uiState = viewModel.uiStateFlow.collectAsState()
        val introScreenData =
            remember { derivedStateOf { uiState.value.introScreenData?.data?.data } }
        val coroutineScope = rememberCoroutineScope()
        val sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val renderBottomSection: (@Composable () -> Unit) = { RenderBottomSection(introScreenData) }

        ModalBottomSheetLayout(
            modifier = Modifier.navigationBarsPadding(),
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 20.sdp, topEnd = 20.sdp),
            sheetContent = {
                val items = viewModel.pagingData.collectAsLazyPagingItems()
                RenderBottomSheet(
                    items,
                    onItemClick = {
                        handleDeeplinkFunction(it)
                    },
                    introScreenData.value?.referralCount.orZero(),
                    postAnalyticsOnButtonClick = {
                        analyticsHandler.postEvent(
                            Refer_ScreenClicked,
                            buildMapForAnalytics().apply {
                                put(buttonclicked, it)
                                put(screen_type, "Refer_bottomsheet")
                            })
                    },
                    closeBottomSheetFxn = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    RightSectionClick = {
                        navigateToContactSupport()
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_292539))
            ) {
                RenderToolBar() {
                    analyticsHandler.postEvent(Refer_ScreenClicked, buildMapForAnalytics().apply {
                        put(buttonclicked, Back_clicked)
                    })
                    popBackStack()
                }
                Divider(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3).copy(
                        alpha = 0.1f
                    ), thickness = 1.sdp
                )
                if (introScreenData.value?.rewards.isNullOrEmpty()) {
                    RenderWithoutRewards(this, introScreenData, renderBottomSection)
                } else {
                    RenderWithRewards(this, introScreenData, renderBottomSection, rewardSection = {
                        analyticsHandler.postEvent(
                            Refer_ScreenClicked,
                            buildMapForAnalytics().apply {
                                put(buttonclicked, it)
                            })
                    }, viewReferralsClick = {
                        coroutineScope.launch {
                            analyticsHandler.postEvent(
                                Refer_ScreenClicked,
                                buildMapForAnalytics().apply {
                                    put(buttonclicked, View_Referrals)
                                })
                            sheetState.show()
                        }
                    })
                }
            }
        }
    }

    private fun handleDeeplinkFunction(deeplink: String) {
        // deeplink consists of the external URL in the 0th position and in the first it contains which screen it has to navigate
        // with params in the next parts
        deeplink.split("/").getOrNull(1)?.let {
            when (it) {
                "myWinnings" -> {
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            BASE_EXTERNAL_DEEPLINK + "${TRANSACTIONS}/WINNINGS"
                        ))
                }

                "offerListPage" -> {
                    navigateTo("android-app://com.jar.app/offerListPage")
                }

                "transactionDetail" -> {
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            deeplink
                        ))
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        glide?.clear(target)
        super.onDestroyView()
    }
    @Composable
    private fun RenderBottomSection(introScreenData: State<ReferIntroScreenData?>) {
        Column {
            introScreenData.value?.staticContent?.referralBreakup?.let {
                RenderVerticalTimelineTextViews(
                    modifier = Modifier.padding(horizontal = 26.sdp),
                    list = it
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (introScreenData.value != null) {
                RenderBottomTimelineTextViews(
                    remoteConfigApi.shouldHaveWABtnOnTopInRefer(),
                    shouldAnimateContactsButton = introScreenData.value?.rewards.isNullOrEmpty(),
                    inviteContactsViaWhatsapp = {
                        shareNewMessage(true)
                        analyticsHandler.postEvent(
                            Refer_ScreenClicked,
                            buildMapForAnalytics().apply {
                                put(buttonclicked, Invite_via_Whatsapp)
                            })
                    },
                    inviteContacts = {
                        contactsSync.initiateContactsSyncFlow(
                            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.REFERRALS,
                            ""
                        )
                        analyticsHandler.postEvent(
                            Refer_ScreenClicked,
                            buildMapForAnalytics().apply {
                                put(buttonclicked, Invite_Contacts)
                            })
                    }, shareVia = {
                        shareNewMessage(false)
                        analyticsHandler.postEvent(
                            Refer_ScreenClicked,
                            buildMapForAnalytics().apply {
                                put(buttonclicked, Share_via)
                            })
                    })
            }
        }
    }

    private fun shareNewMessage(shouldShareOnlyOnWA: Boolean) {
        showProgressBar()
        viewModel.uiStateFlow.value.shareMessageDetails.data?.data?.let {
            val shareMessage = if (shouldShareOnlyOnWA) it.whatsAppShareMessage.orEmpty() else it.othersShareMessage.orEmpty()
            target = object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    shareVia(resource, shouldShareOnlyOnWA, shareMessage)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    shareVia(null, shouldShareOnlyOnWA, shareMessage)
                }
            }
            Glide.with(requireContext())
                .asBitmap()
                .load(it.whatsAppShareImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(target!!) // !! because it is just set above
        }
    }

    private fun shareVia(resource: Bitmap?, shouldShareOnlyOnWA: Boolean, shareMessage: String) {
        val weakReference = WeakReference(requireContext())
        if (resource != null) {
            uiScope.launch {
                fileUtils.copyBitmap(
                    resource,
                    "refer_earn_share_image_${
                        Calendar.getInstance().timeInMillis.toString().takeLast(4)
                    }"
                )?.let {
                    withContext(Dispatchers.Main.immediate) {
                        shareImage(shouldShareOnlyOnWA, weakReference, shareMessage, it)
                        dismissProgressBar()
                    }
                }
            }
        } else {
            dismissProgressBar()
            shareImage(shouldShareOnlyOnWA, weakReference, shareMessage, null)
        }
    }

    private fun shareImage(
        shouldShareOnlyOnWA: Boolean,
        weakReference: WeakReference<Context>,
        shareMessage: String,
        it: File? = null
    ) {
        val context = weakReference.get() ?: return
        if (shouldShareOnlyOnWA) {
            context.shareOnWhatsapp(deviceUtils.getWhatsappPackageName(), shareMessage, image = it)
        } else {
            it?.let {
                fileUtils.shareImage(context, it, shareMessage)
            } ?: run {
                fileUtils.shareText(shareMessage, getString(com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_earn_v2_title))
            }
        }
    }

    private fun navigateToContactSupport() {
        val number = remoteConfigApi.getWhatsappNumber()
        requireContext().openWhatsapp(
            number,
            getCustomString(com.jar.app.feature_refer_earn_v2.shared.MR.strings.feature_refer_earn_hey_i_m_having_trouble_in_refer_earn)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }
    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(Refer_ScreenShown, key = Screen, value = "Refer_Intro")
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.combinedFlowLoading.collectLatest {
                    if (it) showProgressBar() else dismissProgressBar()
                }
            }
        }
    }

    private fun getData() {
        viewModel.fetchReferEarnIntros()
        appsFlyerInviteUtil.getAppsFlyerInviteLink()?.let {
            viewModel.fetchReferEarnMsgLinks(it)
        }
    }

    private fun setupUI() {
        glide = Glide.with(requireContext())
    }

    private fun buildMapForAnalytics(): MutableMap<String, String> {
        val data = viewModel.uiStateFlow.value.introScreenData.data?.data
        val map = mutableMapOf<String, String>(
            screenshown to if (data?.rewards.isNullOrEmpty()) Non_Reward_Screen else Reward_Screen ,
            buttonclicked to "",
            screen_type to refer_intro
        )
        data?.rewards?.forEach {
            it?.referralRewardType?.let {type ->
                map.put(type, it.amountText.orEmpty())
            }
        }
        return map
    }
}