package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalName

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.databinding.FragmentGoalNameBottomSheetBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.openKeyboard
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.Continue
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.ManualGoalselectionBottomSheetV2
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.GoalNameScreen.cross
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.KeyboardObserver
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class GoalNameBottomSheet: BaseBottomSheetDialogFragment<FragmentGoalNameBottomSheetBinding>() {

    private val args by navArgs<GoalNameBottomSheetArgs>()
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private val viewModel by viewModels<GoalNameBottomSheetViewModel> {defaultViewModelProviderFactory}
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
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.OnGoalTitleChange("")
                )
                popBackStack()
            }
        }
    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoalNameBottomSheetBinding
        get() = FragmentGoalNameBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        view?.post {
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        } ?: kotlin.run {
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        return dialog
    }

    override fun setup() {
        registerBackPressDispatcher()
        if (subSharedViewModel.state.value.onGoalTitleChange.isNullOrBlank()) {
            disableButton()
        } else {
            enableButton()
        }
        binding.tvCharLimit.text = "Characters: ${subSharedViewModel.state.value.onGoalTitleChange?.length ?: 0}/${args.goalMaxLength}"
        binding.tvGoalName.apply {
            setText(
                subSharedViewModel.state.value.onGoalTitleChange ?: ""
            )
        }
        Glide.with(
            binding.ivIcon
        ).load(args.goalIconUrl).into(
            binding.ivIcon
        )
        binding.label.text = args.questionName
        binding.tvGoalName.apply {
            maxLines = 1
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            GBSAnalyticsConstants.screen_type to GBSAnalyticsConstants.GoalNameScreen.GoalSelectionScreen,
                            GBSAnalyticsConstants.clickaction to GBSAnalyticsConstants.GoalNameScreen.goalTyped,
                            GBSAnalyticsConstants.GoalNameScreen.goalTyped to binding.tvGoalName.text!!,
                        )
                    )
                    vibrate(vibrator)
                    popBackStack()
                    return@setOnKeyListener true
                }
                false
            }
            requestFocus()
            activity?.openKeyboard(this)
        }
        setUpListeners()
        observeState()
        analyticsHandler.postEvent(
            GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown,
            mapOf(
                GBSAnalyticsConstants.screen_type to ManualGoalselectionBottomSheetV2
            )
        )
    }

    private fun setUpListeners() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(args.goalMaxLength + 1)
        binding.tvGoalName.filters = filterArray
        binding.tvGoalName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    binding.tvGoalName.removeTextChangedListener(this)
                    val result = s.toString()
                    if (result.isNotEmpty()) {
                        if (result.get(0) == ' ') {
                            binding.tvGoalName.setText(result.trimStart())
                        }

                        if (result[result.length-1] == '\n') {
                            subSharedViewModel.handleActions(
                                GoalBasedSavingActions.OnGoalTitleChange(
                                    binding.tvGoalName.text.toString().filter { it != '\n' }.trim()
                                )
                            )
                            popBackStack()
                            return
                        }
                    }

                    val previousCursorPosition = binding.tvGoalName.selectionStart

                    if (result.length > args.goalMaxLength) {
                        val finalStringLength = result.substring(0, args.goalMaxLength).length
                        binding.tvGoalName.setText(result.substring(0, finalStringLength))
                        val newPosition = if (previousCursorPosition < finalStringLength) previousCursorPosition else finalStringLength
                        binding.tvGoalName.setSelection(newPosition)
                        shakeView(binding.tvCharLimit)
                    }

                    if (result.isEmpty() || result.length < args.goalMinLength) {
                        binding.icClearText.visibility = View.GONE
                        disableButton()
                    } else if (result.length >= args.goalMinLength) {
                        binding.icClearText.visibility = View.VISIBLE
                        enableButton()
                    }
                    if (result.length <= args.goalMaxLength)
                        binding.tvCharLimit.text = "Characters: ${result?.length}/${args.goalMaxLength}"
                    binding.tvGoalName.addTextChangedListener(this)
                }
            }
        )

        binding.icClearText.setOnClickListener {
            binding.tvGoalName.setText("")
        }
        binding.ivClose.setOnClickListener {
            subSharedViewModel
                .handleActions(
                    GoalBasedSavingActions.OnGoalTitleChange("")
                )
            analyticsHandler.postEvent(
                SavingsGoal_ScreenClicked,
                mapOf(
                    GBSAnalyticsConstants.screen_type to ManualGoalselectionBottomSheetV2,
                    GBSAnalyticsConstants.clickaction to cross,
                    "goaltyped" to (binding.tvGoalName.text ?: ""),
                )
            )
            popBackStack()
        }
        binding.btnNext.setDebounceClickListener {
            subSharedViewModel.handleActions(
                GoalBasedSavingActions.OnGoalTitleChange(
                    binding.tvGoalName.text.toString()
                )
            )
            analyticsHandler.postEvent(
                SavingsGoal_ScreenClicked,
                mapOf(
                    GBSAnalyticsConstants.screen_type to ManualGoalselectionBottomSheetV2,
                    GBSAnalyticsConstants.clickaction to Continue,
                    "goaltyped" to (binding.tvGoalName.text ?: ""),
                )
            )
            popBackStack()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        subSharedViewModel.handleActions(
            GoalBasedSavingActions.OnDismissCustomGoalNameBottomSheet
        )
    }

    private fun observeState() {
        uiScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                subSharedViewModel.state.collect {
                    it.onGoalTitleChange?.let {

                    }
                }
            }
        }
    }
    private fun shakeView(view: View) {
        val shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shakeAnimator.duration = 500
        shakeAnimator.start()
    }

    private fun disableButton() {
        binding.btnNext.apply {
            alpha = 0.5f
            isEnabled = false
        }
    }

    private fun enableButton() {
        binding.btnNext.apply {
            alpha = 1f
            isEnabled = true
        }
    }
}
