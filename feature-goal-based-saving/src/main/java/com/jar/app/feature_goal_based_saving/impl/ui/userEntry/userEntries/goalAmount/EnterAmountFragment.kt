package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_utils.data.RoundAmountToIntInputFilter
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentGoalAmountBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.INRCurrencyFormatter
import com.jar.app.feature_goal_based_saving.impl.extensions.getCommaFormattedString
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.KeyboardObserver
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalAmountResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class EnterAmountFragment: BaseFragment<FragmentGoalAmountBinding>() {
    private val viewModel: EnterAmountFragmentViewModel by viewModels { defaultViewModelProviderFactory  }
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private var keyboardObserver: KeyboardObserver? = null
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
            private fun handleBackPress() {
                val action = EnterAmountFragmentDirections.actionEnterAmountFragmentToAbandonDailog()
                subSharedViewModel.handleActions(
                    actions = GoalBasedSavingActions.NavigateWithDirection(
                        action
                    )
                )
            }
        }
    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoalAmountBinding
        get() = FragmentGoalAmountBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        subSharedViewModel.handleActions(
            GoalBasedSavingActions.ScrollToEnd
        )
        registerBackPressDispatcher()
        setupListeners()
        observeLiveData()
        checkAndNavigateForward()
    }

    private fun checkAndNavigateForward() {
        if ((subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()
                    || subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not())
            && subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()
            && subSharedViewModel.state.value.onDurationChanged != null) {
            subSharedViewModel.handleActions(
                actions = GoalBasedSavingActions.NavigateTo(R.id.action_enterAmountFragment_to_enterDurationFragment)
            )
        } else {
            viewModel.handelAction(
                EnterAmountFragmentAction.Init
            )
        }
    }

    private fun observeLiveData() {
        uiScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect() {
                    it.loading?.let {
                        if (it) {
                            showProgressBar()
                        } else {
                            dismissProgressBar()
                        }
                    }
                    it.goalAmountResponse?.let {
                        setUpView(it)
                        observeState()
                    }
                }
            }
        }
    }

    private fun setUpView(goalAmountResponse: GoalAmountResponse) {
        val savingForTitle = if (subSharedViewModel.state.value.onGoalTitleChange?.isEmpty()?.not() == true) {
            subSharedViewModel.state.value.onGoalTitleChange
        } else {
            subSharedViewModel.state.value.onGoalSelectedFromList?.name
        }

        // Get the starting and ending indices of the portion you want to replace
        val startIndex = goalAmountResponse.amountQuestion?.indexOfFirst {
            it == '{'
        } ?: 0

        val originalQuestion = goalAmountResponse.amountQuestion

        val replaceString = originalQuestion?.replace("{0}", savingForTitle ?: "")
        val originalQuestionSpan = SpannableString(replaceString)
        val goalNameColor = ForegroundColorSpan(Color.parseColor("#C5B0FF"))


        originalQuestionSpan.setSpan(
            goalNameColor,
            startIndex,
            startIndex+(savingForTitle?.length ?: 0),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )

        val boldSpan = StyleSpan(Typeface.BOLD)
        originalQuestionSpan.setSpan(boldSpan,
            startIndex,
            startIndex+(savingForTitle?.length ?: 0),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )

        binding.tv.text = originalQuestionSpan

        with(binding) {
            etAmount.hint = (goalAmountResponse.amountInputText)
            Glide.with(ivInfoIcon).load(goalAmountResponse.generalTip?.icon).into(ivInfoIcon)
            tvInfoMsg.text = goalAmountResponse.generalTip?.message
            Glide.with(ivErrorIcon).load(goalAmountResponse.lowerAmountTip?.icon).into(ivErrorIcon)
            ivErrorIcon.isVisible = false
            viewModel.state.value.goalAmountResponse?.footerButtonText?.let { btnNext.setText(it) }
            binding.etAmount.INRCurrencyFormatter()
        }
    }

    private fun observeState() {
        uiScope.launch {
            subSharedViewModel.state.collect {
                it.userEntryFragmentHeight?.let {
                    binding.root.layoutParams = binding.root.layoutParams.apply {
                        height = it - 20 // it is
                    }
                }
                it.onAmountChanged?.let {
                    if (it.isEmpty().not()) {
                        val amount = try {
                            it.toInt()
                        } catch (e: Exception) {
                            0
                        }
                        if (amount > (viewModel.state.value.goalAmountResponse?.maxAmount ?: 0) || amount == 0) {
                            binding.tvErrorMsg.text = viewModel.state.value.goalAmountResponse?.higherAmountTip?.message ?: ""
                            binding.llErrorContainer.visibility = View.VISIBLE
                            binding.ivErrorIcon.isVisible = true
                            binding.llInfoContainer.visibility = View.GONE
                            binding.ivCorrectAmount.visibility = View.GONE
                            binding.btnNext.apply {
                                isEnabled = false
                                alpha = 0.5f
                            }
                            viewModel.handelAction(
                                EnterAmountFragmentAction.SentAmountChangedEvent(
                                    screenType = "Amount screen",
                                    action = it,
                                    errorMessageShown = viewModel.state.value.goalAmountResponse?.higherAmountTip?.message ?: ""
                                )
                            )
                        } else if (amount < (viewModel.state.value.goalAmountResponse?.minAmount ?: 0)) {
                            binding.tvErrorMsg.text = viewModel.state.value.goalAmountResponse?.lowerAmountTip?.message ?: ""
                            binding.llErrorContainer.visibility = View.VISIBLE
                            binding.ivErrorIcon.isVisible = true
                            binding.llInfoContainer.visibility = View.GONE
                            binding.ivCorrectAmount.visibility = View.GONE
                            binding.btnNext.apply {
                                isEnabled = false
                                alpha = 0.5f
                            }
                            viewModel.handelAction(
                                EnterAmountFragmentAction.SentAmountChangedEvent(
                                    screenType = "Amount screen",
                                    action = it,
                                    errorMessageShown = viewModel.state.value.goalAmountResponse?.lowerAmountTip?.message ?: ""
                                )
                            )
                        } else {
                            enableButton()
                        }
                    } else {
                        disableButton()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        subSharedViewModel.handleActions(
            actions = GoalBasedSavingActions.OnStepChange(
                GOAL_BASED_SAVING_STEPS.GOAL_AMOUNT
            )
        )
        binding.icClearText.setOnClickListener {
            binding.etAmount.setText("")
        }

        binding.etAmount.apply {
            val inputFilter = InputFilter.LengthFilter(9)
            filters = arrayOf(inputFilter)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val amount = p0.toString().replace(",","")
                    if (amount.isNullOrEmpty().not()) {
                        binding.icClearText.visibility = View.VISIBLE
                        binding.ivCorrectAmount.visibility = View.GONE
                    } else {
                        binding.icClearText.visibility = View.GONE
                    }
                    subSharedViewModel.handleActions(
                        GoalBasedSavingActions.OnAmountChanged(amount)
                    )
                }

                override fun afterTextChanged(p0: Editable?) {}

            })
            requestFocus()
            if(subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()) {
                setText(subSharedViewModel.state.value.onAmountChanged?.getCommaFormattedString())
                binding.icClearText.isVisible = false
                binding.ivCorrectAmount.isVisible = true
            }
        }
        binding.btnNext.setOnClickListener {
            vibrate(vibrator)
            viewModel.handelAction(
                EnterAmountFragmentAction.OnNextButtonClicked(
                    binding.etAmount.text.toString()
                )
            )
            subSharedViewModel.handleActions(
                actions = GoalBasedSavingActions.NavigateTo(R.id.action_enterAmountFragment_to_enterDurationFragment)
            )
        }
        keyboardObserver = KeyboardObserver(binding.root, findNavController(), onOpen = {
            binding.llEnterAmount.background = ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_active)
            if(subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()) {
                binding.icClearText.visibility = View.VISIBLE
            }

            binding.ivCorrectAmount.isVisible = false
            binding.btnNext.visibility = View.GONE
            if (binding.llErrorContainer.isVisible.not()) {
                binding.llInfoContainer.visibility = View.VISIBLE
            }
        }) {

            subSharedViewModel.handleActions(
                GoalBasedSavingActions.ScrollToEnd
            )
            binding.btnNext.isVisible = true
            binding.llEnterAmount.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_field_unselected)

            if(subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()
                &&  binding.btnNext.isEnabled) {
                binding.ivCorrectAmount.visibility = View.VISIBLE
            }
            binding.icClearText.visibility = View.GONE
            binding.llInfoContainer.visibility = View.GONE
            binding.etAmount.clearFocus()
        }.apply {
            lifecycle.addObserver(
                this
            )
        }
    }

    private fun enableButton() {
        binding.llErrorContainer.visibility = View.GONE
        binding.ivErrorIcon.isVisible = false
        binding.btnNext.apply {
            isEnabled = true
            alpha = 1f
        }
    }

    private fun disableButton() {
        binding.llErrorContainer.visibility = View.GONE
        binding.ivErrorIcon.isVisible = false
        binding.llInfoContainer.visibility = View.GONE
        binding.btnNext.apply {
            isEnabled = false
            alpha = 0.5f
        }
    }

}