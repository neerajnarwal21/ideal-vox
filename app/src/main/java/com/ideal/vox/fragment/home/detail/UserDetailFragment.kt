package com.ideal.vox.fragment.home.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.PageAdapter
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.databinding.FgUserDetailBinding
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fg_user_detail.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserDetailFragment : BaseFragment() {

    private var userData: UserData? = null
    lateinit var model: UserViewModel
    private var callLogCall: Call<JsonObject>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FgUserDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fg_user_detail, container, false)

        model = ViewModelProviders.of(this).get(UserViewModel::class.java)
        binding.model = model
        binding.setLifecycleOwner(this)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = (baseActivity as MainActivity).userData
        setToolbar("" + userData?.name, showToolbar = true)
        val data = (baseActivity as MainActivity).userData
        if (data?.userType == UserType.HELPER) topCL.visibility = View.GONE
        model.getStatus().observe(this, Observer {
            when (it) {
                UserStatus.ABOUT, UserStatus.ABOUT_PAGER -> {
                    aboutTV.doColorChange(false)
                    albumsTV.doColorChange(true)
//                    aboutCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary2))
//                    albumsCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary))
                    aboutIV.isSelected = true
                    albumsIV.isSelected = false
                }
                UserStatus.ALBUMS, UserStatus.ALBUMS_PAGER -> {
                    aboutTV.doColorChange(true)
                    albumsTV.doColorChange(false)
//                    aboutCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary))
//                    albumsCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary2))
                    aboutIV.isSelected = false
                    albumsIV.isSelected = true
                }
            }
            when (it) {
                UserStatus.ABOUT -> pager.currentItem = 0
                UserStatus.ALBUMS -> pager.currentItem = 1
            }
        })

        val adapter = PageAdapter(childFragmentManager)
        val frag = UserAboutFragment()
        adapter.addFragment(frag)

        if (data?.userType == UserType.PHOTOGRAPHER) {
            val frag1 = UserAlbumsFragment()
            adapter.addFragment(frag1)
        }
        pager.adapter = adapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> model.onAboutPagerClick()
                    1 -> model.onAlbumsPagerClick()
                }
            }
        })
        model.onAboutClick()

        updateUI()
    }

    private var tgt: MyTarget? = null

    private fun updateUI() {


        if (userData != null) {
            if (userData!!.photoProfile?.youtube.isNotNullAndEmpty()) {
                lytIV.visibility = View.VISIBLE
                ytIV.visibility = View.VISIBLE
                ytIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userData!!.photoProfile!!.youtube))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            if (userData!!.photoProfile?.insta.isNotNullAndEmpty()) {
                linstaIV.visibility = View.VISIBLE
                instaIV.visibility = View.VISIBLE
                instaIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/${userData!!.photoProfile!!.insta}"))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            if (userData!!.photoProfile?.fb.isNotNullAndEmpty()) {
                lfbIV.visibility = View.VISIBLE
                fbIV.visibility = View.VISIBLE
                fbIV.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userData!!.photoProfile!!.fb))
                    startActivity(Intent.createChooser(browserIntent, "Open with"))
                }
            }
            callIV.setOnClickListener { showCallDialog() }
            mapIV.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${userData!!.photoProfile?.lat},${userData!!.photoProfile?.lng}" +
                        "?q=${userData!!.photoProfile?.lat},${userData!!.photoProfile?.lng}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(baseActivity.packageManager) != null) {
                    startActivity(Intent.createChooser(mapIntent, "Open with"))
                }
            }
            lscheduleIV.visibility = View.VISIBLE
            scheduleIV.visibility = View.VISIBLE
            scheduleIV.setOnClickListener {
                baseActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_home, UserScheduleFragment())
                        .addToBackStack(null)
                        .commit()
            }

            nameTV.text = userData?.name
            if (userData?.avatar.isNotNullAndEmpty()) {
                tgt = MyTarget()
                baseActivity.picasso.load(Const.IMAGE_BASE_URL + userData?.avatar).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera).into(tgt)
                picIV.tag = tgt
            }
        }
    }


    private fun showCallDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Confirm !")
        bldr.setMessage("Make a phone call to ${userData?.name}?")
        bldr.setPositiveButton("Yes") { dialogInterface, i ->
            val userId = RequestBody.create(MediaType.parse("text/plain"), userData!!.id.toString())
            callLogCall = apiInterface.addCalllog(userId)
            apiManager.makeApiCall(callLogCall!!, this)
            dialogInterface.dismiss()
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (callLogCall != null && callLogCall == call) {
            val callInt = Uri.parse("tel:${userData!!.mobileNumber}")
            val callIntent = Intent(Intent.ACTION_DIAL, callInt)
            baseActivity.startActivity(Intent.createChooser(callIntent, "Call with"))
        }
    }

    override fun onPause() {
        super.onPause()
        baseActivity.picasso.cancelRequest(tgt)
    }

    inner class MyTarget : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap != null) {
                if (isVisible) {
                    val bit= getCroppedBitmap(bitmap)
                    picIV.setImageBitmap(bit)
                    picIV.setOnClickListener { showFullScreenImage(baseActivity, bitmap) }
                }
            }
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap):Bitmap {
    val output = Bitmap.createBitmap(bitmap.getWidth(),
            bitmap.getHeight(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

//    val color = 0xff424242
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())

    paint.setAntiAlias(true)
    canvas.drawARGB(0, 0, 0, 0)
//    paint.setColor(color)
    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f,
            bitmap.getWidth() / 2f, paint);
    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
    //return _bmp;
    return output;
}
}
