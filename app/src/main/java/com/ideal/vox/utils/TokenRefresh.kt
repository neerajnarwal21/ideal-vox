package com.ideal.vox.utils

object TokenRefresh {
    private var tokenListener: (() -> Unit)? = null

    fun setTokenListener(tokenListener: (() -> Unit)?) {
        this.tokenListener = tokenListener
    }

    fun tokenRefreshComplete() {
        tokenListener?.invoke()
    }
}