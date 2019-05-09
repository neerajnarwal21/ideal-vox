package com.ideal.vox.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.ideal.vox.BuildConfig
import com.ideal.vox.R
import com.ideal.vox.customViews.MyTextView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("doEnable")
fun MyTextView.doColorChange(doEnable: Boolean) {
    this.setTextColor(ContextCompat.getColor(this.context, if (doEnable) R.color.WhiteSmoke else R.color.WhiteSmokeDark))
}

@BindingAdapter("selected")
fun ImageView.selection(enable: Boolean) {
    this.isSelected = !enable
}

fun getNonEmptyText(text: String?): String {
    return if (text == null || text.isEmpty()) "Not Provided" else text
}

fun Spinner.mySpinnerCallback(callBack: (Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callBack.invoke(position)
        }
    }
}

fun changeDateFormat(dateString: String?, sourceDateFormat: String, targetDateFormat: String): String {
    if (dateString == null || dateString.isEmpty()) {
        return ""
    }
    val date: Date
    try {
        date = SimpleDateFormat(sourceDateFormat, Locale.getDefault()).parse(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        return dateString
    }

    return SimpleDateFormat(targetDateFormat, Locale.getDefault()).format(date)
}

fun changeDateFormatFromDate(sourceDate: Date?, targetDateFormat: String?): String {
    return if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
        ""
    } else SimpleDateFormat(targetDateFormat, Locale.getDefault()).format(sourceDate)
}

fun getDateFromStringDate(dateString: String?, sourceDateFormat: String): Date? {
    if (dateString == null || dateString.isEmpty()) {
        return null
    }
    var date = Date()
    try {
        date = SimpleDateFormat(sourceDateFormat, Locale.ENGLISH).parse(dateString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return date
}

fun TextView.getTrimmedText(): String {
    return this.text.toString().trim { it <= ' ' }
}

fun getUniqueDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun isValidMail(email: String): Boolean {
    return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())
}

fun isValidPassword(password: String): Boolean {
    return password.matches("^(?=\\S+$).{8,}$".toRegex())
}

fun changeStatusBarColor(activity: Activity, colorId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.window.apply {
            this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            this.statusBarColor = ContextCompat.getColor(activity, colorId)
        }
    }
}

fun setStatusBarTranslucent(activity: Activity, makeTranslucent: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        activity.window.also {
            if (makeTranslucent) it.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            else it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}

fun getAddress(lat: Double, lng: Double, context: Context): String {
    try {
        val geocoder = Geocoder(context)
        val addresses: List<Address>
        return if (lat != 0.0 || lng != 0.0) {
            addresses = geocoder.getFromLocation(lat, lng, 1)
            val address = addresses[0].getAddressLine(0)
            val address1 = addresses[0].getAddressLine(1)
            val country = addresses[0].countryName
            address + ", " + address1 + ", " + (country ?: "")
        } else {
            "Unable to get address of this location"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return "Unable to get address of this location"
    }

}

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.getActiveNetworkInfo()
    return networkInfo != null && networkInfo.isConnected()
}

fun debugLog(tag: String, s: String) {
    if (BuildConfig.DEBUG)
        Log.e(tag, s)
}

fun keyHash(context: Context) {
    try {
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.e("KeyHash:>>>>>>>>>>>>>>>", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

}

@JvmOverloads
fun View.rotate(backRotate: Boolean = false) {
    val anim = RotateAnimation(if (backRotate) 0.0f else 180.0f,
            if (backRotate) 180.0f else 0.0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f)
    anim.fillAfter = true
    anim.duration = 500
    this.startAnimation(anim)
}

fun <T : Parcelable> copy(orig: T): T? {
    val p = Parcel.obtain()
    orig.writeToParcel(p, 0)
    p.setDataPosition(0)
    var copy: T? = null
    try {
        copy = orig.javaClass.getDeclaredConstructor(Parcel::class.java).newInstance(p) as T
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return copy
}

fun showFullScreenImage(activity: Activity, bitmap: Bitmap) {
    val view = View.inflate(activity, R.layout.view_full_screen_image, null)
    val builder = AlertDialog.Builder(activity)
    builder.setView(view)

    val closeIV = view.findViewById<ImageView>(R.id.closeIV)
    view.findViewById<TouchImageView>(R.id.imageTIV).setImageBitmap(bitmap)
    val dialogg = builder.create()
    closeIV.tag = dialogg
    closeIV.setOnClickListener { (it.tag as AlertDialog).dismiss() }
    dialogg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    if (!dialogg.isShowing) dialogg.show()
    dialogg.window?.setLayout(getDisplaySize(activity), getDisplaySize(activity))
}

fun getDisplaySize(activity: Activity): Int {
    val metrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(metrics)
    val height = metrics.heightPixels
    val width = metrics.widthPixels
    return if (height < width) height else width
}
