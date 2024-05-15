package com.jar.app.base.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.activity.BaseActivity
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseDialogFragment<VB : ViewBinding> :
    DialogFragment(),
    BaseNavigation,
    BaseResources {

    companion object {
        val DEFAULT_CONFIG = DialogFragmentConfig()
    }

    private var _binding: ViewBinding? = null

    protected val binding: VB
        get() = _binding as VB


    abstract val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    abstract val dialogConfig: DialogFragmentConfig

    private var job: Job? = null
    protected lateinit var uiScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = customBindingInflater.invoke(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        uiScope = CoroutineScope(Dispatchers.Main + job!!)
        dialog?.setCancelable(dialogConfig.isCancellable)
        dialog?.setCanceledOnTouchOutside(dialogConfig.isCancellable)
        setup()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //to set full width
        if (dialogConfig.shouldShowFullScreen)
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
    }

    abstract fun setup()

    override fun onDestroyView() {
        job?.cancel()
        _binding = null
        super.onDestroyView()
    }

    protected fun showProgressBar() {
        (requireActivity() as BaseActivity<*>).showProgressBar()
    }

    protected fun isBindingInitialized() = _binding != null

    protected fun dismissProgressBar() {
        (requireActivity() as BaseActivity<*>).dismissProgressBar()
    }

    data class DialogFragmentConfig(
        val isCancellable: Boolean = false,
        val shouldShowFullScreen: Boolean = true
    )
}