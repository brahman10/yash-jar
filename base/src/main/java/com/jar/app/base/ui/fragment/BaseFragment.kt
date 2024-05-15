package com.jar.app.base.ui.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.activity.BaseActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


abstract class BaseFragment<VB : ViewBinding> : Fragment(), BaseNavigation, BaseResources {

    private var _binding: ViewBinding? = null
    abstract val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private var toolbar: Toolbar? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    private var kProgressHUD: KProgressHUD? = null

    private var job: Job? = null
    private var animatorJob: Job? = null
    protected lateinit var uiScope: CoroutineScope

    abstract fun setupAppBar()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = customBindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        uiScope = CoroutineScope(Dispatchers.Main + job!!)
        setupAppBar()
        setup(savedInstanceState)
    }

    abstract fun setup(savedInstanceState: Bundle?)

    override fun onDestroyView() {
        job?.cancel()
        animatorJob?.cancel()
        _binding = null
        super.onDestroyView()
    }

    protected fun showProgressBar() {
        (requireActivity() as BaseActivity<*>).showProgressBar()
    }

    protected fun dismissProgressBar() {
        (requireActivity() as BaseActivity<*>).dismissProgressBar()
    }

    protected fun setStatusBarColor(@ColorRes color: Int) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireActivity(), color)
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireActivity(), color)
    }

    protected fun setStatusBarColorWithAnimation(
        @ColorRes initialColorRes: Int,
        @ColorRes finalColorRes: Int,
        animationTime: Long
    ) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        val initialColor = ContextCompat.getColor(requireActivity(), initialColorRes)
        val finalColor = ContextCompat.getColor(requireActivity(), finalColorRes)
        val statusBarAnimator = ObjectAnimator.ofObject(
            requireActivity().window,
            "statusBarColor",
            ArgbEvaluator(),
            initialColor,
            finalColor
        )

        val navigationBarAnimator = ObjectAnimator.ofObject(
            requireActivity().window,
            "navigationBarColor",
            ArgbEvaluator(),
            initialColor,
            finalColor
        )

        statusBarAnimator.duration = animationTime // Set your desired duration
        navigationBarAnimator.duration = animationTime // Set your desired duration

        // Start the animators
        animatorJob?.cancel()
        animatorJob = uiScope.launch {
            statusBarAnimator.start()
            navigationBarAnimator.start()
        }

    }

    protected fun isBindingInitialized() = _binding != null
}
