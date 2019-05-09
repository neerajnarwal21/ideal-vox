package com.ideal.vox.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import com.ideal.vox.R


class NetworkUtil(private val context: Context) {
    private val MY_CONNECTIVITY_CHANGE = "My connectivity"
    private val HAS_CONNECTION: String = "Has connection"
    private val bldr = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null

    init {
        bldr.setTitle(context.getString(R.string.netwrk_status))
        bldr.setMessage(context.getString(R.string.not_connected))
        bldr.setPositiveButton(context.getString(R.string.retry), null)
        bldr.setOnDismissListener {
            dialog = null
            updateUI(isNetworkAvailable(context))
        }
        bldr.setCancelable(false)
    }

    fun initialize() {
        val filter = IntentFilter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            debugLog("Net", "using registerNetworkCallback")
            createChangeConnectivityMonitor(context)
            filter.addAction(MY_CONNECTIVITY_CHANGE)
        } else {
            debugLog("Net", "using old broadcast receiver")
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        context.registerReceiver(OnConnectivityReceiver(), filter)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createChangeConnectivityMonitor(context: Context) {
        val intent = Intent(MY_CONNECTIVITY_CHANGE)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        debugLog("Net", "On available network")
                        intent.putExtra(HAS_CONNECTION, true)
                        context.sendBroadcast(intent)
                    }

                    override fun onLost(network: Network) {
                        debugLog("Net", "On not available network")
                        intent.putExtra(HAS_CONNECTION, false)
                        context.sendBroadcast(intent)
                    }
                })
    }

    inner class OnConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateUI(intent.getBooleanExtra(HAS_CONNECTION, true))
            } else {
                updateUI(isNetworkAvailable(context))
            }
        }
    }

    private fun updateUI(isAvailable: Boolean) {
        if (isAvailable) {
            debugLog("Net", "triggering on connectivity gain")
            if (dialog != null && dialog!!.isShowing) {
                dialog?.dismiss()
            }
        } else {
            debugLog("Net", "triggering on connectivity lost")
            dialog = bldr.create()
            if (!dialog!!.isShowing) {
                dialog?.show()
            }
        }
    }
}