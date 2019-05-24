package com.ideal.vox.fragment.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.HomeAdapter
import com.ideal.vox.data.FilterData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Filters
import kotlinx.android.synthetic.main.fg_home.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */

class HomeFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var datas = ArrayList<UserData>()
    private var adapter: HomeAdapter? = null
    private lateinit var linearManager: LinearLayoutManager
    var loading = false
    var pageNo = 1
    private var totalPages = 1
    private var filterData: FilterData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Home", true, showMap = true)
        searchET.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery()
            }
            i == EditorInfo.IME_ACTION_SEARCH
        }
        searchIV.setOnClickListener { searchQuery() }
        swipeRL.setOnRefreshListener {
            searchET.setText("")
            apiClient.clearCache()
            initUI()
        }
        initUI()
    }

    private fun searchQuery() {
        loadingPB.visibility = View.VISIBLE
        initUI()
    }

    private fun initUI() {
        baseActivity.hideSoftKeyboard()
        searchET.clearFocus()
        filterIV.setOnClickListener {
            Filters.setListener {
                filterData = it
                loadingPB.visibility = View.VISIBLE
                initUI()
            }
            val bndl = Bundle()
            bndl.putParcelable("data", filterData)
            val frag = HomeFilterFragment()
            frag.arguments = bndl
            baseActivity.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fc_home, frag)
                    .addToBackStack(null)
                    .commit()
        }
        datas.clear()
        pageNo = 1
        totalPages = 1
        linearManager = LinearLayoutManager(context)
        listRV.layoutManager = linearManager
        listRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItems = linearManager.itemCount
                val lastItem = linearManager.findLastVisibleItemPosition()
                if (totalItems <= lastItem + 1 && !loading && getNextList()) {
                    loading = true
                    adapter?.updateLoadMoreView(loading)
                }
            }
        })
        setAdapter()
        getNextList()
    }

    private fun setAdapter() {
        adapter = HomeAdapter(baseActivity, datas)
        listRV.adapter = adapter
    }

    private fun getNextList(): Boolean {
        if (!baseActivity.isFinishing) {
            return if (pageNo <= totalPages) {
                val category = if (filterData == null) null else if (filterData?.category == "All") null else filterData?.category
                val minPrice = if (filterData == null) null else if (!filterData?.priceDisable!!) null else filterData?.min
                val maxPrice = if (filterData == null) null else if (!filterData?.priceDisable!!) null else filterData?.max
                listCall = apiInterface.allPhotographers(pageNo, getText(searchET), category, minPrice, maxPrice)
                apiManager.makeApiCall(listCall!!, this, false)
                true
            } else false
        }
        return false
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        swipeRL.isRefreshing = false
        if (listCall != null && listCall === call) {
            loading = false
            adapter?.updateLoadMoreView(loading)

            val jsonObject = payload as JsonObject
            val parentData = jsonObject.getAsJsonObject("photographers")
            pageNo = parentData.get("current_page").asInt
            totalPages = parentData.get("last_page").asInt
            val actualData = parentData.getAsJsonArray("data")
            val objectType = object : TypeToken<ArrayList<UserData>>() {}.type
            val list = Gson().fromJson<ArrayList<UserData>>(actualData, objectType)
            datas.addAll(list)
            if (pageNo == 1)
                setAdapter()
            else {
                adapter?.notifyItemInserted(datas.size)
            }
            emptyTV.visibility = if (adapter?.itemCount == 0) View.VISIBLE else View.GONE
            pageNo++
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
        loading = false
        adapter?.updateLoadMoreView(loading)
    }

    fun resetEdittext() {
        searchET.setText("")
    }
}
