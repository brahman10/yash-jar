package com.jar.app.base.di.module

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.feature.home.ui.activity.HomeActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object HomeActivityModule {

    @Provides
    @ActivityScoped
    fun provideNavController(activity: FragmentActivity): NavController {
        return (activity as HomeActivity).navController
    }
}