package com.ideal.vox.fragment.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ideal.vox.utils.SingleLiveEvent

class ProfileViewModel : ViewModel() {

    private val status = SingleLiveEvent<ProfileStatus>()

    fun getStatus(): LiveData<ProfileStatus> {
        return status
    }

    fun onAboutClick() {
        status.value = ProfileStatus.ABOUT
    }

    fun onAboutPagerClick() {
        status.value = ProfileStatus.ABOUT_PAGER
    }

    fun onAlbumsClick() {
        status.value = ProfileStatus.ALBUMS
    }

    fun onAlbumsPagerClick() {
        status.value = ProfileStatus.ALBUMS_PAGER
    }
}