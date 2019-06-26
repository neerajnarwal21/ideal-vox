/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ideal.vox.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ideal.vox.R
import com.ideal.vox.activity.splash.SplashActivity


class MyFcmListenerService : FirebaseMessagingService() {

    private val TAG = "MyFcmListenerService"

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        debugLog(TAG, "Token here")
        val store = PrefStore(this)
        store.saveString(Const.DEVICE_TOKEN, s)
        TokenRefresh.tokenRefreshComplete()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage!!.data
        debugLog(TAG, "bundle Data : $data")
        val from = remoteMessage.from
        debugLog(TAG, "From: " + from!!)
        //        Bundle bundle = new Bundle();
        //        bundle.putString("action", data.get("action").toString());
        //        bundle.putString("controller", data.get("controller").toString());
        //        bundle.putString("message", data.get("message").toString());
        val builder: Notification.Builder
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = Notification.Builder(this, Const.NOTI_CHANNEL_ID)
        } else {
            builder = Notification.Builder(this)
        }
        builder.setContentTitle(getString(R.string.app_name))
        builder.setContentText(data["message"].toString())
        builder.style = Notification.BigTextStyle().bigText(data["message"].toString())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("isPush", true)
        intent.putExtra("orderId", data["orderId"].toString())
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, builder.build())
    }
}