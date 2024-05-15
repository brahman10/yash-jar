package com.jar.app.feature_goal_based_saving.impl.ui.userEntry

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentUserEntryBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class UserEntryFragment: BaseFragment<FragmentUserEntryBinding>() {

    init {
        retainInstance = true
    }
    private var stepsRvAdapter: UserEnteredFieldAdapter? = null
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private lateinit var navController: NavController

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserEntryBinding
        get() = FragmentUserEntryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.userEntryFragment) as NavHostFragment
        navController = nestedNavHostFragment.navController
        navController.graph =
            nestedNavHostFragment.navController.navInflater.inflate(R.navigation.user_entry_nav)
        observeState()
        binding.root?.post() {
            // get the display metrics of the current window
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            // calculate the status bar height
            val statusBarId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (statusBarId > 0) resources.getDimensionPixelSize(statusBarId) else 0

            // calculate the navigation bar height
            val navigationBarId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            val navigationBarHeight = if (navigationBarId > 0) resources.getDimensionPixelSize(navigationBarId) else 0

            val heightPixels = displayMetrics.heightPixels - statusBarHeight - navigationBarHeight

            try {
                if (binding != null){
                    val h = heightPixels
                    subSharedViewModel.handleActions(
                        GoalBasedSavingActions.OnFragmentHostContainerHeight(h)
                    )
                }
            } catch (_: Exception) {}
        }
        observeNestedScrollableView()
    }

    private fun observeNestedScrollableView() {
        val nestedScrollView: NestedScrollView = binding.nestedScrollView

        // Initially store the height of NestedScrollView
        var initialHeight = nestedScrollView.height

        val observer = nestedScrollView.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Compare if the current height is greater than initial height
                if (nestedScrollView.height > initialHeight) {
                    // The height of NestedScrollView has increased.
                    binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
                }
                initialHeight = nestedScrollView.height
            }
        })

    }

    private fun observeState() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    subSharedViewModel.navigateTo.collect() {
                        it?.let {
                            try {
                                navController.navigate(it)
                            } catch (_: Exception) {}
                        }
                    }
                }
            }

            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    subSharedViewModel.navigateWithDirection.collect() {
                        it?.let {
                            try {
                                navController.navigate(it)
                            } catch (_: Exception) {}
                        }
                    }
                }
            }

            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    subSharedViewModel.popUserEntryFragment.collect() {
                        it?.let {
                            popBackStack()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                subSharedViewModel.scrollToEndFlow.collect() {
                    it?.let {
                        binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
                    }
                }
            }
        }

    }

}
