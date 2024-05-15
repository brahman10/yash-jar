package com.jar.app.feature_gifting.impl.ui.send_gift

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.base.util.setOnImeActionDoneListener
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.core_ui.dynamic_cards.base.EpoxyBaseEdgeEffectFactory
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gifting.NavigationGiftingDirections
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentSendGiftBinding
import com.jar.app.feature_gifting.shared.domain.model.*
import com.jar.app.feature_gifting.impl.ui.suggested_amount.SuggestedAmountAdapter
import com.jar.app.feature_gifting.shared.util.Constants
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.StringUtils
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class SendGiftFragment : BaseFragment<FeatureGiftingFragmentSendGiftBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<SendGiftFragmentViewModel> { defaultViewModelProviderFactory }

    private val spaceItemDecoration = SpaceItemDecoration(12.dp, 8.dp)

    private var edgeEffectFactory: EpoxyBaseEdgeEffectFactory? = null

    private var controller: SendGiftEpoxyController? = null

    private var suggestedAmountAdapter: SuggestedAmountAdapter? = null

    private var hasAddedRecommendedAmount = false

    private var giftGoldOptions: GiftGoldOptions? = null

    private var message: String? = null

    private val contactPickerLauncherLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode != Activity.RESULT_OK || it.data == null) return@registerForActivityResult
            it.data?.let { intent ->
                intent.data?.let { uri ->
                    viewModel.setReceiverDetailFromUri(controller?.cards.orEmpty(), uri)
                }
            }
        }

    private val args by navArgs<SendGiftFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentSendGiftBinding
        get() = FeatureGiftingFragmentSendGiftBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            EventKey.ShownGiftGoldScreen,
            mapOf(EventKey.fromScreen to args.fromScreen)
        )
        setupUI()
        setupListeners()
        observeLiveData()
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun readContacts() {
        openContactPicker()
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onPermissionDenied() {
        getString(R.string.feature_gifting_please_allow_contacts_permission).toast(binding.root)
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onPermissionNeverAskAgain() {
        getString(R.string.feature_gifting_please_allow_contacts_permission).toast(binding.root)
        requireContext().openAppInfo()
    }

    private fun setupUI() {
        binding.rvGifting.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGifting.addItemDecorationIfNoneAdded(spaceItemDecoration)
        controller = SendGiftEpoxyController(
            onEditNumberClick = {
                showEditContactView()
            },
            onEditAmountClick = {
                showEditAmountView()
            },
            onEditMessageClick = {
                navigateTo(NavigationGiftingDirections.actionToAddMessageDialog(it))
            }
        )
        edgeEffectFactory = EpoxyBaseEdgeEffectFactory()
        binding.rvGifting.edgeEffectFactory = edgeEffectFactory!!
        binding.rvGifting.setControllerAndBuildModels(controller!!)

        setSuggestedAmountView()

        uiScope.launch {
            delay(500)
            prefillReceiverDetails()
        }
    }

    private fun setSuggestedAmountView() {
        binding.clEnterAmount.rvSuggestedAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.clEnterAmount.rvSuggestedAmount.addItemDecorationIfNoneAdded(spaceItemDecoration)
        suggestedAmountAdapter = SuggestedAmountAdapter {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.fromScreen to args.fromScreen,
                    EventKey.buttonType to EventKey.suggestions,
                    EventKey.amount to it.amount,
                    EventKey.unit to it.unit.toString(),
                )
            )
            if (it.unit != null && it.unit!!.contains(Constants.SuggestedAmountUnit.UNIT_GM)) {
                binding.clEnterAmount.etBuyAmountInGrams.setText("${it.amount}")
                binding.clEnterAmount.etBuyAmountInGrams.setSelection(binding.clEnterAmount.etBuyAmountInGrams.text?.length.orZero())
            } else {
                binding.clEnterAmount.etBuyAmountInRupees.setText("${it.amount.toInt()}")
                binding.clEnterAmount.etBuyAmountInRupees.setSelection(binding.clEnterAmount.etBuyAmountInRupees.text?.length.orZero())
            }
        }
        binding.clEnterAmount.rvSuggestedAmount.adapter = suggestedAmountAdapter
    }

    private fun setupListeners() {
        uiScope.launch {
            binding.root.keyboardVisibilityChanges()
                .collectLatest {
                    if (viewModel.giftingState == GiftingState.SHOW_ENTER_AMOUNT)
                        binding.btnNext.isVisible = true

                    if (it) {
                        binding.rvGifting.smoothScrollToPosition(controller?.cards?.size.orZero())
                    }
                }
        }

        binding.btnNext.setDebounceClickListener {
            invokeNextAction()
        }

        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        controller?.addModelBuildListener {
            updateBottomSelectionView()
        }

        setSelectContactViewListener()

        setEnterAmountViewListener()
    }

    private fun setSelectContactViewListener() {
        binding.clContactSelection.btnSelectFromContacts.setDebounceClickListener {
            readContactsWithPermissionCheck()
        }
        binding.clContactSelection.btnClose.setDebounceClickListener {
            binding.clContactSelection.root.isVisible = false
            showEditAmountView()
        }

        binding.clContactSelection.btnEnterMobileNumber.setDebounceClickListener {
            showEditNumber()
        }

        binding.clEnterNumber.btnBack.setDebounceClickListener {
            showEditContactView()
            it.hideKeyboard()
        }

        binding.clEnterNumber.btnDone.setDebounceClickListener {
            val number = binding.clEnterNumber.etMobile.text.toString()
            if (StringUtils.isValidPhoneNumber(number)) {
                viewModel.addReceiverDetail(
                    controller?.cards.orEmpty(),
                    ReceiverDetail(
                        name = "Unknown",
                        number = "${BaseConstants.DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN}$number"
                    )
                )
            }
        }

        binding.clEnterNumber.etMobile.setOnImeActionDoneListener {
            val number = binding.clEnterNumber.etMobile.text.toString()
            if (StringUtils.isValidPhoneNumber(number)) {
                viewModel.addReceiverDetail(
                    controller?.cards.orEmpty(),
                    ReceiverDetail(
                        name = "Unknown",
                        number = "${BaseConstants.DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN}$number"
                    )
                )
                binding.clEnterNumber.etMobile.hideKeyboard()
            }
        }

        binding.clEnterNumber.etMobile.doAfterTextChanged {
            val isValidNumber = StringUtils.isValidPhoneNumber(it.toString())
            binding.clEnterNumber.btnDone.setDisabled(isValidNumber.not())
            binding.clEnterNumber.tvError.isVisible = isValidNumber.not()
        }

    }

    private fun setEnterAmountViewListener() {
        binding.clEnterAmount.etBuyAmountInGrams.setOnFocusChangeListener { view, focus ->
            if (focus && isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                viewModel.sendGiftGoldRequest.buyGoldRequestType = BuyGoldRequestType.VOLUME.name
                giftGoldOptions?.let { suggestedAmountAdapter?.submitList(it.giftGoldOptions.volumeOptions) }
                binding.clEnterAmount.clBuyInRupeesContainer.isSelected = false
                binding.clEnterAmount.clBuyInGramsContainer.isSelected = true
                binding.clEnterAmount.rsSymbol.setTextColor(
                    ContextCompat.getColorStateList(requireContext(), getRupeesTextColor())
                )
                binding.clEnterAmount.gramSymbol.setTextColor(
                    ContextCompat.getColorStateList(requireContext(), getGramsTextColor())
                )
            }
        }

        binding.clEnterAmount.etBuyAmountInRupees.setOnFocusChangeListener { view, focus ->
            if (focus && isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                viewModel.sendGiftGoldRequest.buyGoldRequestType = BuyGoldRequestType.AMOUNT.name
                giftGoldOptions?.let { suggestedAmountAdapter?.submitList(it.giftGoldOptions.options) }
                binding.clEnterAmount.clBuyInRupeesContainer.isSelected = true
                binding.clEnterAmount.clBuyInGramsContainer.isSelected = false
                binding.clEnterAmount.rsSymbol.setTextColor(
                    ContextCompat.getColorStateList(requireContext(), getRupeesTextColor())
                )
                binding.clEnterAmount.gramSymbol.setTextColor(
                    ContextCompat.getColorStateList(requireContext(), getGramsTextColor())
                )
            }
        }

        binding.clEnterAmount.etBuyAmountInRupees.doAfterTextChanged {
            val amount = it?.toString()?.toFloatOrNull().orZero()
            if (binding.clEnterAmount.etBuyAmountInRupees.hasFocus()) {
                viewModel.calculateVolumeFromAmount(amount)
            }
            updateCtaDimState()
        }

        binding.clEnterAmount.etBuyAmountInGrams.doAfterTextChanged {
            val volume = it?.toString()?.toFloatOrNull().orZero()
            if (binding.clEnterAmount.etBuyAmountInGrams.hasFocus()) {
                viewModel.calculateAmountFromVolume(volume)
            }
            updateCtaDimState()
        }

        binding.clEnterAmount.etBuyAmountInRupees.setOnImeActionDoneListener {
            addAmountAndMessage()
        }

        binding.clEnterAmount.etBuyAmountInGrams.setOnImeActionDoneListener {
            addAmountAndMessage()
        }
    }

    private fun getRupeesTextColor(): Int {
        return if (binding.clEnterAmount.etBuyAmountInRupees.hasFocus()) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
    }

    private fun getGramsTextColor(): Int {
        return if (binding.clEnterAmount.etBuyAmountInGrams.hasFocus()) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
    }

    private fun observeLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(Constants.EXTRA_MESSAGE)
            ?.observe(viewLifecycleOwner) {
                message = it
                addAmountAndMessage()
            }

        viewModel.listLiveData.observe(viewLifecycleOwner) {
            controller?.cards = it
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            getString(it).snackBar(binding.root)
        }

        viewModel.suggestedAmountLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                giftGoldOptions = it
                if (!hasAddedRecommendedAmount) {
                    it.giftGoldOptions.options.find { it.recommended.orFalse() }?.amount?.toInt()?.let {
                        binding.clEnterAmount.etBuyAmountInRupees.setText("$it")
                        viewModel.calculateVolumeFromAmount(it.toFloat())
                    }
                    hasAddedRecommendedAmount = true
                }
                binding.clEnterAmount.etBuyAmountInRupees.setSelection(binding.clEnterAmount.etBuyAmountInRupees.text?.length.orZero())
                binding.clEnterAmount.etBuyAmountInGrams.setSelection(binding.clEnterAmount.etBuyAmountInGrams.text?.length.orZero())
                suggestedAmountAdapter?.submitList(it.giftGoldOptions.options)
            }
        )

        viewModel.currentGoldBuyPriceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                val spannable = buildSpannedString {
                    append(getString(R.string.feature_gifting_current_buy_price))
                    append(" ")
                    bold {
                        append(getString(R.string.feature_gifting_currency_sign_x_float, it.price))
                        append(getString(R.string.feature_gifting_per_gm))
                    }
                }
                binding.clEnterAmount.tvCurrentPrice.text = spannable

                uiScope.countDownTimer(
                    it.getValidityInMillis(),
                    onInterval = {
                        binding.clEnterAmount.tvTimer.text = getString(
                            com.jar.app.core_ui.R.string.core_ui_valid_for_s,
                            it.milliSecondsToCountDown()
                        )
                    },
                    onFinished = {
                        viewModel.fetchCurrentGoldBuyPrice()
                    }
                )
            }
        )

        viewModel.volumeFromAmountLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.clEnterAmount.etBuyAmountInGrams.setText(it.volumeToString())
                binding.clEnterAmount.clBuyInGramsContainer.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        com.jar.app.feature_buy_gold_v2.R.drawable.feature_buy_gold_v2_bg_selector_rounded_2e2942_outline_789bde_8dp
                    )
            }
        )

        viewModel.amountFromVolumeLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.clEnterAmount.etBuyAmountInRupees.setText(it.amountToString())
            }
        )
    }

    private fun openContactPicker() {
        analyticsHandler.postEvent(
            EventKey.ClickedSelectContacts_giftGoldScreen,
            mapOf(EventKey.fromScreen to args.fromScreen)
        )
        try {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactPickerLauncherLauncher.launch(intent)
        } catch (e: Exception) {
            getString(R.string.feature_gifting_no_app_found).snackBar(binding.root)
        }
    }

    private fun updateBottomSelectionView() {
        when (viewModel.giftingState) {
            GiftingState.SHOW_CONTACT_SELECTION -> {
                binding.clContactSelection.root.isVisible = true
                binding.clEnterNumber.root.isVisible = false
                binding.clEnterAmount.root.isVisible = false
                binding.tvGoldPurchaseInfo.isVisible = false
                binding.btnNext.isVisible = false
                binding.btnNext.setText(getString(R.string.feature_gifting_done))
                binding.clContactSelection.btnClose.isVisible = controller?.cards?.size.orZero() > 2
            }
            GiftingState.SHOW_ENTER_NUMBER -> {
                binding.clEnterNumber.root.isVisible = true
                binding.clContactSelection.root.isVisible = false
                binding.clEnterAmount.root.isVisible = false
                binding.tvGoldPurchaseInfo.isVisible = false
                binding.btnNext.isVisible = false
                binding.btnNext.setText(getString(R.string.feature_gifting_done))
            }
            GiftingState.SHOW_ENTER_AMOUNT -> {
                binding.clEnterAmount.root.isVisible = true
                binding.clEnterNumber.root.isVisible = false
                binding.clContactSelection.root.isVisible = false
                binding.tvGoldPurchaseInfo.isVisible = false
                binding.btnNext.isVisible = false
                binding.btnNext.setText(getString(R.string.feature_gifting_done))

                binding.clEnterAmount.etBuyAmountInRupees.requestFocus()
                binding.clEnterAmount.etBuyAmountInRupees.showKeyboard()
                updateCtaDimState()
            }
            GiftingState.ALL_DETAILS_ENTERED -> {
                binding.clEnterNumber.root.isVisible = false
                binding.clContactSelection.root.isVisible = false
                binding.clEnterAmount.root.isVisible = false
                binding.tvGoldPurchaseInfo.isVisible = true
                binding.btnNext.isVisible = true
                binding.btnNext.setText(getString(R.string.feature_gifting_confirm_and_proceed))
            }
            else -> {
                //Do Nothing...
            }
        }
        binding.rvGifting.smoothScrollToPosition(controller?.cards?.size.orZero())
    }

    private fun showEditContactView() {
        viewModel.giftingState = GiftingState.SHOW_CONTACT_SELECTION
        updateBottomSelectionView()
    }

    private fun showEditNumber() {
        analyticsHandler.postEvent(
            EventKey.ClickedEnterMobileNumber_GiftGoldScreen,
            mapOf(EventKey.fromScreen to args.fromScreen)
        )
        viewModel.giftingState = GiftingState.SHOW_ENTER_NUMBER
        updateBottomSelectionView()
    }

    private fun showEditAmountView() {
        viewModel.giftingState = GiftingState.SHOW_ENTER_AMOUNT
        updateBottomSelectionView()
    }

    private fun addAmountAndMessage() {
        val amount = binding.clEnterAmount.etBuyAmountInRupees.text
        if (amount.isNullOrBlank().not() &&
            amount.toString().toFloatOrNull().orZero() >= Constants.MIN_GOLD_AMOUNT
        ) {
            binding.root.hideKeyboard()
            viewModel.addAmountAndMessageDetail(
                controller?.cards.orEmpty(),
                AmountAndMessageDetail(
                    amountInRupees = binding.clEnterAmount.etBuyAmountInRupees.text?.toString()
                        ?.toFloatOrNull().orZero(),
                    volumeInGm = binding.clEnterAmount.etBuyAmountInGrams.text?.toString()
                        ?.toFloatOrNull().orZero(),
                    message = message
                )
            )
        } else {
            getString(
                R.string.feature_gifting_minimum_gold_gifting_amount_is_x,
                Constants.MIN_GOLD_AMOUNT
            ).snackBar(binding.root)
        }
    }

    private fun moveToSummaryFragment() {
        if (
            viewModel.sendGiftGoldRequest.receiverName.isNullOrBlank().not()
            && viewModel.sendGiftGoldRequest.receiverPhoneNo.isNullOrBlank().not()
            && viewModel.sendGiftGoldRequest.amount.orZero() != 0f
            && viewModel.sendGiftGoldRequest.volume.orZero() != 0f
        ) {
            analyticsHandler.postEvent(
                EventKey.ClickedSendAfterEnteringAmount_SendGoldScreen,
                mapOf(
                    EventKey.fromScreen to args.fromScreen,
                    EventKey.amount to viewModel.sendGiftGoldRequest.amount.toString(),
                    EventKey.quantity to viewModel.sendGiftGoldRequest.volume.toString(),
                    EventKey.receiverDetails to viewModel.sendGiftGoldRequest.receiverName.toString()
                )
            )
            navigateTo(
                SendGiftFragmentDirections.actionSendGiftFragmentToGiftSummaryFragment(
                    viewModel.sendGiftGoldRequest
                )
            )
        } else {
            getString(R.string.feature_gifting_please_enter_all_the_details).snackBar(binding.root)
        }
    }

    private fun invokeNextAction() {
        when (viewModel.giftingState) {
            GiftingState.SHOW_ENTER_AMOUNT -> addAmountAndMessage()
            GiftingState.ALL_DETAILS_ENTERED -> moveToSummaryFragment()
            else -> {
                //Do Nothing...
            }
        }
    }

    private fun prefillReceiverDetails() {
        args.sendGiftRequest?.let {
            val sendGiftGoldRequest: SendGiftGoldRequest? = serializer.decodeFromString(decodeUrl(it))
            if (sendGiftGoldRequest != null)
                viewModel.prefillReceiverDetails(
                    controller?.cards.orEmpty(),
                    sendGiftGoldRequest
                )
        }
    }

    private fun updateCtaDimState() {
        val amount = binding.clEnterAmount.etBuyAmountInRupees.text
        val volume = binding.clEnterAmount.etBuyAmountInGrams.text
        binding.btnNext.setDisabled(amount.isNullOrBlank() || volume.isNullOrBlank())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}