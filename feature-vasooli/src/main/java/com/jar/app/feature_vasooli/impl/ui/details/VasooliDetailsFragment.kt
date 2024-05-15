package com.jar.app.feature_vasooli.impl.ui.details

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.FragmentVasooliDetailsBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.domain.event.ReminderSentEvent
import com.jar.app.feature_vasooli.impl.domain.event.RepaymentUpdatedEvent
import com.jar.app.feature_vasooli.impl.domain.model.Borrower
import com.jar.app.feature_vasooli.impl.domain.model.VasooliConfirmation
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliStatus
import com.jar.app.feature_vasooli.impl.ui.VasooliViewModel
import com.jar.app.feature_vasooli.impl.util.VasooliConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class VasooliDetailsFragment : BaseFragment<FragmentVasooliDetailsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModel by viewModels<VasooliDetailsViewModel> { defaultViewModelProviderFactory }

    private val vasooliViewModel by viewModels<VasooliViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<VasooliDetailsFragmentArgs>()

    private var adapter: RepaymentAdapter? = null

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVasooliDetailsBinding
        get() = FragmentVasooliDetailsBinding::inflate

    private var borrower: Borrower? = null

    private var isShownEventPosted = false

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        getData()
        initClickListeners()
    }

    private fun setupUI() {
        setupToolbar()
        adapter = RepaymentAdapter()
        binding.rvRepayments.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvRepayments.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvRepayments.adapter = adapter
        binding.rvRepayments.layoutManager = LinearLayoutManager(context)
    }

    private fun setLoanDetails(borrower: Borrower) {
        this.borrower = borrower
        binding.ivLender.setDrawableFromName(
            name = borrower.borrowerName.orEmpty(),
            textColor = com.jar.app.core_ui.R.color.color_375B6F,
            backgroundColor = com.jar.app.core_ui.R.color.color_7DB2CF
        )
        binding.tvName.text = borrower.borrowerName.orEmpty()
        binding.tvDueOn.text = getString(
            R.string.feature_vasooli_due_on_x,
            borrower.dueDate?.epochToDate()?.getFormattedDate("dd LLLL ''yy").orEmpty()
        )
        binding.clDetails.isVisible = true

        val receivedPayment = borrower.borrowedAmount - borrower.dueAmount
        when (borrower.status) {
            VasooliStatus.ACTIVE.name, VasooliStatus.PARTIALLY_RECOVERED.name -> {
                binding.layoutLoanProgress.root.isVisible = true
                binding.groupRepayment.isVisible = true
                binding.layoutLoanProgress.tvTotalUdhaar.text =
                    getString(R.string.feature_vasooli_total_udhaar, borrower.borrowedAmount.toString())
                binding.layoutLoanProgress.tvRepaymentAmount.text =
                    getString(R.string.feature_vasooli_currency_sign_x_int, receivedPayment)
                binding.layoutLoanProgress.tvDueAmount.text =
                    getString(R.string.feature_vasooli_currency_sign_x_int, borrower.dueAmount)
                val progress =
                    ((receivedPayment.toFloat() / borrower.borrowedAmount.toFloat()) * 100f).toInt()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.layoutLoanProgress.progressLoan.setProgress(progress, true)
                } else {
                    binding.layoutLoanProgress.progressLoan.progress = progress
                }
            }
            VasooliStatus.RECOVERED.name -> {
                binding.layoutLoanRepaid.tvFullyRepaid.text =
                    getString(R.string.feature_vasooli_fully_repaid)
                binding.layoutLoanRepaid.clRoot.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.feature_vasooli_bg_rounded_1ea787_10dp
                )
                binding.layoutLoanRepaid.ivTick.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.feature_vasooli_ic_green_tick
                    )
                )
                binding.layoutLoanRepaid.tvRepaymentAmount.text =
                    getString(R.string.feature_vasooli_currency_sign_x_int, borrower.borrowedAmount)
                binding.layoutLoanRepaid.tvRepaymentDate.text =
                    borrower.dueDate?.epochToDate()?.getFormattedDate("dd LLLL ''yy").orEmpty()
                binding.layoutLoanRepaid.root.isVisible = true
            }
            VasooliStatus.DEFAULT.name -> {
                binding.layoutLoanRepaid.tvFullyRepaid.text =
                    getString(R.string.feature_vasooli_default)
                binding.layoutLoanRepaid.clRoot.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.feature_vasooli_bg_red_rounded_10dp
                )
                binding.layoutLoanRepaid.ivTick.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.feature_vasooli_ic_close
                    )
                )
                binding.layoutLoanRepaid.tvRepaymentDate.text =
                    getString(R.string.feature_vasooli_you_marked_as_default)
                binding.layoutLoanRepaid.tvRepaymentAmount.text =
                    getString(R.string.feature_vasooli_currency_sign_x_int, borrower.dueAmount)
                binding.layoutLoanRepaid.root.isVisible = true
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_vasooli_details)
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliDetails.Clicked_LoanDetailsScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Back
                )
            )
            popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.tvAddNewRepayment.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliDetails.Clicked_LoanDetailsScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.NewRepayment
                )
            )
            borrower?.dueAmount?.let {
                navigateTo(
                    VasooliDetailsFragmentDirections.actionVasooliDetailsFragmentToAddRepaymentFragment(
                        args.loanId, it, borrower?.lentOn.orZero()
                    )
                )
            }
        }

        binding.tvSendReminder.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliDetails.Clicked_LoanDetailsScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.SendReminder
                )
            )
            viewModel.fetchReminder(
                args.loanId,
                if (remoteConfigApi.isVasooliReminderSelf()) VasooliConstants.SELF else VasooliConstants.JAR
            )
        }

        binding.ivMenu.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliDetails.Clicked_LoanDetailsScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Edit
                )
            )
            showPopupMenu()
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewModel.reminderLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                navigateTo(
                    VasooliDetailsFragmentDirections.actionVasooliDetailsFragmentToSendReminderFragment(
                        it,
                        args.loanId
                    )
                )
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.repaymentHistoryLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                if (it.isEmpty()) {
                    binding.shimmerRecycler.isVisible = false
                    binding.shimmerRecycler.stopShimmer()
                    binding.emptyLayout.tvTitle.text =
                        getString(R.string.fetaure_vasooli_no_repayment_title)
                    binding.emptyLayout.tvDescription.text =
                        getString(R.string.feature_vasooli_no_repayment_description)
                    binding.emptyLayout.root.isVisible = true
                } else {
                    adapter?.submitList(it)
                    binding.shimmerRecycler.stopShimmer()
                    binding.shimmerRecycler.isVisible = false
                    binding.rvRepayments.isVisible = true
                }
            },
            onError = {
                binding.shimmerRecycler.stopShimmer()
                binding.shimmerRecycler.isVisible = false
            }
        )

        viewModel.loanDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                sendShownEvent(it.borrowerPhoneNumber.orEmpty())
                setLoanDetails(it)
                binding.shimmerOverview.stopShimmer()
                binding.shimmerOverview.isVisible = false
            },
            onSuccessWithNullData = {
                sendShownEvent()
                binding.shimmerOverview.stopShimmer()
                binding.shimmerOverview.isVisible = false
            },
            onError = {
                sendShownEvent()
                binding.shimmerOverview.stopShimmer()
                binding.shimmerOverview.isVisible = false
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

    private fun getData() {
        resetUIState()
        viewModel.fetchRepaymentHistory(args.loanId)
        viewModel.fetchLoanDetails(args.loanId)
    }

    private fun resetUIState() {
        binding.shimmerRecycler.startShimmer()
        binding.shimmerOverview.startShimmer()
        binding.shimmerRecycler.isVisible = true
        binding.shimmerOverview.isVisible = true
        binding.emptyLayout.root.isVisible = false
        binding.layoutLoanRepaid.root.isVisible = false
        binding.layoutLoanProgress.root.isVisible = false
        binding.groupRepayment.isVisible = false
        binding.clDetails.isVisible = false
        binding.rvRepayments.isVisible = false
    }

    private fun showPopupMenu() {
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.PopupStyle)
        val popup = PopupMenu(wrapper, binding.ivMenu, Gravity.LEFT)

        when (borrower?.status.orEmpty()) {
            VasooliStatus.ACTIVE.name, VasooliStatus.PARTIALLY_RECOVERED.name -> {
                popup.menu.add(
                    R.id.menuGroup,
                    R.id.menuEditDetails,
                    Menu.NONE,
                    getString(R.string.feature_vasooli_edit_details)
                )
                popup.menu.add(
                    R.id.menuGroup,
                    R.id.menuMarkPaid,
                    Menu.NONE,
                    getString(R.string.feature_vasooli_mark_as_fully_paid)
                )
                popup.menu.add(
                    R.id.menuGroup,
                    R.id.menuMarkDefault,
                    Menu.NONE,
                    getString(R.string.feature_vasooli_mark_as_default)
                )
                popup.menu.add(
                    R.id.menuGroup,
                    R.id.menuDeleteRecord,
                    Menu.NONE,
                    getString(R.string.feature_vasooli_delete_record)
                )
            }
            VasooliStatus.RECOVERED.name, VasooliStatus.DEFAULT.name -> {
                popup.menu.add(
                    R.id.menuGroup,
                    R.id.menuDeleteRecord,
                    Menu.NONE,
                    getString(R.string.feature_vasooli_delete_record)
                )
            }
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuEditDetails -> {
                    analyticsHandler.postEvent(
                        VasooliEventKey.VasooliDetails.Clicked_EditLoanDetails_Vasooli,
                        mapOf(
                            VasooliEventKey.Button to VasooliEventKey.EditDetails
                        )
                    )
                    borrower?.let { borrower ->
                        navigateTo(
                            VasooliDetailsFragmentDirections.actionVasooliDetailsFragmentToVasooliEntryFragment(
                                VasooliEntryRequest(
                                    borrowerName = borrower.borrowerName,
                                    borrowerPhoneNo = borrower.borrowerPhoneNumber,
                                    borrowerCountryCode = borrower.borrowerCountryCode,
                                    lentOn = borrower.lentOn,
                                    dueOn = borrower.dueDate,
                                    amount = borrower.borrowedAmount
                                ),
                                args.loanId
                            )
                        )
                    }
                }
                R.id.menuMarkPaid -> {
                    analyticsHandler.postEvent(
                        VasooliEventKey.VasooliDetails.Clicked_EditLoanDetails_Vasooli,
                        mapOf(
                            VasooliEventKey.Button to VasooliEventKey.FullyPaid
                        )
                    )
                    openConfirmationDialog(VasooliConfirmation.MARK_AS_PAID)
                }
                R.id.menuMarkDefault -> {
                    analyticsHandler.postEvent(
                        VasooliEventKey.VasooliDetails.Clicked_EditLoanDetails_Vasooli,
                        mapOf(
                            VasooliEventKey.Button to VasooliEventKey.Default
                        )
                    )
                    openConfirmationDialog(VasooliConfirmation.MARK_DEFAULT)
                }
                R.id.menuDeleteRecord -> {
                    analyticsHandler.postEvent(
                        VasooliEventKey.VasooliDetails.Clicked_EditLoanDetails_Vasooli,
                        mapOf(
                            VasooliEventKey.Button to VasooliEventKey.Delete
                        )
                    )
                    openConfirmationDialog(VasooliConfirmation.DELETE_RECORD)
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    private fun openConfirmationDialog(vasooliConfirmation: VasooliConfirmation) {
        navigateTo(
            VasooliDetailsFragmentDirections.actionVasooliDetailsFragmentToVasooliConfirmationFragment(
                vasooliConfirmation,
                args.loanId,
                borrower?.dueAmount.orZero()
            ),
            shouldAnimate = true
        )
    }

    private fun sendShownEvent(phoneNumber: String = "") {
        if (!isShownEventPosted) {
            isShownEventPosted = true
            analyticsHandler.postEvent(
                VasooliEventKey.Shown_Screen_Vasooli,
                mapOf(
                    VasooliEventKey.Screen to VasooliEventKey.LoanDetails,
                    VasooliEventKey.PhoneNumber to phoneNumber
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRepaymentUpdatedEvent(repaymentUpdatedEvent: RepaymentUpdatedEvent) {
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReminderSentEvent(reminderSentEvent: ReminderSentEvent) {
        getString(R.string.feature_vasooli_reminder_sent_successfully).snackBar(
            binding.root,
            translationY = 0f
        )
    }
}