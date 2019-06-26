package com.ideal.vox.fragment.home.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ideal.vox.utils.SingleLiveEvent

class UserViewModel : ViewModel() {

    private val status = SingleLiveEvent<UserStatus>()

    fun getStatus(): LiveData<UserStatus> {
        return status
    }

    fun onAboutClick() {
        status.value = UserStatus.ABOUT
    }

    fun onAboutPagerClick() {
        status.value = UserStatus.ABOUT_PAGER
    }

    fun onAlbumsClick() {
        status.value = UserStatus.ALBUMS
    }

    fun onAlbumsPagerClick() {
        status.value = UserStatus.ALBUMS_PAGER
    }
}