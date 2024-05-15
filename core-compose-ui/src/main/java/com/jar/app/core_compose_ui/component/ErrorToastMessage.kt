package com.jar.app.core_compose_ui.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ErrorToastMessage(errorMessage: String, onErrorShown:()->Unit) {
    val context = LocalContext.current
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    onErrorShown()
}