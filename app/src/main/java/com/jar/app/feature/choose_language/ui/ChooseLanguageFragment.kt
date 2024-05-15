package com.jar.app.feature.choose_language.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jar.app.R
import com.jar.app.base.data.event.RecreateAppEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.FragmentChooseLanguageBinding
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature.choose_language.util.ChooseLanguageConstants
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.desc.StringDesc
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class ChooseLanguageFragment : BaseFragment<FragmentChooseLanguageBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChooseLanguageBinding
        get() = FragmentChooseLanguageBinding::inflate


    private var adapter: ChooseLanguageAdapter? = null

    private val viewModel by viewModels<ChooseLanguageFragmentViewModel> { defaultViewModelProviderFactory }

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(4.dp, 4.dp)

    private val timeInit = System.currentTimeMillis()

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ChooseLanguageFragmentArgs>()

    private var isLanguagePreSelectionUpdated = false

    private var isShownEventFired = false

    override fun onDestroy() {
        analyticsHandler.postEvent(
            EventKey.Exit_LanguageScreen_Onboarding,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroy()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        viewModel.getSupportedLanguages()
    }

    private fun setupUI() {
        adapter = ChooseLanguageAdapter {
            viewModel.selectedLanguage = it
            viewModel.updateSelectedLanguage(it)
            updateDoneButton()
        }
        updateDoneButton()
        binding.rvLanguages.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLanguages.adapter = adapter
        binding.rvLanguages.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private fun setupListeners() {
        binding.btnDone.setDebounceClickListener {
            if (viewModel.selectedLanguage != null) {
                val code = viewModel.selectedLanguage?.code.orEmpty()
                val name = viewModel.selectedLanguage?.language.orEmpty()
                analyticsHandler.postEvent(
                    ChooseLanguageConstants.AnalyticsKeys.Click_LanguageScreen,
                    mapOf(
                        BaseConstants.FromScreen to ChooseLanguageConstants.AnalyticsKeys.SplashScreen,
                        BaseConstants.LanguagesShown to viewModel.languages.toString(),
                        ChooseLanguageConstants.AnalyticsKeys.languageSelected to name,
                    )
                )
                viewModel.selectedLanguage?.language?.let {
                    analyticsHandler.setUserProperty(
                        listOf(
                            Pair(
                                ChooseLanguageConstants.AnalyticsKeys.Language,
                                it
                            )
                        )
                    )
                }
                val recreate = prefs.getCurrentLanguageCode() != viewModel.selectedLanguage?.code
                if (recreate) {
                    prefs.clearAll()
                    prefs.setShouldShowSplashScreen(false)
                    prefs.setSelectedLanguageCode(code)
                    prefs.setSelectedLanguageName(name)
                    //To set the language code in moko resource
                    StringDesc.localeType = StringDesc.LocaleType.Custom(code)
                    EventBus.getDefault().post(RecreateAppEvent())
                }
                // navigate ahead without changing the language
                onboardingStateMachine.navigateAhead()
            } else {
                getString(R.string.please_select_app_language).snackBar(binding.root)
                onboardingStateMachine.navigateAhead()
            }
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
//                if (!isLanguagePreSelectionUpdated && args.preSelectedLanguage.isNotBlank()) {
//                    viewModel.updateSelectedLanguage(args.preSelectedLanguage)
//                    isLanguagePreSelectionUpdated = true
//                }
                if(!isShownEventFired){
                    analyticsHandler.postEvent(
                        ChooseLanguageConstants.AnalyticsKeys.ShownLanguageScreen,
                        mapOf(
                            BaseConstants.FromScreen to ChooseLanguageConstants.AnalyticsKeys.SplashScreen,
                            BaseConstants.LanguagesShown to it.languages.toString(),
                            BaseConstants.defaultLanguageShown to viewModel.selectedLanguage.toString()
                        )
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
        if(viewModel.selectedLanguage == null){
            binding.btnDone.setDisabled(true)
        }else{
            binding.btnDone.setDisabled(false)
        }
        viewModel.selectedLanguage?.let {
            val currentLocale = prefs.getCurrentLanguageCode()
            val configuration = Configuration(requireContext().resources.configuration)
            configuration.setLocale(Locale(it.code))
            val localeString =
                requireContext().createConfigurationContext(configuration).resources.getString(R.string.apply)
            if (currentLocale.equals(it.code, true)){
                binding.btnDone.setText(getString(R.string.apply))
            }
            else{
                binding.btnDone.setText("${getString(R.string.apply)} / $localeString")
            }
        }
    }

    override fun onDestroyView() {

        adapter = null
        super.onDestroyView()
    }

}
