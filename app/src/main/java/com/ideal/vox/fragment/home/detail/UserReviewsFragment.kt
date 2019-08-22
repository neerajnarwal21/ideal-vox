package com.ideal.vox.fragment.home.detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.loginSignup.LoginSignupActivity
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.ReviewsAdapter
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.data.RatingData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.adapter_reviews.*
import kotlinx.android.synthetic.main.fg_p_edit_accessories.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserReviewsFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var addCall: Call<JsonObject>? = null
    private var adapter: ReviewsAdapter? = null
    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_accessories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar("Reviews")
        super.onViewCreated(view, savedInstanceState)
        userData = (baseActivity as MainActivity).userData

        listRV.layoutManager = LinearLayoutManager(baseActivity)
        addIV.setOnClickListener {
            val data = store.getUserData(Const.USER_DATA, UserData::class.java)
            if (data == null) {
                val bldr = AlertDialog.Builder(baseActivity)
                bldr.setTitle("Login Required")
                bldr.setMessage("To post a review, you need to login or register\nProceed to Login screen?")
                bldr.setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(baseActivity, LoginSignupActivity::class.java)
                    intent.putExtra("forLogin", true)
                    baseActivity.startActivity(intent)
                    baseActivity.finish()
                }
                bldr.setNegativeButton("No", null)
                bldr.create().show()
            } else {
                showAddDialog(null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getList()
    }

    private fun getList() {
        if (userData != null) {
            loadingPB.visibility = View.VISIBLE
            reviewCL.visibility = View.INVISIBLE
            listCall = apiInterface.reviewsList(userData!!.id)
            apiManager.makeApiCall(listCall!!, this, false)
        }
    }

    private fun showAddDialog(ratingData: RatingData?) {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_review, null)
        bldr.setView(view)
        val ratingRB = view.findViewById<AppCompatRatingBar>(R.id.ratingRB)
        val reviewET = view.findViewById<MyEditText>(R.id.reviewET)
        if (ratingData != null) {
            ratingRB.rating = ratingData.rating
            reviewET.setText(ratingData.reviews)
        }
        bldr.setPositiveButton("Add") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (ratingRB.rating == 0.0f) {
                showToast("Please add rating", true)
            } else {
                dialog.dismiss()
                addRating(ratingRB.rating, getText(reviewET))
                baseActivity.hideSoftKeyboard()
            }
        }
    }

    private fun addRating(ratingg: Float, revieww: String) {
        val userId = RequestBody.create(MediaType.parse("text/plain"), userData!!.id.toString())
        val rating = RequestBody.create(MediaType.parse("text/plain"), ratingg.toString())
        val review = RequestBody.create(MediaType.parse("text/plain"), revieww)
        addCall = apiInterface.addReview(userId, rating, review)
        apiManager.makeApiCall(addCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            setupUI(payload)
        } else if (addCall != null && addCall === call) {
            apiClient.clearCache()
            getList()
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            (baseActivity as MainActivity).userData = userData
        }
    }

    private fun setupUI(payload: Any) {
        val jsonObj = payload as JsonObject
        val listArr = jsonObj.get("my_reviews").asJsonArray
        val listArrOther = jsonObj.get("other_reviews").asJsonArray
        val objectType = object : TypeToken<ArrayList<RatingData>>() {}.type
        val datas = Gson().fromJson<ArrayList<RatingData>>(listArr, objectType)
        if (datas.size == 0) {
            addIV.visibility = View.VISIBLE
            reviewCL.visibility = View.INVISIBLE
        } else {
            addIV.visibility = View.INVISIBLE
            reviewCL.visibility = View.VISIBLE
            baseActivity.picasso.load(Const.IMAGE_BASE_URL + "/${datas[0].reviewUser?.avatar}")
                    .transform(CircleTransform()).resize(100,100).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera)
                    .into(picIV)

            nameTV.text = "Name: ${datas[0].reviewUser?.name}"
            ratingRB.rating = datas[0].rating
            reviewTV.text = datas[0].reviews
            editReviewIV.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    showAddDialog(datas[0])
                }
            }
        }
        val datasOther = Gson().fromJson<ArrayList<RatingData>>(listArrOther, objectType)
        emptyTV.visibility = if (datas.size == 0 && datasOther.size == 0) View.VISIBLE else View.GONE
        emptyTV.text = "No Reviews now"
        adapter = ReviewsAdapter(baseActivity, datasOther)
        listRV.adapter = adapter

    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
