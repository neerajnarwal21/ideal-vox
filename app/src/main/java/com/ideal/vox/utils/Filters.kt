package com.ideal.vox.utils

import com.ideal.vox.data.FilterData

object Filters {

    private var listener: ((filter: FilterData?) -> Unit)? = null

    fun setListener(listener: ((filter: FilterData?) -> Unit)) {
        this.listener = listener
    }

    fun updateFilters(filter: FilterData?) {
        listener?.invoke(filter)
    }
}
