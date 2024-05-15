package com.jar.app.feature_lending.impl.ui.application_rejected

import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.RefreshHamburgerItemEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentLoanApplicationRejectedBinding
import com.jar.app.feature_lending.impl.ui.eligibility.rejected.LendingEligibilityFaqAdapter
import com.jar.app.feature_lending.impl.ui.eligibility.rejected.LendingEligibilityRejectedViewModelAndroid
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.v2.ApplicationRejectionData
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class LoanApplicationRejectedFragment :
    BaseFragment<FragmentLoanApplicationRejectedBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<LoanApplicationRejectedFragmentArgs>()

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<LendingEligibilityRejectedViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var adapter: LendingEligibilityFaqAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanApplicationRejectedBinding
        get() = FragmentLoanApplicationRejectedBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.Landing_RejectionScreenShown,
            mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
        )
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(shouldHide = true)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        getData()
        observeLiveData()
        setupUI()
        initClickListeners()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.acknowledgeRejection()
            }
        }

    private fun setupUI() {
        binding.lendingToolbar.btnBack.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_close)
        binding.lendingToolbar.btnBack.imageTintList =
            ColorStateList.valueOf(android.graphics.Color.WHITE)
        adapter = LendingEligibilityFaqAdapter {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_RejectionScreenFAQClicked,
                values = mapOf(LendingEventKeyV2.faq_title to it)
            )
        }
        binding.rvFaq.layoutManager = LinearLayoutManager(requireContext())
        val decorator = object : DividerItemDecoration(
            requireContext(), LinearLayoutManager.VERTICAL
        ) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1)
                    outRect.setEmpty()
                else
                    super.getItemOffsets(outRect, view, parent, state)
            }
        }
        ContextCompat.getDrawable(
            requireContext(),
            com.jar.app.core_ui.R.drawable.core_ui_line_separator
        )?.let {
            decorator.setDrawable(it)
        }
        binding.rvFaq.addItemDecorationIfNoneAdded(decorator)
        binding.rvFaq.adapter = adapter

        parentViewModel.staticContent?.applicationRejectedData?.let {
            setRejectionData(it)
        } ?: kotlin.run {
            if (parentViewModel.getLoanId().isEmpty()) {
                parentViewModel.fetchLoanList()
            }
        }
    }

    private fun initClickListeners() {
        binding.btnOkay.setDebounceClickListener {
            handleButtonClick()
        }
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            handleButtonClick()
        }
        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_application_rejected),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
        }
    }

    private fun handleButtonClick() {
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_RejectionScreenGoHomeButtonClicked,
            mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
        )
        viewModel.acknowledgeRejection()
    }

    private fun getData() {
        if (parentViewModel.getLoanId().isNotEmpty()) {
            parentViewModel.fetchStaticContent(
                LendingConstants.StaticContentType.APPLICATION_REJECTED,
                parentViewModel.getLoanId()
            )
        } else {
            parentViewModel.fetchLoanList()
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setRejectionData(it.applicationRejectedData)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanApplicationFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        it?.getOrNull(0)?.let {
                            parentViewModel.fetchStaticContent(
                                LendingConstants.StaticContentType.APPLICATION_REJECTED,
                                it.applicationId.orEmpty()
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.ackFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        goToHome()
                    },
                    onSuccessWithNullData = {
                        goToHome()
                    },
                    onError = { errorMessage, _ ->
                        goToHome()
                    }
                )
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun setRejectionData(data: ApplicationRejectionData?) {
        data ?: return
        binding.tvTitleRejected.text = HtmlCompat.fromHtml(
            parentViewModel.staticContent?.applicationRejectedData?.title.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvDescription.text = HtmlCompat.fromHtml(
            parentViewModel.staticContent?.applicationRejectedData?.description.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        adapter?.submitList(data.faqs)

        val daysLeft = data.daysLeft.orZero()
        if (daysLeft > 0) {
            binding.daysGroup.visibility = View.VISIBLE
            val span = buildSpannedString {
                append(getString(com.jar.app.feature_lending.shared.R.string.feature_lending_please_retry_after))
                append(" ")
                bold {
                    append(
                        getString(
                            com.jar.app.feature_lending.shared.R.string.feature_lending_x_days,
                            daysLeft
                        )
                    )
                }
            }.toSpannable()
            binding.tvDays.text = span
        } else
            binding.daysGroup.visibility = View.GONE
    }

    private fun goToHome() {
        dismissProgressBar()
        EventBus.getDefault().post(RefreshHamburgerItemEvent())
        EventBus.getDefault().post(GoToHomeEvent("LOAN_ELIGIBILITY_REJECTED"))
    }
}