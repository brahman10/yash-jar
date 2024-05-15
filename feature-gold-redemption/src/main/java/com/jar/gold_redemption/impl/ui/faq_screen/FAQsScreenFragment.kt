package com.jar.gold_redemption.impl.ui.faq_screen

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.feature_gold_redemption.R
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_ContactSupportClicked
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class FAQsScreenFragment : BaseComposeFragment() {

    private val viewModel by hiltNavGraphViewModels<FAQsScreenViewModel> (R.id.feature_redemption_navigation)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    @Preview
    override fun RenderScreen() {
        val faqList = viewModel.faqList.observeAsState()
        val faqSelectedIndex = remember { mutableStateOf<Int>(-1) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_292539))
        ) {
            RenderToolBar({
                analyticsHandler.postEvent(
                    Redemption_ContactSupportClicked,
                )
                navigateToContactSupport()
            }) {
                analyticsHandler.postEvent(
                    GoldRedemptionAnalyticsKeys.Redemption_BackClicked,
                    GoldRedemptionAnalyticsKeys.BACK_BUTTON, "FAQ_SCREEN")
                findNavController().navigateUp()
            }
            LazyColumn(
                Modifier
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_292539))
                    .fillMaxSize()
            ) {
                faqList.value?.let {
                    renderExpandableFaqList(
                        this,
                        it,
                        faqSelectedIndex,
                        com.jar.app.core_ui.R.color.color_292539,
                        com.jar.app.core_ui.R.color.color_292539,
                        addSeperator = true,
                        answerTextColor = com.jar.app.core_ui.R.color.color_D5CDF2,
                        questionTextColor = com.jar.app.core_ui.R.color.white,
                    )
                }
            }
        }
    }

    private fun navigateToContactSupport() {
        val number = remoteConfigManager.getWhatsappNumber()
        requireContext().openWhatsapp(
            number,
            "Hey, I'm having trouble in Gold Redemption"
        )
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(this.view)
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showProgressBar() else dismissProgressBar()
        }
    }

    private fun getData() {
        viewModel.fetchFaqs()
    }

    private fun setupUI() {
    }
}