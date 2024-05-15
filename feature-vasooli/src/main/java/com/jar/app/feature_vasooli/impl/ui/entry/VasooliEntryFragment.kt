package com.jar.app.feature_vasooli.impl.ui.entry

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.VasooliNavigationDirections
import com.jar.app.feature_vasooli.databinding.FragmentVasooliEntryBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.domain.model.UpdateEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryRequest
import com.jar.app.feature_vasooli.impl.ui.VasooliViewModel
import com.jar.app.feature_vasooli.impl.util.VasooliConstants
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
@RuntimePermissions
class VasooliEntryFragment : BaseFragment<FragmentVasooliEntryBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var phoneNumberUtils: PhoneNumberUtil

    companion object {
        private const val TAG = "#VasooliEntryFragment#"
    }

    private val args by navArgs<VasooliEntryFragmentArgs>()

    private val viewModel by viewModels<VasooliEntryViewModel> { defaultViewModelProviderFactory }

    private val vasooliViewModel by viewModels<VasooliViewModel> { defaultViewModelProviderFactory }

    private var dateTakenOn = 0L

    private var dateDueOn = 0L

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVasooliEntryBinding
        get() = FragmentVasooliEntryBinding::inflate

    private val contactPickerLauncherLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode != Activity.RESULT_OK || it.data == null) return@registerForActivityResult
            it.data?.let { intent ->
                intent.data?.let { uri ->
                    setNameAndPhone(uri)
                }
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        initClickListeners()
    }

    private fun setupUI() {
        args.vasooliEntryRequest?.let { vasooliEntryRequest ->
            binding.tvPhoneNumber.text = vasooliEntryRequest.borrowerPhoneNo
            binding.etAmount.setText(vasooliEntryRequest.amount.toString())
            binding.tvTakenOn.text =
                vasooliEntryRequest.lentOn?.epochToDate()?.getFormattedDate("dd LLLL yyyy")
            binding.tvDueOn.text =
                vasooliEntryRequest.dueOn?.epochToDate()?.getFormattedDate("dd LLLL yyyy")
            binding.etName.setText(vasooliEntryRequest.borrowerName.orEmpty())
            disableEditText(binding.etAmount)
            binding.tvPhoneNumber.isEnabled = false
            binding.dimmerAmount.alpha = 0.5f
            binding.dimmerPhone.alpha = 0.5f
            dateDueOn = vasooliEntryRequest.dueOn!!
            dateTakenOn = vasooliEntryRequest.lentOn!!
        }

        setupToolbar()
        toggleMainButton()
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_vasooli_paise_kisko_diye)

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.amountSubHeading.setDebounceClickListener {
            navigateTo(VasooliNavigationDirections.actionToPromptBottomSheetFragment())
        }

        binding.tvDueOn.setDebounceClickListener {
            clearFocus()
            if (dateTakenOn != 0L) {
                showDatePicker(isTakenOn = false)
            } else {
                getString(R.string.feature_vasooli_loan_date_first).snackBar(
                    binding.root,
                    translationY = 0f
                )
            }
        }

        binding.tvTakenOn.setDebounceClickListener {
            clearFocus()
            showDatePicker(isTakenOn = true)
        }

        binding.tvPhoneNumber.setDebounceClickListener {
            clearFocus()
            navigateToContactPickerWithPermissionCheck()
        }

        binding.btnSave.setDebounceClickListener {
            clearFocus()
            validate()?.let {
                val lentOnDate: String = it.lentOn?.epochToDate()?.getFormattedDate().orEmpty()
                val dueOnDate: String = it.dueOn?.epochToDate()?.getFormattedDate().orEmpty()
                analyticsHandler.postEvent(
                    VasooliEventKey.VasooliEntry.Clicked_SaveNewLoan_Vasooli,
                    mapOf(
                        VasooliEventKey.Name to it.borrowerName.orEmpty(),
                        VasooliEventKey.PhoneNumber to it.borrowerPhoneNo.orEmpty(),
                        VasooliEventKey.Amount to it.amount?.orZero().toString(),
                        VasooliEventKey.LoanDate to lentOnDate,
                        VasooliEventKey.DueDate to dueOnDate
                    )
                )
                if (args.vasooliEntryRequest != null) {
                    viewModel.updateVasooliEntry(
                        UpdateEntryRequest(
                            loanId = args.loanId!!,
                            lentOn = it.lentOn,
                            dueDate = it.dueOn,
                            borrowerName = it.borrowerName
                        )
                    )
                } else {
                    viewModel.postVasooliRequest(it)
                }
            }
        }

        binding.etName.textChanges()
            .debounce(100)
            .onEach {
                toggleMainButton()
            }
            .launchIn(uiScope)

        binding.etAmount.textChanges()
            .debounce(100)
            .onEach {
                toggleMainButton()
            }
            .launchIn(uiScope)
    }

    private fun observeLiveData() {

        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewModel.postVasooliRequestLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                popBackStack()
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.updateVasooliEntryLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                popBackStack()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                popBackStack()
            },
            onError = {
                dismissProgressBar()
            }
        )

        vasooliViewModel.networkStateLiveData.observe(viewLifecycleOwner) {
            binding.toolbar.clNetworkContainer.isSelected = it
            binding.toolbar.tvInternetConnectionText.text =
                if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                    com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again)
            binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off, 0, 0, 0
            )
            if (it) {
                if (binding.toolbar.networkExpandableLayout.isExpanded) {
                    uiScope.launch {
                        delay(500)
                        binding.toolbar.networkExpandableLayout.collapse(true)
                    }
                }
            } else {
                binding.toolbar.networkExpandableLayout.expand(true)
            }
        }
    }

    private fun validate(): VasooliEntryRequest? {
        val data = phoneNumberUtils.parse(
            binding.tvPhoneNumber.text?.toString().orEmpty(),
            BaseConstants.REGION_CODE
        )
        val vasooliEntryRequest = VasooliEntryRequest(
            borrowerName = binding.etName.text?.toString().orEmpty(),
            borrowerPhoneNo = data.nationalNumber.toString(),
            borrowerCountryCode = data.countryCode.toString(),
            amount = binding.etAmount.text?.toString()?.toInt(),
            dueOn = dateDueOn,
            lentOn = dateTakenOn
        )

        if (vasooliEntryRequest.borrowerPhoneNo.isNullOrEmpty()) {
            getString(R.string.feature_vasooli_select_a_contact).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.borrowerName.isNullOrEmpty()) {
            getString(R.string.feature_vasooli_enter_name).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.amount?.orZero() == 0) {
            getString(R.string.feature_vasooli_enter_valid_amount).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.amount?.orZero()!! > VasooliConstants.MAX_AMOUNT) {
            getString(
                R.string.feature_vasooli_amount_cannot_be_greater_than_x,
                VasooliConstants.MAX_AMOUNT.toString()
            ).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.lentOn == null) {
            getString(R.string.feature_vasooli_lending_date).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.dueOn == null) {
            getString(R.string.feature_vasooli_select_due_date).snackBar(binding.root)
            return null
        } else if (vasooliEntryRequest.lentOn > vasooliEntryRequest.dueOn) {
            getString(R.string.feature_vasooli_date_mismatch).snackBar(binding.root)
            return null
        }

        return vasooliEntryRequest
    }

    private fun showDatePicker(isTakenOn: Boolean) {
        val constraintsBuilder = CalendarConstraints.Builder()

        if (isTakenOn) {
            constraintsBuilder.setValidator(DateValidatorPointBackward.now())
        }
        if (!isTakenOn && dateTakenOn != 0L) {
            constraintsBuilder.setValidator(DateValidatorPointForward.from(dateTakenOn))
        }

        val materialDatePicker = datePicker()
            .setTheme(com.jar.app.core_ui.R.style.ThemeOverlay_App_DatePicker)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            val formattedDate = it.epochToDate().getFormattedDate("dd LLLL yyyy")

            if (isTakenOn) {
                dateTakenOn = it
                binding.tvTakenOn.text = formattedDate
            } else {
                dateDueOn = it
                binding.tvDueOn.text = formattedDate
            }
            toggleMainButton()
        }
        materialDatePicker.show(childFragmentManager, TAG)
    }

    private fun setNameAndPhone(uri: Uri) {
        val cursor = requireContext().contentResolver.query(
            uri, null, null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val numberIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            binding.etName.setText(cursor.getString(nameIndex).orEmpty())
            binding.tvPhoneNumber.setText(
                phoneNumberUtils.parse(
                    cursor.getString(numberIndex),
                    BaseConstants.REGION_CODE
                ).nationalNumber.toString()
            )
        } else
            getString(R.string.feature_vasooli_failed_to_get_contact).snackBar(binding.root)

        cursor?.close()
        toggleMainButton()
    }

    private fun openContactPicker() {
        try {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactPickerLauncherLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            getString(R.string.feature_vasooli_no_app_found).snackBar(binding.root)
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        var shouldEnable =
            if (disableAnyway) false else (!binding.etName.text.isNullOrEmpty()
                    && !binding.etAmount.text.isNullOrEmpty()
                    && !binding.tvPhoneNumber.text.isNullOrEmpty()
                    && !binding.tvTakenOn.text.isNullOrEmpty()
                    && !binding.tvDueOn.text.isNullOrEmpty())
        args.vasooliEntryRequest?.let {
            val data = phoneNumberUtils.parse(
                binding.tvPhoneNumber.text?.toString().orEmpty(),
                BaseConstants.REGION_CODE
            )
            val vasooliEntryRequest = VasooliEntryRequest(
                borrowerName = binding.etName.text?.toString().orEmpty(),
                borrowerPhoneNo = data.nationalNumber.toString(),
                borrowerCountryCode = data.countryCode.toString(),
                amount = binding.etAmount.text?.toString()?.toInt(),
                dueOn = dateDueOn,
                lentOn = dateTakenOn
            )
            shouldEnable = it.borrowerName != vasooliEntryRequest.borrowerName
                    || it.lentOn != vasooliEntryRequest.lentOn
                    || it.dueOn != vasooliEntryRequest.dueOn
        }
        binding.btnSave.setDisabled(!shouldEnable)
    }

    private fun clearFocus() {
        binding.etName.clearFocus()
        binding.etAmount.clearFocus()
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun navigateToContactPicker() {
        openContactPicker()
        analyticsHandler.postEvent(
            VasooliEventKey.VasooliEntry.ClickedPermission_Vasooli,
            mapOf(EventKey.PROP_STATUS to EventKey.ALLOWED)
        )
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun onPermissionDenied() {
        getString(R.string.feature_vasooli_need_contact_access).snackBar(binding.root)
        analyticsHandler.postEvent(
            VasooliEventKey.VasooliEntry.ClickedPermission_Vasooli,
            mapOf(EventKey.PROP_STATUS to EventKey.DENIED)
        )
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun onPermissionNeverAskAgain() {
        getString(R.string.feature_vasooli_need_contact_access).snackBar(binding.root)
        requireContext().openAppInfo()
        analyticsHandler.postEvent(
            VasooliEventKey.VasooliEntry.ClickedPermission_Vasooli,
            mapOf(EventKey.PROP_STATUS to EventKey.NEVER_ASK_AGAIN)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}