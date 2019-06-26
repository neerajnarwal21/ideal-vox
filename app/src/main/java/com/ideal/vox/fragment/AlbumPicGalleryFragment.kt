package com.ideal.vox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ideal.vox.R
import com.ideal.vox.adapter.PicPagerAdapter
import com.ideal.vox.data.profile.PicData
import kotlinx.android.synthetic.main.fg_user_detail.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class AlbumPicGalleryFragment : BaseFragment() {

    private var picList = ArrayList<PicData>()
    private var position = 0
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("data")) {
            picList = arguments!!.getParcelableArrayList("data")
            position = arguments!!.getInt("pos")
            userId = arguments!!.getInt("userId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_album_view_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("", showToolbar = false)
        pager.adapter = PicPagerAdapter(baseActivity, userId, picList)
        pager.currentItem = position
    }
}