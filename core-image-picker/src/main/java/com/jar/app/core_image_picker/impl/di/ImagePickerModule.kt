package com.jar.app.core_image_picker.impl.di

import com.jar.app.core_image_picker.api.ImagePickerManager
import com.jar.app.core_image_picker.impl.ImagePickerManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ImagePickerModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideImagePickerManager(
        imagePickerManagerImpl: ImagePickerManagerImpl
    ): ImagePickerManager

}