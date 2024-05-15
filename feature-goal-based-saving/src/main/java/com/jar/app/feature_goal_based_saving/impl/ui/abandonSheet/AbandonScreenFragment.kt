package com.jar.app.feature_goal_based_saving.impl.ui.abandonSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.databinding.FragmentAbandonScreenBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.shared.data.model.AbandonedScreenResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class AbandonScreenFragment: BaseBottomSheetDialogFragment<FragmentAbandonScreenBinding>() {

    private val viewModel: AbandonScreenFragmentViewModel by viewModels { defaultViewModelProviderFactory }
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAbandonScreenBinding
        get() = FragmentAbandonScreenBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG


    override fun setup() {
        viewModel.handleActions(
            AbandonScreenFragmentAction.Init
        )
        observeState()
    }

    private fun setUpView(abandonedScreenResponse: AbandonedScreenResponse) {
        with(binding) {
            header.text = abandonedScreenResponse.header
            description.text = abandonedScreenResponse.description
            Glide.with(image.context).load(abandonedScreenResponse.image).into(image)
            footerButton.setText(abandonedScreenResponse.footerButtonText ?: "")
            exitButton.setText(abandonedScreenResponse.exitCta?.text?:"")
            overlappingViewText.text = abandonedScreenResponse.socialProofingText
            profileOverlayView.submitProfilePics(
                listOf(
                    "https://i.pravatar.cc/150?img=60",
                    "https://i.pravatar.cc/150?img=52",
                    "https://i.pravatar.cc/150?img=47",
                )
            )
            setupListener(abandonedScreenResponse)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when(it) {
                    is AbandonScreenFragmentState.OnData -> {
                        it.abandonedScreenResponse.let {
                            setUpView(it)
                        }
                    }
                    AbandonScreenFragmentState.OnClose -> {
                        dismissAllowingStateLoss()
                    }
                    is AbandonScreenFragmentState.OnIWllDoItLater -> {
                        try{
                            subSharedViewModel.handleActions(GoalBasedSavingActions.OnGoalTitleChange(""))
                            subSharedViewModel.handleActions(
                                GoalBasedSavingActions.OnGoalSelectedFromList(
                                GoalRecommendedItem("","","")
                            ))
                            subSharedViewModel.handleActions(GoalBasedSavingActions.OnDurationChanged(-1))
                            subSharedViewModel.handleActions(GoalBasedSavingActions.OnAmountChanged(""))
                            EventBus.getDefault().post(
                                GoToHomeEvent(
                                    "AbandonScreenFragment",
                                    BaseConstants.HomeBottomNavigationScreen.HOME
                                )
                            )
                        } catch (e: Exception) {
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                it.setBackgroundResource(android.R.color.transparent)
                val behaviour = BottomSheetBehavior.from(it)
                val bottomSheetConfig = BaseBottomSheetDialogFragment.BottomSheetConfig()
                behaviour.isHideable = bottomSheetConfig.isHideable
                behaviour.skipCollapsed = bottomSheetConfig.skipCollapsed
                behaviour.isDraggable = bottomSheetConfig.isDraggable
                behaviour.isFitToContents = bottomSheetConfig.isFitToContent
                behaviour.halfExpandedRatio = bottomSheetConfig.halfExpandedRatio
                behaviour.expandedOffset = bottomSheetConfig.expandedOffSet
                behaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        return dialog
    }

    private fun setupListener(abandonedScreenResponse: AbandonedScreenResponse) {
        with(binding) {
            closeButton.setDebounceClickListener {
                viewModel.handleActions(AbandonScreenFragmentAction.OnClickOnClose)
            }
            footerButton.setDebounceClickListener {
                viewModel.handleActions(
                    AbandonScreenFragmentAction.OnClickOnContinue
                )
                viewModel.handleActions(AbandonScreenFragmentAction.OnClickOnClose)
            }
            exitButton.setDebounceClickListener {
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.OnAmountChanged("")
                )
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.OnDurationChanged(null)
                )
                viewModel.handleActions(AbandonScreenFragmentAction.OnClickOnExit(
                    abandonedScreenResponse.exitCta?.deeplink ?: ""
                ))
            }
        }
    }
}