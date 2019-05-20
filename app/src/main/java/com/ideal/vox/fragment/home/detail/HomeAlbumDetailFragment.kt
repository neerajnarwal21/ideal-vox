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
import com.ideal.vox.adapter.PicAdapter
import com.ideal.vox.data.AlbumData
import com.ideal.vox.data.PicData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import kotlinx.android.synthetic.main.fg_p_albums.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class HomeAlbumDetailFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var adapter: PicAdapter? = null
    private var albumData: AlbumData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("data"))
            albumData = arguments!!.getParcelable("data")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(albumData!!.name)
        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        if (albumData != null) {
            loadingPB.visibility = View.VISIBLE
            listCall = apiInterface.allAlbumPics(albumData!!.id)
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
        val listArr = jsonObj.get("pictures").asJsonArray
        val objectType = object : TypeToken<ArrayList<PicData>>() {}.type
        val datas = Gson().fromJson<ArrayList<PicData>>(listArr, objectType)
        emptyTV.visibility = if (datas.size == 0) View.VISIBLE else View.GONE
        emptyTV.text = "No Pictures added yet"
//        if (datas.size < MAX_ITEMS) {
//            datas.add(PicData(id = -1))
//        }
        adapter = PicAdapter(baseActivity, datas, (baseActivity as MainActivity).userData!!.id, null)
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}