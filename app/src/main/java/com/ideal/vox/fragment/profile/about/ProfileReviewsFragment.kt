package com.ideal.vox.fragment.profile.about

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.ReviewsAdapter
import com.ideal.vox.data.RatingData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_edit_accessories.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileReviewsFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var adapter: ReviewsAdapter? = null
    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_accessories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar("Reviews")
        super.onViewCreated(view, savedInstanceState)
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        reviewCL.visibility = View.GONE
        listRV.layoutManager = LinearLayoutManager(baseActivity)
    }

    override fun onResume() {
        super.onResume()
        getList()
    }

    private fun getList() {
        if (userData != null) {
            loadingPB.visibility = View.VISIBLE
            listCall = apiInterface.reviewsList(userData!!.id)
            apiManager.makeApiCall(listCall!!, this, false)
        }
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            setupUI(payload)
        }
    }

    private fun setupUI(payload: Any) {
        val jsonObj = payload as JsonObject
        val listArrOther = jsonObj.get("other_reviews").asJsonArray
        val objectType = object : TypeToken<ArrayList<RatingData>>() {}.type
        val datasOther = Gson().fromJson<ArrayList<RatingData>>(listArrOther, objectType)
        emptyTV.visibility = if (datasOther.size == 0) View.VISIBLE else View.GONE
        emptyTV.text = "No Reviews now"
        adapter = ReviewsAdapter(baseActivity, datasOther)
        listRV.adapter = adapter

    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
