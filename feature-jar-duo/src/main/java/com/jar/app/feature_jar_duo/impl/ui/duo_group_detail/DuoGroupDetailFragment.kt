package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.dp
import com.jar.app.base.util.spaceBeforeUpperCaseChar
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoDetailFragmentBinding
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPopupWindowBinding
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupObject
import com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent
import com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.DuoGroupDetailHelper.constructHashMapForAnalytics
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DuoGroupDetailFragment : BaseFragment<FeatureDuoDetailFragmentBinding>() {

    private val args by navArgs<DuoGroupDetailFragmentArgs>()

    private val viewModel by viewModels<DuoGroupDetailViewModel> {
        defaultViewModelProviderFactory
    }

    private var featureOneUserOneDeepLink: String? = null

    private var featureTwoUserOneDeepLink: String? = null

    private var featureOneUserTwoDeepLink: String? = null

    private var featureTwoUserTwoDeepLink: String? = null

    private var isAutoPayUserOneEnabled: Boolean? = null

    private var isDailyInvestUserOneEnabled: Boolean? = null

    private var isAutoPayUserTwoEnabled: Boolean? = null

    private var isDailyInvestUserTwoEnabled: Boolean? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoDetailFragmentBinding
        get() = FeatureDuoDetailFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(DuoEventKey.Shown_Screen_Duo, DuoEventKey.Duo_details)
        viewModel.fetchGroupInfo(args.groupID)
        binding.ivInfo.isVisible = !args.groupID.isNullOrEmpty()
        Glide.with(this).load(BaseConstants.ImageUrlConstants.FEATURE_DUO_BG_DUO_SPIRAL).into(binding.ivSpiralBg)

    }

    private fun setupListeners() {

        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }

        binding.ivInfo.setDebounceClickListener {
            analyticsHandler.postEvent(
                DuoEventKey.Clicked_button_Duo, mapOf
                    (
                    DuoEventKey.Button to DuoEventKey.option,
                    DuoEventKey.FromScreen to DuoEventKey.duo_info
                )
            )
            val popupBinding =
                FeatureDuoPopupWindowBinding.inflate(layoutInflater, binding.root, false)
            popupBinding.tvRename.text = getString(R.string.feature_duo_rename)
            popupBinding.tvDelete.text = getString(R.string.feature_duo_delete_small)

            val popupWindow = PopupWindow(
                popupBinding.root, 220.dp,
                114.dp, true
            )

            popupBinding.tvRename.setDebounceClickListener {
                val groupName = viewModel.groupInfoLiveData.value?.data?.data?.groupName
                if (groupName != null) {
                    analyticsHandler.postEvent(
                        DuoEventKey.Clicked_button_Duo, mapOf(
                            DuoEventKey.Button to DuoEventKey.rename_duo,
                            DuoEventKey.FromScreen to DuoEventKey.duo_info
                        )
                    )
                    popupWindow.dismiss()
                    navigateTo(
                        DuoGroupDetailFragmentDirections.actionFeatureDuoDetailFragmentToRenameGroupFragment(
                            args.groupID.toString(),
                            groupName
                        )
                    )
                }
            }

            popupBinding.tvDelete.setDebounceClickListener {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo, mapOf(
                        DuoEventKey.Button to DuoEventKey.delete_duo,
                        DuoEventKey.FromScreen to DuoEventKey.duo_info
                    )
                )
                popupWindow.dismiss()
                navigateTo(
                    DuoGroupDetailFragmentDirections.actionFeatureDuoDetailFragmentToDeleteGroupFragment2(
                        args.groupID.toString()
                    )
                )
            }

            popupWindow.showAsDropDown(binding.ivInfo, 14, 0, Gravity.END)
        }

        binding.user1AutoPay.ivFeature.setDebounceClickListener {
            if (featureOneUserOneDeepLink.isNullOrEmpty().not()) {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.AutoSaveSelf, isAutoPayUserOneEnabled)
                )
            } else {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.AutoSaveFriend, isAutoPayUserOneEnabled)
                )
            }

            featureOneUserOneDeepLink?.let {
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            }
        }

        binding.user1DailyInvest.ivFeature.setDebounceClickListener {
            if (featureTwoUserOneDeepLink.isNullOrEmpty().not()) {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.DailySavingsSelf, isDailyInvestUserOneEnabled)
                )
            } else {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.DailySavingsFriend, isDailyInvestUserOneEnabled)
                )
            }
            featureTwoUserOneDeepLink?.let {
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            }
        }

        binding.user2AutoPay.ivFeature.setDebounceClickListener {
            if (featureOneUserTwoDeepLink.isNullOrEmpty().not()) {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.AutoSaveSelf, isAutoPayUserTwoEnabled)
                )
            } else {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.AutoSaveFriend, isAutoPayUserTwoEnabled)
                )
            }
            featureOneUserTwoDeepLink?.let {
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            }
        }

        binding.user2DailyInvest.ivFeature.setDebounceClickListener {
            if (featureTwoUserTwoDeepLink.isNullOrEmpty().not()) {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.DailySavingsSelf, isDailyInvestUserTwoEnabled)
                )
            } else {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo,
                    constructHashMapForAnalytics(DuoEventKey.DailySavingsFriend, isDailyInvestUserTwoEnabled)
                )
            }
            featureTwoUserTwoDeepLink?.let {
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            }
        }

    }

    private fun observeLiveData() {
        viewModel.groupInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                if (args.groupID.isNullOrEmpty()) {
                    binding.tvHeaderText.text = getString(R.string.feature_duo_jar_duo_sample_page)
                } else {
                    binding.tvHeaderText.text = it.groupName
                }

                renderUserData(it.duoGroupsUserInfo[0])
                renderFriendData(it.duoGroupsUserInfo[1])
            }
        )

        viewModel.renameGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccessWithNullData = { viewModel.fetchGroupInfo(args.groupID) }
        )

        viewModel.deleteGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccessWithNullData = {
                popBackStack()
            }
        )
    }

    private fun renderFriendData(duoGroupObject: DuoGroupObject) {
        if (duoGroupObject.userProfile.isNullOrEmpty()) {
            binding.ivUser2Thumbnail.isVisible = false
            binding.tvUser2Initials.isVisible = true
            binding.tvUser2Initials.text =
                duoGroupObject.userName.spaceBeforeUpperCaseChar().asInitials()
        } else {
            binding.tvUser2Initials.isVisible = false
            binding.ivUser2Thumbnail.isVisible = true
            Glide.with(requireContext()).load(duoGroupObject.userProfile).circleCrop()
                .into(binding.ivUser2Thumbnail)
        }
        binding.tvUser2Name.text =
            (if (args.groupID.isNullOrEmpty()) getString(R.string.feature_duo_your_friend) else duoGroupObject.userName).toString()
        binding.user2AutoPay.tvFeatureName.text = duoGroupObject.top[0].displayText
        binding.user2DailyInvest.tvFeatureName.text = duoGroupObject.top[1].displayText
        binding.tvTotalTransactionLastWeekUser2Header.text =
            duoGroupObject.bottom[0].header
        binding.tvTotalTransactionUser2Header.text =
            duoGroupObject.bottom[1].header
        binding.tvTotalTransactionUser2Value.text =
            duoGroupObject.bottom[1].value
        binding.tvTotalTransactionLastWeekUser2Value.text =
            duoGroupObject.bottom[0].value
        Glide.with(this).load(duoGroupObject.top[0].iconLink)
            .into(binding.user2AutoPay.ivFeature)
        Glide.with(this).load(duoGroupObject.top[1].iconLink)
            .into(binding.user2DailyInvest.ivFeature)

        val autoPayEnabled = duoGroupObject.top[0].enabled
        binding.user2AutoPay.tvFeatureStatus.text =
            if (autoPayEnabled) getString(R.string.feature_duo_on) else getString(R.string.feature_duo_off)
        binding.user2AutoPay.tvFeatureStatus.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (autoPayEnabled) com.jar.app.core_ui.R.color.color_1EA787 else com.jar.app.core_ui.R.color.color_EB6A6E
        )
        binding.user2AutoPay.ivFeature.background = ContextCompat.getDrawable(
            requireContext(),
            if (autoPayEnabled) R.drawable.feature_duo_on_background else R.drawable.feature_duo_off_background
        )

        val dailyInvestEnabled = duoGroupObject.top[1].enabled
        binding.user2DailyInvest.tvFeatureStatus.text =
            if (dailyInvestEnabled) getString(R.string.feature_duo_on) else getString(R.string.feature_duo_off)
        binding.user2DailyInvest.tvFeatureStatus.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                if (dailyInvestEnabled) com.jar.app.core_ui.R.color.color_1EA787 else com.jar.app.core_ui.R.color.color_EB6A6E
            )
        binding.user2DailyInvest.ivFeature.background = ContextCompat.getDrawable(
            requireContext(),
            if (dailyInvestEnabled) R.drawable.feature_duo_on_background else R.drawable.feature_duo_off_background
        )

        featureOneUserTwoDeepLink = duoGroupObject.top[0].deepLink
        featureTwoUserTwoDeepLink = duoGroupObject.top[1].deepLink
        isAutoPayUserTwoEnabled = autoPayEnabled
        isDailyInvestUserTwoEnabled = dailyInvestEnabled
    }

    private fun renderUserData(duoGroupObject: DuoGroupObject) {
        if (duoGroupObject.userProfile.isNullOrEmpty()) {
            binding.ivUser1Thumbnail.isVisible = false
            binding.tvUser1Initials.isVisible = true
            binding.tvUser1Initials.text =
                duoGroupObject.userName.spaceBeforeUpperCaseChar().asInitials()
        } else {
            binding.tvUser1Initials.isVisible = false
            binding.ivUser1Thumbnail.isVisible = true
            Glide.with(requireContext()).load(duoGroupObject.userProfile).circleCrop()
                .into(binding.ivUser1Thumbnail)
        }
        binding.tvUser1Name.text = duoGroupObject.userName
        binding.user1AutoPay.tvFeatureName.text = duoGroupObject.top[0].displayText
        binding.user1DailyInvest.tvFeatureName.text =
            duoGroupObject.top[1].displayText
        binding.tvTotalTransactionUserLastWeek1Header.text =
            duoGroupObject.bottom[0].header
        binding.tvTotalTransactionUser1Header.text =
            duoGroupObject.bottom[1].header
        binding.tvTotalTransactionUserLastWeek1Value.text =
            duoGroupObject.bottom[0].value
        binding.tvTotalTransactionUser1Value.text =
            duoGroupObject.bottom[1].value
        Glide.with(this).load(duoGroupObject.top[0].iconLink)
            .into(binding.user1AutoPay.ivFeature)
        Glide.with(this).load(duoGroupObject.top[1].iconLink)
            .into(binding.user1DailyInvest.ivFeature)

        val autoPayEnabled = duoGroupObject.top[0].enabled
        binding.user1AutoPay.tvFeatureStatus.text =
            if (autoPayEnabled) getString(R.string.feature_duo_on) else getString(R.string.feature_duo_off)
        binding.user1AutoPay.tvFeatureStatus.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (autoPayEnabled) com.jar.app.core_ui.R.color.color_1EA787 else com.jar.app.core_ui.R.color.color_EB6A6E
        )
        binding.user1AutoPay.ivFeature.background = ContextCompat.getDrawable(
            requireContext(),
            if (autoPayEnabled) R.drawable.feature_duo_on_background else R.drawable.feature_duo_off_background
        )

        val dailyInvestEnabled = duoGroupObject.top[1].enabled
        binding.user1DailyInvest.tvFeatureStatus.text =
            if (dailyInvestEnabled) getString(R.string.feature_duo_on) else getString(R.string.feature_duo_off)
        binding.user1DailyInvest.tvFeatureStatus.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                if (dailyInvestEnabled) com.jar.app.core_ui.R.color.color_1EA787 else com.jar.app.core_ui.R.color.color_EB6A6E
            )
        binding.user1DailyInvest.ivFeature.background = ContextCompat.getDrawable(
            requireContext(),
            if (dailyInvestEnabled) R.drawable.feature_duo_on_background else R.drawable.feature_duo_off_background
        )

        featureOneUserOneDeepLink = duoGroupObject.top[0].deepLink
        isAutoPayUserOneEnabled = autoPayEnabled
        isDailyInvestUserOneEnabled = dailyInvestEnabled
        featureTwoUserOneDeepLink = duoGroupObject.top[1].deepLink

    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGroupListEvent(refreshGroupListEvent: RefreshGroupListEvent) {
        viewModel.fetchGroupInfo(args.groupID)
    }
}