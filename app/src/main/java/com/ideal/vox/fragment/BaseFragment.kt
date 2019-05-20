package com.ideal.vox.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.TextView
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.activity.loginSignup.LoginSignupActivity
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.di.BaseApp
import com.ideal.vox.di.module.ActivityModule
import com.ideal.vox.retrofitManager.ApiClient
import com.ideal.vox.retrofitManager.ApiInterface
import com.ideal.vox.retrofitManager.ApiManager
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.PrefStore
import retrofit2.Call
import javax.inject.Inject


/**
 * Created by Neeraj Narwal on 2/5/17.
 */
open class BaseFragment : Fragment(), AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, ResponseListener {

    lateinit var baseActivity: BaseActivity
    lateinit var apiInterface: ApiInterface
    @Inject
    lateinit var apiClient: ApiClient
    @Inject
    lateinit var apiManager: ApiManager
    @Inject
    lateinit var store: PrefStore

    override fun onAttach(context: Context) {
        (context.applicationContext as BaseApp).getInjection().activityModule(ActivityModule(activity!!)).build().inj(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity
        baseActivity.hideSoftKeyboard()
        apiInterface = apiClient.client.create(ApiInterface::class.java)
    }

    fun setToolbar(title: String, showDrawer: Boolean = false, showEdit: Boolean = false, showToolbar: Boolean = true) {
        (baseActivity as MainActivity).setToolbar(showDrawer, title, showEdit,showToolbar);
    }

    fun setLoginTimeToolbar(title: String) {
        (baseActivity as LoginSignupActivity).setToolbar(title);
    }

    override fun onResume() {
        super.onResume()
        activity!!.invalidateOptionsMenu()
    }

    override fun onClick(v: View) {

    }

    @JvmOverloads
    fun showToast(msg: String, isError: Boolean = false) {
        baseActivity.showToast(msg, isError)
    }

    public fun log(s: String) {
        baseActivity.log(s)
    }

    fun getText(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

    }

    override fun onSuccess(call: Call<*>, payload: Any) {

    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        baseActivity.onError(call, statusCode, errorMessage, responseListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("API >>>>>> cancelling calls")
        apiManager.cancelAnyCall()
    }
}
