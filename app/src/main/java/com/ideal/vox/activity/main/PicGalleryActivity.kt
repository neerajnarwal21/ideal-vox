package com.ideal.vox.activity.main

import android.os.Bundle
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.adapter.PicPagerAdapter
import com.ideal.vox.data.profile.PicData
import kotlinx.android.synthetic.main.fg_user_detail.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class PicGalleryActivity : BaseActivity() {

    private var picList = ArrayList<PicData>()
    private var position = 0
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getIntExtra("userId", 0) != 0) {
            picList = intent.getParcelableArrayListExtra("data")
            position = intent.getIntExtra("pos", 0)
            userId = intent.getIntExtra("userId", 0)
        }
        setContentView(R.layout.fg_album_view_gallery)
        pager.adapter = PicPagerAdapter(this, userId, picList)
        pager.currentItem = position
    }
}