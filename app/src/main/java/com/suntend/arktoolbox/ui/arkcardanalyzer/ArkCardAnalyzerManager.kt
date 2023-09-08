package com.suntend.arktoolbox.ui.arkcardanalyzer

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DeprecatedSinceApi
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suntend.arktoolbox.APP
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil
import com.suntend.arktoolbox.ui.arklabel.ArkLabelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

object ArkCardAnalyzerManager {
    //原始数据
    private var originData =
        ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>()
    private var page = 1
    private var isQuerying = false
    private var callback: OnArkCardAnalyzerCallback? = null
    private val Preferences_Name = "ArkCardAnalyzerData"
    private const val Key_data_append = "ArkCardAnalyzerData"
    private val preferences: SharedPreferences =
        APP.appContext.getSharedPreferences(Preferences_Name, Context.MODE_PRIVATE)

    fun startQueryOnlineData(uid: String, token: String, callback: OnArkCardAnalyzerCallback) {
        if (isQuerying) {
            return
        }
        originData.clear()
        page = 1
        this.callback = callback
        isQuerying = true
        queryLocalData(uid)
        queryOnlineData(uid, token)
    }

    private fun queryOnlineData(uid: String, token: String) {
        MainScope().launch(Dispatchers.IO) {
            val url =
                "https://ak.hypergryph.com/user/api/inquiry/gacha?channelId=1" + "&token=" + token + "&page=" + page
            try {
                val request: Request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                val responseBody = response.body!!.string()
                if (response.isSuccessful) {
                    val result = Gson().fromJson(responseBody, ArkGachaResultEntity::class.java)
                    if (result.code != 0) {
                        throw Exception(result.msg)
                    }
                    result.data?.list?.forEach { data ->
                        if (originData.size == 0) {
                            addOnlineToOrigin(0, data)
                        } else {
                            for ((index, entry) in originData.withIndex()) {

                                if (index == originData.size - 1) {
                                    addOnlineToOrigin(-1, data)
                                    break
                                } else if (data.ts > entry.ts) {
                                    //数据更新,直接添加
                                    addOnlineToOrigin(0, data)
                                    break
                                } else if (data.ts == entry.ts && data.pool == entry.pool) {
                                    //匹配到相同项,认为重复了,中断所有后续请求
                                    isQuerying = false
                                    callback?.onResult(originData)
                                    saveData(uid)
                                    return@launch
                                }
                            }
                        }
//                        data.chars?.forEach {
//                            it.ts = data.ts
//                            it.pool = data.pool
//                            if (originData.size == 0) {
//                                originData.add(it)
//                            } else {
//                                for ((index, entry) in originData.withIndex()) {
//
//                                    if (index == originData.size - 1) {
//                                        originData.add(it)
//                                        break
//                                    } else if (it.ts > entry.ts) {
//                                        //数据更新,直接添加
//                                        originData.add(index, it)
//                                        break
//                                    } else if (it.ts == entry.ts && it.pool == entry.pool
//                                        && it.name == entry.name && it.isNew == entry.isNew
//                                    ) {
//                                        //匹配到相同项,认为重复了,中断所有后续请求
//                                        isQuerying = false
//                                        callback?.onResult(originData)
//                                        saveData(uid)
//                                        return@launch
//                                    }
//                                }
//                            }
//                        }
                    }
                    if (result.data?.pagination?.hasNext == true) {
                        page++
                        queryOnlineData(uid, token)
                    } else {
                        isQuerying = false
                        callback?.onResult(originData)
                        saveData(uid)
                    }

                } else {
                    throw Exception("not response.isSuccessful")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isQuerying = false
                launch(Dispatchers.Main) {
                    RIMTUtil.ShowToast(APP.currentActivity, e.message)
                }
            }
        }
    }

    private fun addOnlineToOrigin(
        index: Int,
        item: ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity
    ) {
        item.chars?.forEach {
            it.pool = item.pool
            it.ts = item.ts
            if (index >= 0) {
                originData.add(index, it)
            } else {
                originData.add(it)
            }
        }
    }

    private fun queryLocalData(uid: String) {
        val local = preferences.getString(uid + Key_data_append, "")
        if ((local?.length ?: 0) > 0) {
            originData = Gson().fromJson(
                local,
                object :
                    TypeToken<ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>>() {}.type
            )
        }
    }

    private fun saveData(uid: String) {
        val json = Gson().toJson(originData)
        preferences.edit().putString(uid + Key_data_append, json).apply()
    }

//    private fun getData() {
//
//    }

    interface OnArkCardAnalyzerCallback {
        fun onResult(result: ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>)
    }
}