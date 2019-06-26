package com.ideal.vox.fragment.home.detail

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.AlbumsAdapter
import com.ideal.vox.data.profile.AlbumData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import kotlinx.android.synthetic.main.fg_p_albums.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserAlbumsFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var adapter: AlbumsAdapter? = null
    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = (baseActivity as MainActivity).userData

        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        if (userData != null) {
            loadingPB.visibility = View.VISIBLE
            listCall = apiInterface.allAlbums(userData!!.id)
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
        val listArr = jsonObj.get("albums").asJsonArray
        val objectType = object : TypeToken<ArrayList<AlbumData>>() {}.type
        val datas = Gson().fromJson<ArrayList<AlbumData>>(listArr, objectType)
//        if (datas.size < MAX_ITEMS) {
//            datas.add(AlbumData(id = -1))
//        }
        emptyTV.visibility = if (datas.size == 0) View.VISIBLE else View.GONE
        adapter = AlbumsAdapter(baseActivity, datas, userData!!.id, null)
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}