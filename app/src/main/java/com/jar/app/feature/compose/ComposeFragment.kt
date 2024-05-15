package com.jar.app.feature.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jar.app.core_compose_ui.api.CoreComposeUiApi
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTheme
import dagger.hilt.android.AndroidEntryPoint
import com.jar.app.core_compose_ui.views.AlphabeticalScrollerPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ComposeFragment : BaseComposeFragment() {

    @Inject
    lateinit var coreComposeUiApi: CoreComposeUiApi


    fun navigateToShowkaseActivity() {
        coreComposeUiApi.openShowkaseActivity()
    }

    private val viewModel: ComposeViewModel by viewModels()
    override fun setupAppBar() {

    }

    @Composable
    override fun RenderScreen() {
        val message by viewModel.message.observeAsState("")
        ComposeFragmentScreen(message) { navigateToShowkaseActivity() }
    }
    override fun setup(savedInstanceState: Bundle?) {
//        uiScope.launch {
//            viewModel.combinedFlowLoading.collectLatest {
//            }
//            viewModel.uiStateFlow.collectLatest {
//            }
//        }
    }
}

@Composable
private fun ComposeFragmentScreen(message: String, function: () -> Unit) {
    JarTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                message,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            val context = LocalContext.current
            JarPrimaryButton(modifier = Modifier, text = "Buy Gold", onClick = {
                function()
            })
            Spacer(modifier = Modifier.height(20.dp))
            JarSecondaryButton(
                text = "Secondary Button",
                onClick = {
                    Toast.makeText(context, "Secondary Button", Toast.LENGTH_SHORT).show()
                })
            AlphabeticalScrollerPreview()
        }

    }
}

@Preview
@Composable
private fun ComposeFragmentScreenPreview() {
    ComposeFragmentScreen("Hello There!!") {  }
}