package com.suntend.arktoolbox.ui.arkcardanalyzer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.ClipboardUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar
import kotlin.concurrent.thread

class ArkCardAnalyzerActivity : AppCompatActivity() {
    private var token: String? = null
    private lateinit var cardBefore: CardView
    private lateinit var linearInfo: LinearLayout
    private lateinit var editInput: EditText
    private var page = 1

    //按卡池统计数据
    private var poolMap =
        HashMap<String, ArkGanchaShowEntity>()

    //原始数据
    private var originData =
        ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ark_card_analyzer)
        cardBefore = findViewById(R.id.card_before)
        linearInfo = findViewById(R.id.linear_info)
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.tv_copy_line).setOnClickListener {
            ClipboardUtils.copyString(this, "https://web-api.hypergryph.com/account/info/hg")
        }
        editInput = findViewById(R.id.edit_input)
        editInput.setText("{\"code\":0,\"data\":{\"content\":\"j7TkIOmgW6PxhvQRQv9rCbHm\"},\"msg\":\"\"}")
        findViewById<View>(R.id.btn_submit).setOnClickListener {
            val json = JSONObject(editInput.text.toString())
            val data = json.getJSONObject("data")
            token = data.getString("content")
            nextView()
        }

        nextView()

    }

    private fun nextView() {
        val tokenSuccess = checkToken()
        linearInfo.visibility = if (tokenSuccess) View.VISIBLE else View.GONE
        cardBefore.visibility = if (!tokenSuccess) View.VISIBLE else View.GONE
        if (tokenSuccess) {
            //状态重置,开始轮询
            originData.clear()
            page = 1
            queryGachaPage()

        } else {

        }
    }

    private  fun parseData() {
        Log.e("eeeeee","parseData:"+page)
        //根据原始数据对数据进行分解
        poolMap.clear()
        poolMap["整体数据概览"] = ArkGanchaShowEntity().apply {
            pool = "整体数据概览"
        }
        originData.forEach {
            if (!poolMap.containsKey(it.pool)) {
                val entity = ArkGanchaShowEntity()
                entity.pool = it.pool
                poolMap[it.pool] = entity
            }
            poolMap[it.pool]?.let { pool ->
                if (!pool.levelMap.containsKey(it.level)) {
                    pool.levelMap[it.level] = ArrayList()
                }
                pool.levelMap[it.level]?.add(it)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.ts * 1000L
                val month = calendar.get(Calendar.MONTH) + 1
                if (it.ts < pool.minTs) {
                    pool.minTs = it.ts
                }
                if (it.ts > pool.maxTs) {
                    pool.maxTs = it.ts
                }
                if (!pool.monthMap.containsKey(month)) {
                    pool.monthMap[month] = ArrayList()
                }
                pool.monthMap[month]?.add(it)
            }
            poolMap["整体数据概览"]?.let { pool ->
                if (!pool.levelMap.containsKey(it.level)) {
                    pool.levelMap[it.level] = ArrayList()
                }
                pool.levelMap[it.level]?.add(it)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.ts * 1000L
                val month = calendar.get(Calendar.MONTH) + 1
                if (it.ts < pool.minTs) {
                    pool.minTs = it.ts
                }
                if (it.ts > pool.maxTs) {
                    pool.maxTs = it.ts
                }
                if (!pool.monthMap.containsKey(month)) {
                    pool.monthMap[month] = ArrayList()
                }
                pool.monthMap[month]?.add(it)
            }
        }
        poolMap.forEach {
            it.value.let { pool ->
                var allRoll = 0
                pool.levelMap.forEach { level ->
                    allRoll += level.value.size
                }
                pool.averageLevelSix = (allRoll / ((pool.levelMap[6]?.size) ?: 1)).toFloat()
                pool.averageLevelFive = (allRoll / ((pool.levelMap[5]?.size) ?: 1)).toFloat()
            }
        }
        poolMap.forEach {
            Log.e("eeeeee",it.key+"  "+it.value.toString())
        }
        runBlocking {
            launch(Dispatchers.Main) {
                //更新UI
                Log.e("eeeeee","parseData:"+"更新UI")
            }
        }
    }

    private fun queryGachaPage() {
        Log.e("eeeeee","queryGachaPage:"+page)
        runBlocking {
            launch(Dispatchers.IO){
                var url =
                    "https://ak.hypergryph.com/user/api/inquiry/gacha?channelId=1" + "&token=" + token + "&page=" + page
                try {
                    val request: Request = Request.Builder()
                        .url(url)
                        .get()
                        .build()
                    val response = OkHttpClient().newCall(request).execute()
                    val responseBody = response.body!!.string()
                    Log.e("eeeeee",responseBody)
                    if (response.isSuccessful) {
                        val result = Gson().fromJson(responseBody, ArkGachaResultEntity::class.java)
                        result.data?.list?.forEach { data ->
                            data.chars?.forEach {
                                it.ts = data.ts
                                it.pool = data.pool
                                originData.add(it)
                            }
                        }
                        if (result.data?.pagination?.hasNext == true) {
                            page++
                            queryGachaPage()
                        } else {
                            parseData()
                        }

                    } else {
                    }
                } catch (e: IOException) {
                }
            }
        }

    }

    //检查token是否有效
    private fun checkToken(): Boolean {
        if (token != null && token!!.isNotEmpty()) {
            return true
        }
        return false
    }

    public class ArkGanchaShowEntity {
        var pool: String = ""

        //存储最大最小时间,用于展示数据区间
        var minTs = Int.MAX_VALUE
        var maxTs = Int.MIN_VALUE
        var averageLevelSix = 0f
        var averageLevelFive = 0f

        //以等级统计所有数据
        var levelMap =
            HashMap<Int, ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>>()

        //以月份统计所有数据
        var monthMap =
            HashMap<Int, ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>>()

        override fun toString(): String {
            return "ArkGanchaShowEntity(pool='$pool', minTs=$minTs, maxTs=$maxTs, averageLevelSix=$averageLevelSix, averageLevelFive=$averageLevelFive, levelMapSize=${levelMap.size}, monthMapSize=${monthMap.size})"
        }

    }
}