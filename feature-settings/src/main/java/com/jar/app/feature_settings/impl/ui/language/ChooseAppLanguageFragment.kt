package com.jar.app.feature_settings.impl.ui.language

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.jar.app.base.data.event.RecreateAppEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_settings.databinding.FragmentChooseAppLanguageBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_settings.util.SettingsConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.desc.StringDesc
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class ChooseAppLanguageFragment : BaseFragment<FragmentChooseAppLanguageBinding>() {

    private var adapter: ChooseAppLanguageAdapter? = null

    private val viewModel by viewModels<ChooseAppLanguageViewModel> { defaultViewModelProviderFactory }

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(4.dp, 4.dp)

    private val timeInit = System.currentTimeMillis()

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var isLanguagePreSelectionUpdated = false

    private var isShownEventFired = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChooseAppLanguageBinding
        get() = FragmentChooseAppLanguageBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        viewModel.getSupportedLanguages()
    }

    private fun setupUI() {
        adapter = ChooseAppLanguageAdapter {
            viewModel.selectedLanguage = it
            viewModel.updateSelectedLanguage(it)
            updateDoneButton()
        }

        binding.rvLanguages.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLanguages.adapter = adapter
        binding.rvLanguages.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun setupListeners() {
        binding.btnDone.setDebounceClickListener {
            if (viewModel.selectedLanguage != null) {

                val code = viewModel.selectedLanguage?.code.orEmpty()
                val name = viewModel.selectedLanguage?.language.orEmpty()
                analyticsHandler.postEvent(
                    SettingsEventKey.Clicked_Apply_LanguageScreen,
                    mapOf(SettingsEventKey.FromScreen to SettingsEventKey.settings,
                        SettingsEventKey.languagesShown to viewModel.languages.toString(),
                        SettingsEventKey.languageSelected to name
                    )
                )
                viewModel.selectedLanguage?.language?.let {
                    analyticsHandler.setUserProperty(
                        listOf(
                            Pair(
                                SettingsConstants.AnalyticsKey.Language,
                                it
                            )
                        )
                    )
                }
                val recreate = prefs.getCurrentLanguageCode() != viewModel.selectedLanguage?.code
                if (recreate) {
                    prefs.setShouldShowSplashScreen(false)
                    prefs.setSelectedLanguageCode(code)
                    prefs.setSelectedLanguageName(name)
                    //To set the language code in moko resource
                    StringDesc.localeType = StringDesc.LocaleType.Custom(code)
                    prefs.setShouldShowSplashScreen(false)
                    EventBus.getDefault().post(RecreateAppEvent())
                }
            } else {
                getCustomString(SettingsMR.strings.feature_settings_please_select_app_language).snackBar(
                    binding.root
                )
            }
        }

        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.languageLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.progressBar.isVisible = false
                adapter?.submitList(it.languages)
                viewModel.languages = it.languages.toMutableList()
                if (!isLanguagePreSelectionUpdated && prefs.getCurrentLanguageCode().isNotEmpty()) {
                    viewModel.updateSelectedLanguage(prefs.getCurrentLanguageCode())
                    isLanguagePreSelectionUpdated = true
                }
                if(!isShownEventFired){
                    analyticsHandler.postEvent(SettingsEventKey.Shown_LanguageScreen,
                        mapOf(SettingsEventKey.FromScreen to SettingsEventKey.settings,
                            SettingsEventKey.languagesShown to viewModel.languages.toString(),
                            SettingsEventKey.defaultLanguageShown to viewModel.selectedLanguage.toString())
                    )
                    isShownEventFired = true
                }
            },
            onError = {
                binding.progressBar.isVisible = false
            }
        )
    }

    private fun updateDoneButton() {
        viewModel.selectedLanguage?.let {
            val currentLocale = prefs.getCurrentLanguageCode()
            val configuration = Configuration(requireContext().resources.configuration)
            configuration.setLocale(Locale(it.code))
            val localeString =
                getCustomString(
                    requireContext().createConfigurationContext(configuration),
                    SettingsMR.strings.feature_settings_apply
                )
            binding.btnDone.setText(
                if (currentLocale.equals(it.code, true))
                    getCustomString(SettingsMR.strings.feature_settings_okay2)
                else "${getCustomString(SettingsMR.strings.feature_settings_apply)} / $localeString"
            )
        }
    }

    override fun onDestroy() {
        analyticsHandler.postEvent(
            EventKey.Exit_LanguageScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroy()
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }
}