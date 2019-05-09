package com.ideal.vox.di.module

import android.app.Activity
import android.content.Context
import com.ideal.vox.retrofitManager.ApiClient
import com.ideal.vox.retrofitManager.ApiManager
import com.ideal.vox.retrofitManager.ProgressDialog
import com.ideal.vox.utils.PrefStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by neeraj on 30/04/2018.
 */
@Module(includes = [ContextModule::class])
class ActivityModule(private val activity: Activity) {

    @Singleton
    @Provides
    fun provideActivity() = activity

    @Singleton
    @Provides
    fun provideProgressDialog() = ProgressDialog(activity)

    @Singleton
    @Provides
    fun provideApiManager(context: Context, progressDialog: ProgressDialog) = ApiManager(context, progressDialog)

    @Singleton
    @Provides
    fun providePrefStore(context: Context) = PrefStore(context)

    @Singleton
    @Provides
    fun provideApiClient(context: Context, store: PrefStore) = ApiClient(context, store)
}
