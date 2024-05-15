package com.jar.app.feature_transaction.impl.di

import com.jar.app.feature_transaction.api.TransactionApi
import com.jar.app.feature_transaction.impl.data.TransactionApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class TransactionApiModule {

    @Binds
    @ActivityScoped
    abstract fun provideTransactionApi(transactionApiImpl: TransactionApiImpl): TransactionApi

}