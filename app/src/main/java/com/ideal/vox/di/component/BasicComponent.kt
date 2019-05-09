package com.ideal.vox.di.component

import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.di.module.ActivityModule
import com.ideal.vox.di.module.ContextModule
import com.ideal.vox.fragment.BaseFragment
import dagger.Component
import javax.inject.Singleton


/**
 * Created by neeraj on 30/04/2018.
 */
@Singleton
@Component(modules = [ContextModule::class, ActivityModule::class])
interface BasicComponent {
    fun inject(activity: BaseActivity)
    fun inj(fragment: BaseFragment)
}
