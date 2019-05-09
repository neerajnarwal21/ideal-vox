package com.ideal.vox.fragment.profile

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ideal.vox.R
import com.ideal.vox.adapter.AlbumsAdapter
import com.ideal.vox.fragment.BaseFragment
import kotlinx.android.synthetic.main.fg_p_albums.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAlbumsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setToolbar(true, "Profile")
        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        val list = ArrayList<String>()
        for (i in 0..15) {
            list.add(i.toString())
        }
        listRV.adapter = AlbumsAdapter(baseActivity, list)
    }
}
