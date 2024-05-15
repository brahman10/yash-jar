package com.myjar.app.feature_graph_manual_buy

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.myjar.app.feature_graph_manual_buy.data.network.GraphManualBuyDataSource
import com.myjar.app.feature_graph_manual_buy.data.repository.GraphManualBuyRepository
import com.myjar.app.feature_graph_manual_buy.domain.repository.GraphManualBuyRepositoryImpl
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchCalenderUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchGraphDataUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchManualBuyGraphFaqUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchQuickActionUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.impl.FetchCalenderUseCaseImpl
import com.myjar.app.feature_graph_manual_buy.domain.userCases.impl.FetchGraphDataUseCaseImpl
import com.myjar.app.feature_graph_manual_buy.domain.userCases.impl.FetchManualBuyGraphFaqUseCaseImpl
import com.myjar.app.feature_graph_manual_buy.domain.userCases.impl.FetchQuickActionUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GraphManualBuyModule {
    @Provides
    @Singleton
    internal fun provideManualBuyGraphDataSource(@AppHttpClient client: HttpClient): GraphManualBuyDataSource {
        return GraphManualBuyDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideManualBuyGraphRepository(goalBasedSavingDataSource: GraphManualBuyDataSource): GraphManualBuyRepository {
        return GraphManualBuyRepositoryImpl(goalBasedSavingDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchManualBuyGraphFaqUseCase(graphManualBuyRepository: GraphManualBuyRepository): FetchManualBuyGraphFaqUseCase {
        return FetchManualBuyGraphFaqUseCaseImpl(graphManualBuyRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchQuickActionUseCase(graphManualBuyRepository: GraphManualBuyRepository): FetchQuickActionUseCase {
        return FetchQuickActionUseCaseImpl(graphManualBuyRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGraphDataUseCase(graphManualBuyRepository: GraphManualBuyRepository): FetchGraphDataUseCase {
        return FetchGraphDataUseCaseImpl(graphManualBuyRepository)
    }
    @Provides
    @Singleton
    internal fun provideFetchCalenderUseCase(graphManualBuyRepository: GraphManualBuyRepository): FetchCalenderUseCase {
        return FetchCalenderUseCaseImpl(graphManualBuyRepository)
    }
}