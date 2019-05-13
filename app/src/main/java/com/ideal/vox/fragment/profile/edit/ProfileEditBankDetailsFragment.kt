package com.ideal.vox.fragment.profile.edit

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.BankData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.*
import kotlinx.android.synthetic.main.fg_p_edit_advance_bank.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditBankDetailsFragment : BaseFragment() {

    private var file: File? = null
    private var updateCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_advance_bank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userid = store.getUserData(Const.USER_DATA, UserData::class.java)?.id
        val bankData = store.getUserData(Const.USER_DATA, UserData::class.java)?.bankAccount
        if (bankData != null) setupUI(bankData,userid)

        passbookIV.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
        saveBT.setOnClickListener { if (validate()) submit() }
    }

    private fun setupUI(bankData: BankData, userid: Int?) {
        baseActivity.picasso.load(Const.IMAGE_BANK_BASE_URL+"/${userid}/"+bankData.passbookPic).into(passbookIV)
        nameET.setText(bankData.accountName)
        accountET.setText(bankData.accountNumber)
        confAccountET.setText(bankData.accountNumber)
        ifscET.setText(bankData.ifscCode)
    }

    private fun selectImage() {
        ImageUtils.Builder(baseActivity, 11) { imagePath, resultCode ->
            val bitmap = imageCompress(imagePath, 1000.0f, 1000.0f)
            file = bitmapToFile(bitmap, baseActivity)
            baseActivity.picasso.load(file).into(passbookIV)
        }.start()
    }

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter name", true)
            getText(accountET).isEmpty() -> showToast("Please enter account number", true)
            getText(confAccountET).isEmpty() -> showToast("Please enter confirm account number", true)
            getText(accountET) != getText(confAccountET) -> showToast("Account no. and Confirm no. doesn't match", true)
            getText(ifscET).isEmpty() -> showToast("Please enter IFSC code", true)
            else -> return true
        }
        return false
    }

    private fun submit() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        val accNo = RequestBody.create(MediaType.parse("text/plain"), getText(accountET))
        val ifsc = RequestBody.create(MediaType.parse("text/plain"), getText(ifscET))

        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file?.length()}")
            fileFirst = MultipartBody.Part.createFormData("passbook_pic", file?.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateCall = apiInterface.updateBankDetails(name, accNo, ifsc, fileFirst)
        apiManager.makeApiCall(updateCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        val jsonObj = payload as JsonObject
        val userObj = jsonObj.getAsJsonObject("user")
        val userData = Gson().fromJson(userObj, UserData::class.java)
        store.saveUserData(Const.USER_DATA, userData)
        showToast("Details updated successfully")
    }
}
