package com.ideal.vox.activity.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.animation.AnimationUtils
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.activity.loginSignup.LoginSignupActivity
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.databinding.ActivitySplashBinding
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.TokenRefresh
import com.ideal.vox.utils.keyHash
import com.ideal.vox.utils.setStatusBarTranslucent
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */


class SplashActivity : BaseActivity() {

    private val handler = Handler()

    private val waitingRunnable = Runnable {
        if (!isFinishing && store.getString(Const.DEVICE_TOKEN) == null) {
            showDialogNoServices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        val model = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        binding.model = model
        model.getStatus().observe(this, Observer {
            when (it) {
                SplashStatus.LOGIN -> gotoLSActivity()
                SplashStatus.SKIP -> {
                    store.setBoolean(Const.GUEST_DIRECT, true)
                    gotoMainActivity()
                }
            }
        })

        keyHash(this)
        log("Token >>>> onCreate checking token :-- ${store.getString(Const.DEVICE_TOKEN)}")
        createNotificationChannel()
        setStatusBarTranslucent(this, true)
        if (initFCM())
            handler.postDelayed({
                log("Token >>>> After 1 sec checking token :-- ${store.getString(Const.DEVICE_TOKEN)}")
                if (!isFinishing && store.getString(Const.DEVICE_TOKEN) != null) {
                    if (store.getString(Const.SESSION_KEY, null) != null)
                        gotoMainActivity()
                    else if (store.getBoolean(Const.GUEST_DIRECT, false))
                        gotoMainActivity()
                    else
                        startAnimations()
                } else {
                    registerForTokenCallback()
                }
            }, 1000)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(0)
    }

    private fun startAnimations() {
        runOnUiThread {
            loginCL.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom)
            val animationResize = AnimationUtils.loadAnimation(this, R.anim.resize_image)
            animation.fillAfter = true
            animationResize.fillAfter = true
            loginCL.startAnimation(animation)
            logoIV.startAnimation(animationResize)
        }
    }

    private fun showDialogNoServices() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert !")
        builder.setMessage("Unable to initialize Google Services." +
                "\nMake sure your Internet connection is good." +
                "\nYou can try restart your phone, check if google apps like maps working or not." +
                "\nIf still problem persists then contact app admin")
        builder.setPositiveButton("Close") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.setOnDismissListener { exitFromApp() }
        builder.show()
    }

    private fun registerForTokenCallback() {
        log("Token >>>> Waiting for token to come")
        handler.postDelayed(waitingRunnable, 10000)

        TokenRefresh.setTokenListener {
            log("Token >>>> Token is here")
            handler.removeCallbacks(waitingRunnable)
            startAnimations()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun gotoLSActivity() {
        val intent = Intent(this, LoginSignupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelId = Const.NOTI_CHANNEL_ID
            val channelName = "Some Channel"
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}