package com.ideal.vox.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import java.util.*

class PrefStore(private val mAct: Context) {

    private val pref: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(mAct)

    fun cleanPref() = pref.edit().clear().apply()

    fun containValue(key: String) = pref.contains(key)

    fun setBoolean(key: String, value: Boolean) = pref.edit().putBoolean(key, value).apply()

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false) = pref.getBoolean(key, defaultValue)

    fun saveString(key: String, value: String?) = pref.edit().putString(key, value).apply()

    @JvmOverloads
    fun getString(key: String, defaultVal: String? = null): String? = pref.getString(key, defaultVal)

    fun saveLong(key: String, value: Long) = pref.edit().putLong(key, value).apply()

    @JvmOverloads
    fun getLong(key: String, defaultVal: Long = 0) = pref.getLong(key, defaultVal)

    fun setInt(id: String, value: Int) = pref.edit().putInt(id, value).apply()

    @JvmOverloads
    fun getDouble(key: String, defaultVal: Double = 0.0) = java.lang.Double.longBitsToDouble(pref.getLong(key, java.lang.Double.doubleToRawLongBits(defaultVal)))

    fun setDouble(id: String, value: Double) = pref.edit().putLong(id, java.lang.Double.doubleToRawLongBits(value)).apply()

    @JvmOverloads
    fun getInt(key: String, defaultVal: Int = 0) = pref.getInt(key, defaultVal)

    fun <T> setData(value: String, datas: ArrayList<T>) {
        pref.edit().putString(value, ObjectSerializer.serialize(datas)).apply()
    }

    fun <T> getData(name: String): ArrayList<T> {
        return ObjectSerializer.deserialize(pref.getString(name, ObjectSerializer.serialize(ArrayList<T>()))) as ArrayList<T>
    }

    fun saveUserData(key: String, data: Any?) = pref.edit().putString(key, Gson().toJson(data)).apply()

    fun <T> getUserData(key: String, dataClass: Class<T>): T? = Gson().fromJson(pref.getString(key, null), dataClass)
}