package com.jar.app.feature_lending.impl.ui.agreement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending.databinding.FragmentBreakdownInfoBottomSheetBinding
import com.jar.app.feature_lending.impl.ui.common.QuestionAnswerAdapter
import com.jar.app.feature_lending.shared.domain.model.v2.LendingEligibilityRange
import com.jar.app.feature_lending.shared.domain.model.v2.QuestionAnswer
import com.jar.app.feature_lending.shared.util.LendingConstants
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BreakdownInfoBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentBreakdownInfoBottomSheetBinding>() {

    private val args by navArgs<BreakdownInfoBottomSheetFragmentArgs>()

    private var infoAdapter: QuestionAnswerAdapter? = null
    private var chargesAdapter: LoanBreakdownInfoAdapter? = null
    private var rangeAdapter: QuestionAnswerAdapter? = null

    private val spaceDecorator by lazy { SpaceItemDecoration(0.dp, 12.dp) }
    private val dividerDecorator by lazy { DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL) }

    private val viewModelProvider by viewModels<BreakdownInfoViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBreakdownInfoBottomSheetBinding
        get() = FragmentBreakdownInfoBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
    }

    private fun setupUI() {
        when (args.type) {
            ARGS_INFO -> {
                infoAdapter = QuestionAnswerAdapter()
                binding.rvBreakdownInfo.adapter = infoAdapter
                binding.rvBreakdownInfo.addItemDecorationIfNoneAdded(spaceDecorator)
            }
            ARGS_CHARGES -> {
                binding.llInfo.isVisible = true
                chargesAdapter = LoanBreakdownInfoAdapter()
                binding.rvBreakdownInfo.adapter = chargesAdapter
                binding.rvBreakdownInfo.addItemDecorationIfNoneAdded(spaceDecorator)
            }
            ARGS_ELIGIBILITY_RANGE -> {
                rangeAdapter = QuestionAnswerAdapter()
                binding.rvBreakdownInfo.adapter = rangeAdapter
                binding.rvBreakdownInfo.addItemDecorationIfNoneAdded(spaceDecorator, dividerDecorator)
            }
        }
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setEligibilityData(it.lenderEligibilityRange)
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
                viewModel.loanDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        when (args.type) {
                            ARGS_INFO -> {
                                it?.applicationDetails?.loanSummary?.readyCashBreakdownDescription?.let {
                                    infoAdapter?.submitList(it)
                                }
                            }

                            ARGS_CHARGES -> {
                                it?.applicationDetails?.loanSummary?.readyCashChargesDescription?.let {
                                    chargesAdapter?.submitList(it)
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setEligibilityData(data: LendingEligibilityRange?) {
        data ?: return
        val finalList = arrayListOf<QuestionAnswer>()
        finalList.add(QuestionAnswer(question = getCustomString(MR.strings.feature_lending_detail_loan_amount), answer = data.loanAmount))
        finalList.add(QuestionAnswer(question = getCustomString(MR.strings.feature_lending_detail_interest_rate), answer = data.interestRate))
        finalList.add(QuestionAnswer(question = getCustomString(MR.strings.feature_lending_detail_tenure), answer = data.tenure))
        rangeAdapter?.submitList(finalList)
    }

    private fun getData() {
        when (args.type) {
            ARGS_INFO, ARGS_CHARGES -> {
                viewModel.fetchLoanDetails(LendingConstants.LendingApplicationCheckpoints.LOAN_SUMMARY, true, args.loanId)
            }
            ARGS_ELIGIBILITY_RANGE -> {
                viewModel.fetchStaticContent(LendingConstants.StaticContentType.LENDER_ELIGIBILITY_RANGE, args.loanId)
            }
        }
    }

    companion object {
        const val ARGS_INFO = "ARGS_INFO"
        const val ARGS_CHARGES = "ARGS_CHARGES"
        const val ARGS_ELIGIBILITY_RANGE = "ARGS_ELIGIBILITY_RANGE"
    }
}