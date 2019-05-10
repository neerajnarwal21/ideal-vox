package com.ideal.vox.activity.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.activity.loginSignup.LoginSignupActivity
import com.ideal.vox.activity.splash.SplashActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.fragment.AddLocationPinFragment
import com.ideal.vox.fragment.home.BecomePhotographerFragment
import com.ideal.vox.fragment.home.HomeFragment
import com.ideal.vox.fragment.profile.ProfileBasicFragment
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.LocationManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_custom.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {
        setDrawer()
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        navigationView.setCheckedItem(R.id.home)
        jumpToHome()
    }

    fun jumpToHome() {
        drawer.closeDrawer(Gravity.START)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_home, HomeFragment())
                .commit()
    }

    private fun setDrawer() {
        navigationView.menu.clear()
        if (store.getString(Const.SESSION_KEY, null) == null) {
            navigationView.inflateMenu(R.menu.drawer_guest)
        } else {
            val data = store.getUserData(Const.USER_DATA, UserData::class.java)
            if (data?.userType == UserType.USER)
                navigationView.inflateMenu(R.menu.drawer)
            else
                navigationView.inflateMenu(R.menu.drawer_photographer)
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // This method will trigger on item Click of navigation menu
            drawer.closeDrawers()
            var fragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.home -> {
                    fragment = HomeFragment()
                    menuItem.isChecked = true
                }
                R.id.become_photographer -> showPhotographerDialog()
                R.id.logout -> {
                    store.saveString(Const.SESSION_KEY, null)
                    store.saveUserData(Const.USER_DATA, null)
                    val intent = Intent(this, SplashActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
                    startActivity(intent)
                }
            }

            if (fragment != null)
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, fragment)
                        .commit()
            true
        }

        setupHeaderView()
    }

    private fun showPhotographerDialog() {
        val bldr = AlertDialog.Builder(this)
        bldr.setTitle("Become a photographer")
        bldr.setMessage("By continuing, you will be registered as a photographer with us\n\n" +
                "You need to provide some more details about you.\n\n" +
                "Are you good to go ?")
        bldr.setNegativeButton("No", null)
        bldr.setPositiveButton("Yes") { _, _ ->
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fc_home, BecomePhotographerFragment())
                    .commit()
        }
        bldr.create().show()
    }

    fun setupHeaderView() {
        val view = navigationView.getHeaderView(0)
        val nameTV = view.findViewById<MyTextView>(R.id.nameTV)
        val picIV = view.findViewById<ImageView>(R.id.picIV)

        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        nameTV.setText("Hi, ${if (userData != null) userData.name else "SignIn"}")
        if (userData?.avatar != null && userData.avatar.isNotEmpty()) {
            picasso.load(Const.IMAGE_BASE_URL + userData.avatar).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera).transform(CircleTransform()).into(picIV)
        }
        view.setOnClickListener {
            drawer.closeDrawers()
            if (userData == null) {
                val intent = Intent(this, LoginSignupActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            } else {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, ProfileBasicFragment())
                        .commit()
            }
        }
    }

    fun setToolbar(showDrawer: Boolean, text: String) {
        titleTBTV.text = text
        menuTBIV.visibility = if (showDrawer) View.VISIBLE else View.GONE
        menuTBIV.setOnClickListener { drawer.openDrawer(Gravity.START) }
        backTBIV.visibility = if (showDrawer) View.GONE else View.VISIBLE
        backTBIV.setOnClickListener { onBackPressed() }
//        newTBIV.visibility = if (showAddUser) View.VISIBLE else View.GONE
//        closeTBIV.visibility = if (showClose) View.VISIBLE else View.GONE
//        closeTBIV.setImageResource(R.drawable.ic_close_white)
//        navTBIV.setOnClickListener(this)
//        backTBIV.setOnClickListener(this)
//        newTBIV.setOnClickListener(this)
//        closeTBIV.setOnClickListener(this)
    }

    override fun onBackPressed() {
        hideSoftKeyboard()
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START)
        } else {
            val frag = supportFragmentManager.findFragmentById(R.id.fc_home)
            if (frag is HomeFragment) {
                showExit()
            } else if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                jumpToHome()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.fc_home)
        if (fragment is AddLocationPinFragment) {
            when (requestCode) {
                LocationManager.REQUEST_LOCATION ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            fragment.onGPSEnable()
                        }
                        Activity.RESULT_CANCELED -> {
                            fragment.onGPSEnableDenied()
                        }
                        else -> {
                            fragment.onGPSEnableDenied()
                        }
                    }
            }
        }
    }
}