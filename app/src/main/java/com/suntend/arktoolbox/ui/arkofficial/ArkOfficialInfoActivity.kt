package com.suntend.arktoolbox.ui.arkofficial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.enums.PlatformIconEnum
import com.suntend.arktoolbox.widgets.OfficialInfoPlatformLinksLayout
import java.text.SimpleDateFormat
import java.util.Date

class ArkOfficialInfoActivity : AppCompatActivity() {
    private lateinit var entity: ArkOfficialEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_ark_official_info)

        var mIvCover = findViewById<ImageView>(R.id.iv_cover)
        var mCardCover = findViewById<CardView>(R.id.card_cover)
        val mTvName = findViewById<TextView>(R.id.tv_name)
        val mTvSubName = findViewById<TextView>(R.id.tv_sub_name)
        val mTvTitleName = findViewById<TextView>(R.id.tv_title_name)
        val mTvDesc = findViewById<TextView>(R.id.tv_desc)
        val mTvCreateTime = findViewById<TextView>(R.id.tv_create_time)
        val mLinks = findViewById<OfficialInfoPlatformLinksLayout>(R.id.platform_links)

        entity = intent.getParcelableExtra<ArkOfficialEntity>("bean")!!
        Glide.with(this).load(entity.cover).into(mIvCover)
        mTvName.setText(entity.name)
        mTvTitleName.setText(entity.name)
        mTvSubName.setText(entity.subName)
        mTvDesc.setText(entity.desc)
        mTvCreateTime.setText(
            "创建时间     " + SimpleDateFormat("yyyy年-MM月-dd日").format(
                Date(
                    entity.createTime
                )
            )
        )
        mLinks.setData(entity.links)
        mLinks.setOnChildClickListener(object :OfficialInfoPlatformLinksLayout.OnChildClickListener{
            override fun onClick(entity: ArkOfficialLinkEntity) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entity.url))
                startActivity(intent)
            }
        })


        ViewCompat.setTransitionName(mCardCover, "cardCover")
        ViewCompat.setTransitionName(mTvName, "name")
        ViewCompat.setTransitionName(mTvTitleName, "titleName")
        ViewCompat.setTransitionName(mTvSubName, "subName")
        ViewCompat.setTransitionName(mTvDesc, "desc")
        // ViewCompat.setTransitionName(mTvCreateTime, "createTime")
        //ViewCompat.setTransitionName(mRv, "platform")
        startPostponedEnterTransition()
    }
}