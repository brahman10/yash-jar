package com.myjar.app.feature_graph_manual_buy.impl.di

import com.myjar.app.feature_graph_manual_buy.api.GraphManualBuyApi
import com.myjar.app.feature_graph_manual_buy.impl.GraphManualBuyImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ManualBuyGraphUiModule {

    @Binds
    @ActivityScoped
    abstract fun provideGraphManualBuyApi(graphManualBuyImpl: GraphManualBuyImpl): GraphManualBuyApi
}