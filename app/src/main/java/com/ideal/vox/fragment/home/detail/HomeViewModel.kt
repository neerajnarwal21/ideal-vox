package com.ideal.vox.fragment.home.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ideal.vox.utils.SingleLiveEvent

class HomeViewModel : ViewModel() {

    private val status = SingleLiveEvent<HomeStatus>()

    fun getStatus(): LiveData<HomeStatus> {
        return status
    }

    fun onAboutClick() {
        status.value = HomeStatus.ABOUT
    }

    fun onAboutPagerClick() {
        status.value = HomeStatus.ABOUT_PAGER
    }

    fun onAlbumsClick() {
        status.value = HomeStatus.ALBUMS
    }

    fun onAlbumsPagerClick() {
        status.value = HomeStatus.ALBUMS_PAGER
    }
}