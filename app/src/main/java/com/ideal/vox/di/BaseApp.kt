package com.ideal.vox.di

import android.app.Application
import com.ideal.vox.di.component.DaggerBasicComponent
import com.ideal.vox.di.module.ContextModule


/**
 * Created by neeraj on 30/04/2018.
 */

class BaseApp : Application() {

    private lateinit var basicComponent: DaggerBasicComponent.Builder

    override fun onCreate() {
        super.onCreate()
        basicComponent = DaggerBasicComponent.builder()
                .contextModule(ContextModule(applicationContext))
    }

    fun getInjection() = basicComponent
}
