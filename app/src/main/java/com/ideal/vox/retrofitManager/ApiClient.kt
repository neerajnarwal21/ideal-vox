package com.ideal.vox.retrofitManager

import android.content.Context
import android.net.ConnectivityManager
import com.ideal.vox.BuildConfig
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.PrefStore
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ApiClient(private val context: Context, private val store: PrefStore) {
    private var retrofit: Retrofit? = null

    /* Creates client for retrofit, here you can configure different settings of retrofit manager
          like Logging, Cache size, Decoding factories, Convertor factories etc.
         *///                .addNetworkInterceptor(provideCacheInterceptor())
    val client: Retrofit
        get() {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor { chain ->
                        val newRequestBuilder = chain.request().newBuilder()
                                .addHeader("accept", "application/json")
                        if (store.getString(Const.SESSION_KEY) != null) {
                            newRequestBuilder.addHeader("authorization", "Bearer ${store.getString(Const.SESSION_KEY)}")
                        }
                        chain.proceed(newRequestBuilder.build())
                    }
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .cache(provideCache())
                    .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(Const.SERVER_REMOTE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(client)
                        .build()
            }
            return retrofit!!
        }

    var cache: Cache? = null

    private fun provideCache(): Cache? {
        try {
            if (cache == null)
                cache = Cache(File(context.cacheDir, "http-cache"),
                        (10 * 1024 * 1024).toLong()) // 10 MB
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cache
    }

    fun clearCache() {
        cache?.evictAll()
    }

    private fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val cacheBuilder = CacheControl.Builder()
            cacheBuilder.maxAge(0, TimeUnit.SECONDS)
            cacheBuilder.maxStale(120, TimeUnit.DAYS)
            val cacheControl = cacheBuilder.build()

            var request = chain.request()
            if (checkIfHasNetwork()) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build()
            }
            val originalResponse = chain.proceed(request)
            if (checkIfHasNetwork()) {
                val maxAge = 60 * 2 // read from cache upto 2 min
                originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
            } else {
                val maxStale = 60 * 60 * 24// tolerate 1 day stale
                originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
            }
        }
    }

    private fun provideOfflineCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if (!checkIfHasNetwork()) {
                val cacheControl = CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build()
            }

            chain.proceed(request)
        }
    }

    private fun checkIfHasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    companion object {
        private val HEADER_CACHE_CONTROL = "Cache-Control"
        private val HEADER_PRAGMA = "Pragma"
    }
}
