package com.ideal.vox.fragment.profile.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.AccessoryAdapter
import com.ideal.vox.adapter.CategoryPriceAdapter
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.data.profile.Accessory
import com.ideal.vox.data.profile.AccessoryData
import com.ideal.vox.data.profile.CategoryPriceData
import com.ideal.vox.data.profile.KeyData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.getAge
import com.ideal.vox.utils.isNotNullAndEmpty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fg_p_about.*
import kotlinx.android.synthetic.main.inc_p_about.*
import retrofit2.Call
import java.util.concurrent.TimeUnit


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAboutFragment : BaseFragment() {

    private var adapter: AccessoryAdapter? = null
    private var listCall: Call<JsonObject>? = null
    private var catListCall: Call<JsonObject>? = null
    private var viewCall: Call<JsonObject>? = null
    private var userData: UserData? = null
    private var obj: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null) {
            if (userData!!.photoProfile?.expertise == null || userData!!.photoProfile?.expertise!!.isEmpty()) {
                expTVV.visibility = View.GONE
                expTV.visibility = View.GONE
            }
            expTV.text = userData!!.photoProfile?.expertise
            experTV.text = "${userData!!.photoProfile?.experienceInYear} years, ${userData!!.photoProfile?.experienceInMonths} months"
            ageTV.text = "${getAge(userData!!.photoProfile!!.dob)}, ${userData!!.photoProfile?.gender}"
            emailTV.text = userData!!.email
            addressTV.text = "${userData!!.photoProfile?.address}\n${userData!!.photoProfile?.pinCode}"
            if (userData!!.photoProfile?.about.isNotNullAndEmpty()) {
                aboutTVV.visibility = View.VISIBLE
                aboutTV.visibility = View.VISIBLE
                aboutTV.text = userData!!.photoProfile?.about
            }

            obj = Completable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        ratingRB.rating = userData!!.rating
                        reviewTV.text = "(${userData!!.reviews})"
                    }

            ratingRB.setOnClickListener {
                baseActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, ProfileReviewsFragment())
                        .addToBackStack(null)
                        .commit()
            }
            reviewTV.setOnClickListener { ratingRB.callOnClick() }
           if (userData?.userType == UserType.PHOTOGRAPHER) getList()
            else {
                accTV.visibility = View.GONE
                accListRV.visibility = View.GONE
                editAccessoryIV.visibility = View.GONE
            }
            viewCall = apiInterface.viewsCount(userData!!.id)
            apiManager.makeApiCall(viewCall!!, this, false)

            catListCall = apiInterface.userCategories(userData!!.id)
            apiManager.makeApiCall(catListCall!!, this, false)
        }
        editAccessoryIV.setOnClickListener {
            baseActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fc_home, ProfileEditAccessoryFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        obj?.dispose()
    }

    private fun getList() {
        loadingPB.visibility = View.VISIBLE
        listCall = apiInterface.allAccessories(userData!!.id)
        apiManager.makeApiCall(listCall!!, this, false)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            val jsonObj = payload as JsonObject
            val listObj = jsonObj.get("accessories").asJsonObject

            val keysArr = listObj["keys"].asJsonArray
            val objectType = object : TypeToken<ArrayList<KeyData>>() {}.type
            val datas = Gson().fromJson<ArrayList<KeyData>>(keysArr, objectType)
            val list = ArrayList<Accessory>()
            for (data in datas) {
                val arr = listObj[data.key].asJsonArray
                val objType = object : TypeToken<ArrayList<AccessoryData>>() {}.type
                val accDatas = Gson().fromJson<ArrayList<AccessoryData>>(arr, objType)
                if (accDatas.size > 0)
                    list.add(Accessory(data.key, accDatas))
            }
            adapter = AccessoryAdapter(baseActivity, list, userData!!.id, false)
            if (list.size == 0) emptyTV.visibility = View.VISIBLE
            accListRV.adapter = adapter
        } else if (viewCall != null && viewCall === call) {
            val jsonObj = payload as JsonObject
            val count = jsonObj["views"].asString
            viewCountTV.text = "$count Profile Views"
            viewLoadingPB.visibility = View.GONE
        } else if (catListCall != null && catListCall == call) {
            val jsonArr = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<CategoryPriceData>>() {}.type
            val datas = Gson().fromJson<ArrayList<CategoryPriceData>>(jsonArr, objectType)
            catListRV.adapter = CategoryPriceAdapter(datas)
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
