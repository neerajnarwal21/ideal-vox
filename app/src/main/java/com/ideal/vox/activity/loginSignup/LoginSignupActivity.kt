package com.ideal.vox.activity.loginSignup

import android.os.Bundle
import android.view.View
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.fragment.loginSignup.login.LoginFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.toolbar_custom.*


class LoginSignupActivity : BaseActivity() {

    var forLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forLogin = intent.getBooleanExtra("forLogin", false)
        setContentView(R.layout.activity_ls)
        initUI()
    }

    private fun initUI() {
        store.saveString(Const.SESSION_KEY, null)
        store.saveUserData(Const.USER_DATA, null)
        jumpToLogin()
    }

    private fun jumpToLogin() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_ls, LoginFragment())
                .commit()
    }

    fun setToolbar(text: String) {
        titleTBTV.text = text
        menuTBIV.visibility = View.GONE
        backTBIV.visibility = View.VISIBLE
        backTBIV.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        hideSoftKeyboard()
        val frag = supportFragmentManager.findFragmentById(R.id.fc_ls)
        if (frag is LoginFragment) {
            finish()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }
}