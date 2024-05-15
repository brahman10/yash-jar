package com.jar.app.base.ui.activity

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.jar.app.base.R
import com.jar.app.base.di.PreferenceEntryPoint
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_base.util.orFalse
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import java.util.*

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), BaseNavigation, BaseResources {

    private var _binding: ViewBinding? = null
    abstract val customBindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    protected lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    private var job: Job? = null
    protected lateinit var uiScope: CoroutineScope

    companion object {
        private const val TIME_INTERVAL = 2000
    }

    private var backPressedTime: Long = 0L

    private var toast: Toast? = null

    private var kProgressHUD: KProgressHUD? = null

    abstract fun setup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        uiScope = CoroutineScope(Dispatchers.Main + job!!)
        _binding = customBindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        setup()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val prefs =
                EntryPointAccessors.fromApplication(newBase, PreferenceEntryPoint::class.java)
            val selectedLanguage = prefs.getPrefs().getCurrentLanguageCode()
            val deviceLanguageCode =
                ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]?.language ?: "en"
            val configuration = newBase.resources.configuration
            if (selectedLanguage.isNotBlank()) {
                configuration.setLocale(Locale(selectedLanguage))
            } else {
                configuration.setLocale(Locale(deviceLanguageCode))
            }
            super.attachBaseContext(newBase.createConfigurationContext(configuration))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    protected fun initializeDoubleBackPressToExit() {
        if (backPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            finishAffinity()
        } else {
            toast?.cancel()
            toast = Toast.makeText(
                applicationContext,
                R.string.press_back_again_to_exit,
                Toast.LENGTH_SHORT
            )
            toast?.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()
        toast?.cancel()
        job?.cancel()
        _binding = null
    }

    fun showProgressBar() {
        if (kProgressHUD == null)
            kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setAnimationSpeed(2)
                .setCancellable(false)
                .setDimAmount(0.5f)
        if (kProgressHUD?.isShowing == false)
            kProgressHUD?.show()

    }

    fun dismissProgressBar() {
        if (kProgressHUD?.isShowing.orFalse())
            kProgressHUD?.dismiss()
    }
}
