package com.suntend.arktoolbox.ui.arkofficial

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suntend.arktoolbox.APP

class ArkOfficialManager {

    companion object {
        private val list = ArrayList<ArkOfficialEntity>()
        suspend fun loadData(): ArrayList<ArkOfficialEntity> {
            if (list.isEmpty()) {
                APP.appContext.assets.open("ark_official_json").use { bis ->
                    var value = bis.reader().readText()
                    val data: ArrayList<ArkOfficialEntity>? =
                        Gson().fromJson<java.util.ArrayList<ArkOfficialEntity>>(
                            value,
                            object : TypeToken<java.util.ArrayList<ArkOfficialEntity>>() {}.type
                        )

                    data?.let {
                        list.clear()
                        list.addAll(it)
                    }
                }
            }
            return list
        }
    }
}