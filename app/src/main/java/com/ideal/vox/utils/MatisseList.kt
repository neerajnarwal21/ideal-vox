package com.ideal.vox.utils

import android.net.Uri

object MatisseList {

    private var listener: ((list: List<Uri>?) -> Unit)? = null

    fun setListener(listener: ((list: List<Uri>?) -> Unit)) {
        this.listener = listener
    }

    fun sendList(list: List<Uri>?) {
        listener?.invoke(list)
    }
}
