package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.v2

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.CancelEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.dp
import com.jar.app.base.util.spaceBeforeUpperCaseChar
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoDetailFragmentV2Binding
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPopupWindowBinding
import com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent
import com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.DuoGroupDetailHelper
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class DuoGroupDetailFragmentV2 : BaseFragment<FeatureDuoDetailFragmentV2Binding>() {

    private val args by navArgs<DuoGroupDetailFragmentV2Args>()

    private val viewModel by viewModels<DuoGroupDetailViewModelV2> {
        defaultViewModelProviderFactory
    }
    private var duoOptionsAdapter: DuoOptionsAdapter? = null

    private var duoScoreAdapter: DuoGroupScoreAdapter? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoDetailFragmentV2Binding
        get() = FeatureDuoDetailFragmentV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.colorPrimaryDark)
        EventBus.getDefault().register(this)
        setupUI()
        observeLiveData()
        setupListeners()
        setupHeaderLottie()
    }

    private fun setupHeaderLottie() {
        binding.machineHeaderLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.DUO_MACHINE_HEADER
        ).apply {
            repeatCount = LottieDrawable.INFINITE
        }
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            DuoEventKey.Shown_Screen_Duo,
            mapOf(EventKey.Screen to DuoEventKey.Duo_Screen_Name)
        )
        viewModel.fetchGroupInfoV2(args.groupID)
        binding.ivInfo.isVisible = !args.groupID.isNullOrEmpty()

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
                val groupName = viewModel.groupInfoV2LiveData.value?.data?.data?.groupName
                if (groupName != null) {
                    analyticsHandler.postEvent(
                            DuoEventKey.Clicked_button_Duo_main_page, DuoEventKey.rename,
                        )
                    popupWindow.dismiss()
                    navigateTo(
                        DuoGroupDetailFragmentV2Directions.actionFeatureDuoDetailFragmentToRenameGroupFragment(
                            args.groupID.toString(),
                            groupName
                        )
                    )
                }
            }

            popupBinding.tvDelete.setDebounceClickListener {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page, DuoEventKey.delete,
                )
                popupWindow.dismiss()
                navigateTo(
                    DuoGroupDetailFragmentV2Directions.actionFeatureDuoDetailFragmentToDeleteGroupFragment2(
                        args.groupID.toString()
                    )
                )
            }

            popupWindow.showAsDropDown(binding.ivInfo, 14, 0, Gravity.END)
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.groupInfoV2LiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                var isSamplePage = false
                if (args.groupID.isNullOrEmpty()) {
                    binding.tvHeaderText.text = getString(R.string.feature_duo_jar_duo_sample_page)
                    binding.tvDuoHeaderName.text = getString(R.string.feature_duo_sample)
                    isSamplePage = true
                } else {
                    binding.tvHeaderText.text = it.groupName
                    binding.tvDuoHeaderName.text = getString(R.string.feature_duo_duo_text)

                }
                renderUserData(it.duoGroupsUserInfo[0], isSamplePage = isSamplePage)
                renderFriendData(it.duoGroupsUserInfo[1], isSamplePage)
                renderScoreData(it.duoBottomObject, isSamplePage)
                setWeekDaysLeft(it)
            }
        )

        viewModel.renameGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccessWithNullData = { viewModel.fetchGroupInfoV2(args.groupID) }
        )

        viewModel.deleteGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccessWithNullData = {
                popBackStack()
            }
        )
    }

    private fun setWeekDaysLeft(it: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2) {

        val spannableString = SpannableString(String.format(binding.root.resources.getString(R.string.week_days_left),it.weekDaysLeft))
        val foregroundSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.smallTxtColor))
        spannableString.setSpan(
            foregroundSpan,
            0,
            spannableString.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.daysLeftInWeek.text = spannableString
    }

    private fun renderScoreData(
        duoBottomObject: List<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2>,
        isSamplePage: Boolean
    ) {
        duoScoreAdapter = DuoGroupScoreAdapter(isSamplePage) { options ->
            handleScoreButtonAnalytics(options)
            EventBus.getDefault().post(options.deepLink?.let { HandleDeepLinkEvent(it) })
        }
        duoScoreAdapter?.submitList(duoBottomObject)
        binding.rvScoreView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = duoScoreAdapter

        }
    }

    private fun handleScoreButtonAnalytics(options: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) {
        options.optionName?.uppercase()?.let {buttonName->
        analyticsHandler.postEvent(
            DuoEventKey.Clicked_button_Duo_main_page,
            mapOf(
                DuoEventKey.Key to buttonName,
            )
        )
        }
    }

    private fun renderUserData(duoGroupObject: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupObjectV2, isSamplePage: Boolean) {
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
        binding.tvNameUsr1.text = duoGroupObject.userName.replace(' ', '\n')
        binding.tvOverallUsr1Score.text = duoGroupObject.overallScore.toString()
        setupUserOptionsRecyclerView(
            duoGroupObject.top,
            isSamplePage,
            isOwner = duoGroupObject.isOwner
        )


    }

    private fun renderFriendData(duoGroupObject: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupObjectV2, isSamplePage: Boolean) {
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
        binding.tvNameUsr2.text = duoGroupObject.userName.replace(' ', '\n')
        binding.tvOverallUsr2Score.text = duoGroupObject.overallScore.toString()
        setupFriendsOptionsRecyclerView(duoGroupObject.top, isSamplePage, duoGroupObject.isOwner)
    }

    private fun setupUserOptionsRecyclerView(
        options: List<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2>,
        isSamplePage: Boolean,
        isOwner: Boolean
    ) {

        duoOptionsAdapter = DuoOptionsAdapter(isOwner, isSamplePage) {
            handleOptionsUserAnalytics(it)
            if (it.message != null) {
                val errorMessage =
                    String.format(resources.getString(R.string.feature_duo_options_error), it.message)
                errorMessage.snackBar(binding.duoDetailContainer)
            } else {

                it.deepLink?.let { _deeplink ->
                    EventBus.getDefault().post(HandleDeepLinkEvent(_deeplink))
                }

            }
        }
        duoOptionsAdapter?.submitList(options)
        binding.rvSwitchOptionsUsr1.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = duoOptionsAdapter
            isEnabled = true
        }
    }

    private fun handleOptionsUserAnalytics(it: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) {
        when (it.optionName?.uppercase()) {
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_ROUND_OFF.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.RoundOffSelf,
                        it.initialState
                    )
                )
            }
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_DAILY_SAVING.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.DailySavingsSelf,
                        it.initialState
                    )
                )
            }
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_KYC.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.KYCSelf,
                        it.initialState
                    )
                )
            }

        }
    }


    private fun handleOptionsFriendAnalytics(it: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) {
        when (it.optionName?.uppercase()) {
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_ROUND_OFF.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.RoundOffFriend,
                        it.initialState
                    )
                )
            }
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_DAILY_SAVING.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.DailySavingsFriend,
                        it.initialState
                    )
                )
            }
            com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoOptionName.DUO_OPTION_KYC.value -> {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_main_page,
                    DuoGroupDetailHelper.constructHashMapForAnalytics(
                        DuoEventKey.KYCFriend,
                        it.initialState
                    )
                )
            }

        }
    }

    private fun setupFriendsOptionsRecyclerView(
        options: List<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupTopObjectV2>,
        isSamplePage: Boolean,
        owner: Boolean
    ) {
        val friendsAdapter = DuoOptionsAdapter(owner, isSamplePage) {
            handleOptionsFriendAnalytics(it)

        }
        binding.rvSwitchOptionsUsr2.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = friendsAdapter
        }
        friendsAdapter.submitList(options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGroupListEvent(refreshGroupListEvent: RefreshGroupListEvent) {
        viewModel.fetchGroupInfoV2(args.groupID)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDialogCanceledEvent(cancelEvent: CancelEvent) {
        duoOptionsAdapter?.updateCancel()
    }
}