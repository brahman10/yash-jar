package com.jar.app.core_compose_ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.activity.BaseActivity
import com.jar.app.core_compose_ui.theme.JarTheme
import com.jar.app.core_ui.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseComposeFragment : Fragment(), BaseNavigation, BaseResources {

    private var toolbar: Toolbar? = null

    private var job: Job? = null
    protected lateinit var uiScope: CoroutineScope

    abstract fun setupAppBar()

    @Composable
    abstract fun RenderScreen()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JarTheme {
                    RenderScreen()
                }
            }
        }
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
    }

    protected fun setNavigationBarColor(@ColorRes color: Int = R.color.color_272239) {
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireActivity(), color)
    }
}
