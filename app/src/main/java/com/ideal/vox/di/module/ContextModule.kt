package com.ideal.vox.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by neeraj on 30/04/2018.
 */
@Module
class ContextModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext() = context

//    @Singleton
//    @Provides
//    fun providePrefStore() = PrefStore(context)
//
//    @Singleton
//    @Provides
//    fun provideApiClient(store: PrefStore) = ApiClient(context, store)

    //    @Singleton
    //    @Provides
    //    public SharedPreferences provideSharedPreferences(Context context) {
    //        return PreferenceManager.getDefaultSharedPreferences(context);
    //    }
    //
    //    @Singleton
    //    @Provides
    //    public Gson provideGson() {
    //        return new Gson();
    //    }
}
