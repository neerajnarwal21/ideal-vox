package com.ideal.vox.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonArray
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_about.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAboutFragment : BaseFragment() {

    private var listCall: Call<JsonArray>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, "Profile")
        initUI()
    }

    private fun initUI() {
        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null) {
            nameET.setText(userData.name)
            mobileET.setText(userData.mobileNumber)
            emailET.setText(userData.email)
        }
        addAccessoryIV.setOnClickListener {
            baseActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fc_home, ProfileAddAccessoryFragment())
                    .addToBackStack(null)
                    .commit()
        }
        getList()
    }

    private fun getList() {
//        listCall = apiInterface.allAccessories(store.getString(Const.SESSION_KEY)!!)
//        apiManager.makeApiCall(listCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
    }
}
