package com.jar.app.core_web_pdf_viewer.impl.di

import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.core_web_pdf_viewer.impl.data.WebPdfViewerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class WebPdfViewerModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideWebPdfViewerApi(webPdfViewerImpl: WebPdfViewerImpl): WebPdfViewerApi

}