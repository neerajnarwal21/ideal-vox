package com.ideal.vox.fragment.profile.edit

import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ideal.vox.R
import com.ideal.vox.adapter.PageAdapter
import com.ideal.vox.fragment.BaseFragment
import kotlinx.android.synthetic.main.fg_p_edit_home.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditAdvFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_edit_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Edit Profile")

        val adapter = PageAdapter(childFragmentManager)
        val frag = ProfileEditBasicFragment()
        val frag1 = ProfileEditPhotographerFragment()
        val frag2 = ProfileEditPriceDetailsFragment()
        val frag3 = ProfileEditBankDetailsFragment()
        adapter.addFragment(frag, "Basic Details")
        adapter.addFragment(frag1, "Professional")
        adapter.addFragment(frag2, "Price")
        adapter.addFragment(frag3, "Bank Details")
        pager.adapter = adapter
        pager.currentItem = 1

        pager.offscreenPageLimit = 2
        tabs.setupWithViewPager(pager)
        tabs.setFont()
    }

    private fun TabLayout.setFont() {
        val vg = this.getChildAt(0) as ViewGroup
        for (i: Int in 0..vg.childCount) {
            val vgTab = vg.getChildAt(i) as ViewGroup?
            vgTab?.let {
                for (j: Int in 0..vgTab.childCount) {
                    val tab = vgTab.getChildAt(j)
                    if (tab is TextView) {
                        tab.typeface = Typeface.createFromAsset(baseActivity.assets, "fonts/myriad.ttf")
                    }
                }
            }
        }
    }
}
