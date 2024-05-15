package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalName

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.toast
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentEnterGoalNameBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.SaveForAdapter
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.GoalSelectionScreenV2
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class EnterTitleFragment: BaseFragment<FragmentEnterGoalNameBinding>() {
    private val viewModel by viewModels<EnterTitleFragmentViewModel> { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private var saveForAdapter: SaveForAdapter? = null
    private var previousGoalName: String? = null
    private var currentSelectedItem = -1
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
            private fun handleBackPress() {
                val action = EnterTitleFragmentDirections.actionSavingForTitleFragmentToAbandonDailog()
                subSharedViewModel.handleActions(
                    actions = GoalBasedSavingActions.NavigateWithDirection(
                        action
                    )
                )
            }
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
    private val list: MutableList<GoalRecommendedItem> by lazy {
        mutableListOf()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEnterGoalNameBinding
        get() = FragmentEnterGoalNameBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handelAction(
            EnterTitleFragmentAction.Init
        )
        viewModel.handelAction(
            EnterTitleFragmentAction.SendShownEvent
        )
        sharedViewModel.handleActions(
            GoalBasedSavingActions.HideAppBar(false)
        )
        previousGoalName = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()) {
            subSharedViewModel.state.value.onGoalSelectedFromList?.name
        } else {
            subSharedViewModel.state.value.onGoalTitleChange
        }
        registerBackPressDispatcher()
        initView()
        observeState()
        observeLiveData()
        setClickListener()
        checkAndNavigateForward()
    }

    private fun checkAndNavigateForward() {
        uiScope.launch {
            if ((subSharedViewModel.state.value.onGoalTitleChange.isNullOrBlank().not()
                        || subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrBlank().not())
                && subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()
                && subSharedViewModel.shouldNavigateForward
            ) {
                subSharedViewModel.handleActions(
                    actions = GoalBasedSavingActions.NavigateTo(R.id.action_savingForTitleFragment_to_userAmountAndDuration)
                )
            } else {
                val customGoalName = subSharedViewModel.state.value.onGoalTitleChange
                if (customGoalName?.length ?: 0 > 0) {
                    openCustomGoalNameBottomSheet()
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.intro.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = { goalFirstQuestionResponse ->
                subSharedViewModel.state.value.defaultGoalImage = goalFirstQuestionResponse.defaultIconWithBottom

                // initializing the variables in viewModel
                with(viewModel) {
                    maxLength = goalFirstQuestionResponse.charMaxLimit ?: 20
                    minLength = goalFirstQuestionResponse.charMinLimit ?: 4
                    goalIcon = goalFirstQuestionResponse.nameInputDefaultIcon ?: ""
                    questionName = goalFirstQuestionResponse.nameQuestion ?: ""
                    nameInputText = goalFirstQuestionResponse.nameInputText ?: ""
                }

                //setting up the views
                with(binding) {
                    tv.text = goalFirstQuestionResponse.nameQuestion
                    goalFirstQuestionResponse.footerButtonText?.let { it1 -> this.btnNext.setText(it1) }
                }
                goalFirstQuestionResponse.goalRecommendedItems?.let {
                    list.clear()
                    list.addAll(
                        (it as? List<GoalRecommendedItem>) ?: emptyList()
                    )
                    list.add(
                        GoalRecommendedItem("", goalFirstQuestionResponse.nameInputDefaultIcon, "+ Add Goal", isSelected = false, isCustomInput = true)
                    )
                    val previouslySelectedIndex = list.indexOfFirst {
                        it.name == subSharedViewModel.state.value.onGoalSelectedFromList?.name
                    }
                    if (previouslySelectedIndex != -1) {
                        currentSelectedItem = previouslySelectedIndex
                        list[previouslySelectedIndex].isSelected = true
                    }
                    saveForAdapter?.notifyDataSetChanged()
                    view?.post {
                        subSharedViewModel.handleActions(
                            GoalBasedSavingActions.ScrollToEnd
                        )
                    }

                }
            },
            onError = {
                it.toast(binding.root)
            }
        )
    }

    private fun initView() {
        subSharedViewModel?.handleActions(
            actions = GoalBasedSavingActions.OnStepChange(
                GOAL_BASED_SAVING_STEPS.GOAL_NAME
            )
        )
        binding.rvSaveFor.apply {
            saveForAdapter = SaveForAdapter(list) {pos, title ->
                if (currentSelectedItem != pos) {
                    list[pos].isSelected = true
                    if (currentSelectedItem != -1) {
                        list[currentSelectedItem].isSelected = false
                        saveForAdapter?.notifyItemChanged(currentSelectedItem)
                    }
                    saveForAdapter?.notifyItemChanged(pos)
                    currentSelectedItem = pos
                    if (list[pos].isCustomInput) {
                        openCustomGoalNameBottomSheet()
                    } else {
                        subSharedViewModel
                            .handleActions(
                                GoalBasedSavingActions.OnGoalTitleChange("")
                            )
                        subSharedViewModel.handleActions(
                            GoalBasedSavingActions.OnGoalSelectedFromList(
                                list[pos]
                            )
                        )
                    }
                } else {
                    if (list[pos].isCustomInput) {
                        openCustomGoalNameBottomSheet()
                    } else {
                        list[pos].isSelected = false
                        saveForAdapter?.notifyItemChanged(pos)
                        currentSelectedItem = -1
                        subSharedViewModel
                            .handleActions(
                                GoalBasedSavingActions.OnGoalTitleChange("")
                            )
                        subSharedViewModel.handleActions(
                            GoalBasedSavingActions.OnGoalSelectedFromList(
                                // -1 here indicates that there is nothing selected from the goal list options
                                GoalRecommendedItem("-1", "-1" ,"")
                            )
                        )
                    }
                }
                vibrate(vibrator)
            }
            adapter = saveForAdapter
            layoutManager  = GridLayoutManager(this@EnterTitleFragment.context, 4)
        }
    }

    private fun openCustomGoalNameBottomSheet() {
        val action =
            EnterTitleFragmentDirections.actionSavingForTitleFragmentToEnterUserGoalDialog(
                viewModel.questionName,
                viewModel.nameInputText,
                viewModel.minLength,
                viewModel.maxLength,
                viewModel.goalIcon,
            )
        subSharedViewModel.handleActions(
            actions = GoalBasedSavingActions.NavigateWithDirection(
                action
            )
        )
    }

    private fun observeState() {
        uiScope.launch {
            subSharedViewModel
                .state
                .collect {
                    it.onGoalTitleChange?.let {
                        if (it.isEmpty().not()) {
                            if (currentSelectedItem != -1) {
                                subSharedViewModel.handleActions(
                                    GoalBasedSavingActions.OnGoalSelectedFromList(GoalRecommendedItem("-1","-1",""))
                                )
                            }
                            binding.btnNext.apply {
                                isEnabled = true
                                alpha = 1f
                            }
                        } else {
                            binding.btnNext.apply {
                                isEnabled =
                                    subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()
                                alpha = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()) {
                                    1f
                                } else {
                                    0.5f
                                }
                            }
                        }
                    }

                    it.onGoalSelectedFromList?.let {
                        if (it.name.isNullOrEmpty().not()) {
                            binding.btnNext.apply {
                                isEnabled = true
                                alpha = 1f
                            }
                        } else {
                            binding.btnNext.apply {
                                isEnabled =
                                    subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()
                                alpha = if (subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()) {
                                    1f
                                } else {
                                    0.5f
                                }
                            }
                        }
                    }

                    it.userEntryFragmentHeight?.let {
                        binding.root.layoutParams = binding.root.layoutParams.apply {
                            height = it
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                subSharedViewModel.onCustomButtonSheet.collect {
                    if (it) {
                        val customGoalName = subSharedViewModel.state.value.onGoalTitleChange
                        if ((customGoalName?.length ?: 0) > 0) {
                            list.getOrNull(list.size-1)?.isSelected = true
                            saveForAdapter?.notifyItemChanged(list.size - 1)
                            binding.btnNext.performClick()
                        } else {
                            subSharedViewModel.handleActions(
                                GoalBasedSavingActions.OnGoalSelectedFromList(
                                    GoalRecommendedItem("-1", "-1" ,"")
                                )
                            )
                            list.getOrNull(list.size-1)?.isSelected = false
                            saveForAdapter?.notifyItemChanged(list.size - 1)
                        }
                    }
                }
            }
        }
    }

    private fun setClickListener() {
        binding.btnNext.setOnClickListener {
            vibrate(vibrator)
            val goalName = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()) {
                subSharedViewModel.state.value.onGoalSelectedFromList?.name
            } else {
                subSharedViewModel.state.value.onGoalTitleChange
            }
            if (goalName?.lowercase() != previousGoalName?.lowercase()) {
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.OnAmountChanged("")
                )
            }
            viewModel.handelAction(
                EnterTitleFragmentAction.OnClickedOnNext(
                    screenType = GoalSelectionScreenV2,
                    clickAction = GBSAnalyticsConstants.nextClick,
                    goalselectionprocess = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty()) {
                        "Typed"
                    } else {
                        "Select from list"
                    },
                    finalgoalselected = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty()){
                        ""
                    }
                    else {
                         subSharedViewModel.state.value.onGoalSelectedFromList?.name ?: ""
                         },
                )
            )
            subSharedViewModel.handleActions(
                actions = GoalBasedSavingActions.NavigateTo(R.id.action_savingForTitleFragment_to_userAmountAndDuration)
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

}