package com.suntend.arktoolbox.ui.arkcardanalyzer

import RIMTUtil.ClipboardUtils
import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.suntend.arktoolbox.base.BasicActivity
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.RIMTUtil.AttrUtil
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil
import com.suntend.arktoolbox.enums.ArkCardPoolTypeEnum
import com.suntend.arktoolbox.widgets.ArkCardAnalyzerProgress
import com.zackratos.ultimatebarx.ultimatebarx.BuildConfig
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections


class ArkCardAnalyzerActivity : BasicActivity() {
    private var token: String? = null
    private lateinit var cardBefore: CardView
    private lateinit var linearInfo: LinearLayout
    private lateinit var editInput: EditText
    private var page = 1
    private val ALL_POOL_DATA_NAME = "整体数据概览"
    private var isQuerying = false
    private var arkUserName = "抽卡分析"
    private var arkUserId = ""

    //按卡池统计数据
    private var poolMap =
        HashMap<String, ArkGanchaShowEntity>()

    //剔除整体数据概览的数据
    private var poolArray = ArrayList<ArkGanchaShowEntity>()
    //展示保底的数据,,因为寻访可能出现相同的??
    //private var poolArray = ArrayList<ArkGanchaShowEntity>()


    private var currentPool = ALL_POOL_DATA_NAME

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
        //editInput.setText("{\"code\":0,\"data\":{\"content\":\"j7TkIOmgW6PxhvQRQv9rCbHm\"},\"msg\":\"\"}")
        editInput.setText("{\"code\":0,\"data\":{\"content\":\"Elm63jxXdU1/ZcUhwoAGWnER\"},\"msg\":\"\"}")
        findViewById<View>(R.id.btn_submit).setOnClickListener {
            val json = JSONObject(editInput.text.toString())
            val data = json.getJSONObject("data")
            token = data.getString("content")
            nextView()
        }

        nextView()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun nextView() {
        val tokenSuccess = checkToken()
        if (tokenSuccess) {
            if (isQuerying) {
                return
            }
            //状态重置,开始轮询
            page = 1
            isQuerying = true
            RIMTUtil.ShowToast(this@ArkCardAnalyzerActivity, "开始请求数据,请稍候")
            queryArkUserInfo()
        } else {
            linearInfo.visibility = View.GONE
            cardBefore.visibility = View.VISIBLE
        }
    }

    private fun updateUI() {
        val entity = poolMap[currentPool]
        entity?.apply {
            findViewById<TextView>(R.id.tv_pool_name).setText(entity.pool)
            findViewById<View>(R.id.linear_change_pool).setOnClickListener {

                ArkCardAnalyzerChangePoolDialog(this@ArkCardAnalyzerActivity)
                    .setData(poolMap.values.toList(), currentPool)
                    .setOnSelectPoolListener(object : OnSelectPoolListener {
                        override fun onSelect(entry: ArkGanchaShowEntity) {
                            currentPool = entry.pool
                            updateUI()
                        }
                    })
                    .show()
            }
            findViewById<View>(R.id.tv_month_statistics).setOnClickListener {
                ArkCardAnalyzerPoolInfoDialog(this@ArkCardAnalyzerActivity).setData(
                    poolArray,
                    poolMap[ALL_POOL_DATA_NAME]!!.rollList
                )
                    .setOnSelectPoolListener(object : OnSelectPoolListener {
                        override fun onSelect(entry: ArkGanchaShowEntity) {
                            currentPool = entry.pool
                            updateUI()
                        }
                    })
                    .show()
            }
            findViewById<TextView>(R.id.tv_roll_max_count).setText("共 " + entity.rollList.size + " 抽")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = entity.minTs * 1000L
            val startTime = SimpleDateFormat("yyyy/MM/dd").format(calendar.time)
            calendar.timeInMillis = entity.maxTs * 1000L
            val endTime = SimpleDateFormat("yyyy/MM/dd").format(calendar.time)
            findViewById<TextView>(R.id.tv_time_interval).setText("统计时间:" + startTime + "-" + endTime)
            findViewById<TextView>(R.id.tv_pool_all_count).setText((entity.rollList.size).toString() + "人")
            findViewById<TextView>(R.id.tv_six_all_count).setText(
                (entity.levelMap.get(6)?.size ?: 0).toString() + "人"
            )
            findViewById<TextView>(R.id.tv_five_all_count).setText(
                (entity.levelMap.get(5)?.size ?: 0).toString() + "人"
            )
            findViewById<TextView>(R.id.tv_four_all_count).setText(
                (entity.levelMap.get(4)?.size ?: 0).toString() + "人"
            )
            findViewById<TextView>(R.id.tv_six_average_count).setText(
                (if ((entity.levelMap.get(6)?.size
                        ?: 0) == 0
                ) "-" else (entity.rollList.size / (entity.levelMap.get(6)?.size
                    ?: 0)).toString())
            )
            findViewById<TextView>(R.id.tv_five_average_count).setText(
                (if ((entity.levelMap.get(5)?.size
                        ?: 0) == 0
                ) "-" else (entity.rollList.size / (entity.levelMap.get(5)?.size
                    ?: 0)).toString())
            )

            val progress =
                findViewById<ArkCardAnalyzerProgress>(R.id.ark_card_analyzer_progress)
            progress.setTextColor(
                AttrUtil.getColor(R.attr.ark_card_analyzer_accent_text_color),
                AttrUtil.getColor(R.attr.ark_label_general_text_color)
            )
            progress.setProgressBackground(AttrUtil.getColor(R.attr.ark_card_analyzer_progress_background_color))
            val color1 = AttrUtil.getColor(R.attr.ark_card_analyzer_progress_1_color)
            val color2 = AttrUtil.getColor(R.attr.ark_card_analyzer_progress_2_color)
            progress.setProgress(
                arrayListOf(
                    (entity.levelMap[6]?.size ?: 0) / entity.rollList.size.toDouble(),
                    (entity.levelMap[5]?.size ?: 0) / entity.rollList.size.toDouble(),
                    (entity.levelMap[4]?.size ?: 0) / entity.rollList.size.toDouble(),
                    (entity.levelMap[3]?.size ?: 0) / entity.rollList.size.toDouble(),
                ),
                arrayListOf(
                    intArrayOf(
                        0xFFFFD981.toInt(), 0xFFD5FF81.toInt(), 0xFF1CFFF0.toInt()
                    ),
                    intArrayOf(0xFF0256FF.toInt(), 0xFF0256FF.toInt()),
                    intArrayOf(color2, color2),
                    intArrayOf(color1, color1)
                )
            )

            val mRv = findViewById<RecyclerView>(R.id.rv)
            mRv.layoutManager = LinearLayoutManager(this@ArkCardAnalyzerActivity)
            mRv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): VH {
                    return VH(
                        LayoutInflater.from(this@ArkCardAnalyzerActivity)
                            .inflate(R.layout.item_card_analyzer_guarantee, parent, false)
                    )
                }

                override fun getItemCount(): Int {
                    return poolArray.size
                }

                @SuppressLint("SetTextI18n")
                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val vh = holder as VH
                    poolArray[position].let {
                        var typeStr =
                            if (it.poolType == ArkCardPoolTypeEnum.BOOT) "新手" else if (ArkCardPoolTypeEnum.isNormalPool(
                                    it.poolType
                                )
                            ) "普通" else if (ArkCardPoolTypeEnum.isClassicPool(it.poolType)) "中坚" else "限定"
                        vh.mTvPoolType.text = "【$typeStr】"
                        vh.mTvPoolName.text = it.pool
                        vh.mTvPoolName.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                        vh.mTvPoolName.paint.isAntiAlias = true
                        vh.mTvPoolName.setOnClickListener { v ->
                            ArkCardAnalyzerPoolInfoDialog(this@ArkCardAnalyzerActivity).setData(
                                arrayListOf(it),
                                it.rollList
                            )
                                .setOnSelectPoolListener(object : OnSelectPoolListener {
                                    override fun onSelect(entry: ArkGanchaShowEntity) {
                                        currentPool = entry.pool
                                        updateUI()
                                    }
                                })
                                .show()
                        }
                        vh.mTvGuarantee.text = it.guaranteeCount.toString()
                    }
                    vh.divider.visibility =
                        if (position == poolArray.size - 1) View.GONE else View.VISIBLE
                }

                inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
                    var mTvPoolName: TextView
                    var mTvPoolType: TextView
                    var mTvGuarantee: TextView
                    var divider: View

                    init {
                        mTvPoolType = itemView.findViewById(R.id.tv_pool_type)
                        mTvPoolName = itemView.findViewById(R.id.tv_pool_name)
                        mTvGuarantee = itemView.findViewById(R.id.tv_guarantee_count)
                        divider = itemView.findViewById(R.id.divider)
                    }

                }

            }

        }


    }

    private fun parseData(originData: ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>) {
        //根据原始数据对数据进行分解
        poolMap.clear()
        poolMap[ALL_POOL_DATA_NAME] = ArkGanchaShowEntity().apply {
            pool = ALL_POOL_DATA_NAME
            poolType = ArkCardPoolTypeEnum.ALL
        }
        originData.forEach {
            if (!poolMap.containsKey(it.pool)) {
                val entity = ArkGanchaShowEntity()
                entity.pool = it.pool
                poolMap[it.pool] = entity
                entity.poolType = ArkCardPoolTypeEnum.poolNameOf(it.pool)
            }
            poolMap[it.pool]?.let { pool ->
                pool.rollList.add(it)
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
            poolMap[ALL_POOL_DATA_NAME]?.let { pool ->
                pool.rollList.add(it)
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
        //整体数据统合计算
        poolMap.forEach {
            it.value.let { pool ->
                var allRoll = 0
                var sixGet = false

                pool.rollList.forEach { roll ->
                    if (!sixGet) {
                        if (roll.level == 6) {
                            sixGet = true
                        } else {
                            pool.guaranteeCount++
                        }
                    }
                }
                pool.levelMap.forEach { level ->
                    allRoll += level.value.size
                }
                pool.averageLevelSix = (allRoll / ((pool.levelMap[6]?.size) ?: 1)).toFloat()
                pool.averageLevelFive = (allRoll / ((pool.levelMap[5]?.size) ?: 1)).toFloat()
            }
        }
        poolMap.forEach {
            if (it.key != ALL_POOL_DATA_NAME) {
                poolArray.add(it.value)
            }
        }
        Collections.sort(
            poolArray,
            Comparator<ArkCardAnalyzerActivity.ArkGanchaShowEntity> { o1, o2 ->
                val i: Int = o1.rollList.get(0).ts - o2.rollList.get(0).ts
                -i
            })
        runBlocking {
            launch(Dispatchers.Main) {
                //更新UI
                linearInfo.visibility = View.VISIBLE
                cardBefore.visibility = View.GONE
                findViewById<TextView>(R.id.tv_title_name).text = arkUserName
                updateUI()
            }
        }
    }

    private fun queryArkUserInfo() {
        MainScope().launch(Dispatchers.IO) {
            try {
                val url = "https://as.hypergryph.com/u8/user/info/v1/basic"
                val map = hashMapOf<String, Any>()
                map.put("appId", 1)
                map.put("channelMasterId", 1)
                map.put(
                    "channelToken",
                    hashMapOf<String, Any>().apply { put("token", token ?: "") })

                val request: Request = Request.Builder()
                    .url(url)
                    .post(Gson().toJson(map).toRequestBody())
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                val responseBody = response.body!!.string()
                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val data = json.getJSONObject("data")
                    arkUserName = data.getString("nickName")
                    arkUserId = data.getString("uid")
                    if (arkUserId == "") {
                        throw Exception("请求失败")
                    } else {
                        ArkCardAnalyzerManager.startQueryOnlineData(
                            arkUserId,
                            token!!,
                            object : ArkCardAnalyzerManager.OnArkCardAnalyzerCallback {
                                override fun onResult(result: ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>) {
                                    parseData(result)
                                }
                            })
                    }
                } else {
                    throw Exception("请求失败")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isQuerying = false
                launch(Dispatchers.Main) {
                    RIMTUtil.ShowToast(this@ArkCardAnalyzerActivity, e.message)
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
        var poolType = ArkCardPoolTypeEnum.NOTHING

        //存储最大最小时间,用于展示数据区间
        var minTs = Int.MAX_VALUE
        var maxTs = Int.MIN_VALUE

        //平均抽数,为0时展示-线
        var averageLevelSix = 0f
        var averageLevelFive = 0f

        //保底抽数,倒序查找上次6星出货
        var guaranteeCount = 0

        //抽卡所有数据-时间排序
        var rollList =
            ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>()

        //以等级统计所有数据
        var levelMap =
            HashMap<Int, ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>>()

        //以月份统计所有数据
        var monthMap =
            HashMap<Int, ArrayList<ArkGachaResultEntity.ArkGachaDataEntity.ArkGachaDataItemEntity.ArkGachaCharEntity>>()

        override fun toString(): String {
            return "ArkGanchaShowEntity(pool='$pool', minTs=$minTs, maxTs=$maxTs, averageLevelSix=$averageLevelSix, averageLevelFive=$averageLevelFive, guaranteeCount=$guaranteeCount, rollList=${rollList.size}, levelMap=${levelMap.size}, monthMap=${monthMap.size})"
        }


    }
}