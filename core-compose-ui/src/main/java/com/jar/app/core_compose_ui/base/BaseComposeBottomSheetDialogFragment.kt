package com.jar.app.core_compose_ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.activity.BaseActivity
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_compose_ui.theme.JarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseComposeBottomSheetDialogFragment : BottomSheetDialogFragment(), BaseNavigation {
    companion object {
        val DEFAULT_CONFIG = BottomSheetConfig()
    }

    abstract val bottomSheetConfig: BottomSheetConfig

    private var job: Job? = null
    protected lateinit var uiScope: CoroutineScope


    @Composable
    abstract fun RenderBottomSheet()

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
                    RenderBottomSheet()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        uiScope = CoroutineScope(Dispatchers.Main + job!!)
        isCancelable = bottomSheetConfig.isCancellable
        dialog?.setCanceledOnTouchOutside(bottomSheetConfig.isCancellable)
        setup()
    }

    abstract fun setup()

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
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
                behaviour.isHideable = bottomSheetConfig.isHideable
                behaviour.skipCollapsed = bottomSheetConfig.skipCollapsed
                behaviour.isDraggable = bottomSheetConfig.isDraggable
                behaviour.isFitToContents = bottomSheetConfig.isFitToContent
                behaviour.halfExpandedRatio = bottomSheetConfig.halfExpandedRatio
                behaviour.expandedOffset = bottomSheetConfig.expandedOffSet
                if (bottomSheetConfig.shouldShowFullHeight)
                    setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    fun getRootView() = dialog?.window?.decorView!!

    protected fun showProgressBar() {
        (requireActivity() as BaseActivity<*>).showProgressBar()
    }

    protected fun dismissProgressBar() {
        (requireActivity() as BaseActivity<*>).dismissProgressBar()
    }

    protected fun expandBottomSheet() {
        try {
            val behavior = BottomSheetBehavior.from(requireView().parent as View)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //    protected fun isBindingInitialized() = _binding != null
    data class BottomSheetConfig(
        val isHideable: Boolean = true,
        val skipCollapsed: Boolean = true,
        val shouldShowFullHeight: Boolean = false,
        val isCancellable: Boolean = true,
        val isDraggable: Boolean = true,
        val isFitToContent: Boolean = true,
        val halfExpandedRatio: Float = 0.9f,
        val expandedOffSet: Int = 0
    )
}