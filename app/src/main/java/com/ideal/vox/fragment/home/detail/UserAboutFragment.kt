package com.ideal.vox.fragment.home.detail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRatingBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.AccessoryAdapter
import com.ideal.vox.adapter.CategoryPriceAdapter
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.data.RatingData
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.data.profile.Accessory
import com.ideal.vox.data.profile.AccessoryData
import com.ideal.vox.data.profile.CategoryPriceData
import com.ideal.vox.data.profile.KeyData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.getAge
import com.ideal.vox.utils.isNotNullAndEmpty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fg_p_about.*
import kotlinx.android.synthetic.main.inc_p_about.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.util.concurrent.TimeUnit


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserAboutFragment : BaseFragment() {

    private var adapter: AccessoryAdapter? = null
    private var listCall: Call<JsonObject>? = null
    private var reviewsCall: Call<JsonObject>? = null
    private var addCall: Call<JsonObject>? = null
    private var catListCall: Call<JsonObject>? = null
    private var viewCall: Call<JsonObject>? = null
    private var userData: UserData? = null
    private var obj: Disposable? = null

    private var doUpdateCall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doUpdateCall = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        emptyTV.text = "No Accessories"
    }

    private fun initUI() {
        userData = (baseActivity as MainActivity).userData
        if (userData != null) {
            checkMyRating()

            viewCall = apiInterface.viewsCount(userData!!.id)
            apiManager.makeApiCall(viewCall!!, this, false)

            catListCall = apiInterface.userCategories(userData!!.id)
            apiManager.makeApiCall(catListCall!!, this, false)

            if (userData!!.photoProfile?.expertise == null || userData!!.photoProfile?.expertise!!.isEmpty()) {
                expTVV.visibility = View.GONE
                expTV.visibility = View.GONE
            }
            expTV.text = userData!!.photoProfile?.expertise
            experTV.text = "${userData!!.photoProfile?.experienceInYear} years, ${userData!!.photoProfile?.experienceInMonths} months"
            ageTV.text = "${getAge(userData!!.photoProfile!!.dob)}, ${userData!!.photoProfile?.gender}"
            mobileTV.text = userData!!.mobileNumber
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
                        reviewTV.text = "${userData!!.reviews} Reviews"
                    }

            ratingRB.setOnClickListener {
                baseActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, UserReviewsFragment())
                        .addToBackStack(null)
                        .commit()
            }
            reviewTV.setOnClickListener { ratingRB.callOnClick() }

            if (userData!!.photoProfile?.youtube.isNotNullAndEmpty()) {
                ytIV.visibility = View.VISIBLE
                ytIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userData!!.photoProfile!!.youtube))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            if (userData!!.photoProfile?.insta.isNotNullAndEmpty()) {
                instaIV.visibility = View.VISIBLE
                instaIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/${userData!!.photoProfile!!.insta}"))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            if (userData!!.photoProfile?.fb.isNotNullAndEmpty()) {
                fbIV.visibility = View.VISIBLE
                fbIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userData!!.photoProfile!!.fb))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            callIV.setOnClickListener {
                val call = Uri.parse("tel:${userData!!.mobileNumber}")
                val callIntent = Intent(Intent.ACTION_DIAL, call)
                baseActivity.startActivity(Intent.createChooser(callIntent, "Call with"))
            }
            mapIV.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${userData!!.photoProfile?.lat},${userData!!.photoProfile?.lng}" +
                        "?q=${userData!!.photoProfile?.lat},${userData!!.photoProfile?.lng}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(baseActivity.packageManager) != null) {
                    startActivity(Intent.createChooser(mapIntent, "Open with"))
                }
            }
            scheduleIV.visibility = View.VISIBLE
            scheduleIV.setOnClickListener {
                baseActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, UserScheduleFragment())
                        .addToBackStack(null)
                        .commit()
            }
            if (userData?.userType == UserType.PHOTOGRAPHER) getList()
            else {
                accTV.visibility = View.GONE
                accListRV.visibility = View.GONE
            }
        }
        editAccessoryIV.visibility = View.INVISIBLE
    }

    private fun checkMyRating() {
        reviewsCall = apiInterface.reviewsList(userData!!.id)
        apiManager.makeApiCall(reviewsCall!!, this, false)
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
            if (isVisible) viewCountTV.text = "$count Profile Views"
            viewLoadingPB.visibility = View.GONE
            if (doUpdateCall) {
                doUpdateCall = false
                val callUpdate = apiInterface.updateViewsCount(userData!!.id)
                apiManager.makeApiCall(callUpdate, this, false)
            }
        } else if (catListCall != null && catListCall === call) {
            val jsonArr = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<CategoryPriceData>>() {}.type
            val datas = Gson().fromJson<ArrayList<CategoryPriceData>>(jsonArr, objectType)
            TransitionManager.beginDelayedTransition(parentCL)
            catListRV.adapter = CategoryPriceAdapter(datas)
        } else if (reviewsCall != null && reviewsCall === call) {
            TransitionManager.beginDelayedTransition(parentCL)
            myRatingRB.visibility = View.VISIBLE
            myRatingTV.visibility = View.VISIBLE
            val jsonObj = payload as JsonObject
            val listArr = jsonObj.get("my_reviews").asJsonArray
            val objectType = object : TypeToken<ArrayList<RatingData>>() {}.type
            val datas = Gson().fromJson<ArrayList<RatingData>>(listArr, objectType)
            if (datas.size > 0) {
                myRatingRB.rating = datas[0].rating
                myRatingTV.text = datas[0].reviews
                myRatingTV.setOnClickListener(null)
            } else {
                myRatingTV.setOnClickListener {
                    showAddDialog()
                }
            }
        } else if (addCall != null && addCall === call) {
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            this.userData = userData
            (baseActivity as MainActivity).userData = userData
            ratingRB.rating = userData!!.rating
            reviewTV.text = "${userData!!.reviews} Reviews"
            myRatingRB.visibility = View.GONE
            myRatingTV.visibility = View.GONE
            checkMyRating()
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }

    private fun showAddDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_review, null)
        bldr.setView(view)
        val ratingRB = view.findViewById<AppCompatRatingBar>(R.id.ratingRB)
        val reviewET = view.findViewById<MyEditText>(R.id.reviewET)
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
}