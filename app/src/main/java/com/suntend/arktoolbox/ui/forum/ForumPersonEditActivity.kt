package com.suntend.arktoolbox.ui.forum

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.base.BasicActivity
import com.suntend.arktoolbox.widgets.BadgeLinearLayout

class ForumPersonEditActivity : BasicActivity() {
    private lateinit var linearBadge: BadgeLinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_person_edit)
        linearBadge = findViewById<BadgeLinearLayout>(R.id.linear_badge)
        (linearBadge.parent as View).setOnClickListener {
            startActivity(Intent(this@ForumPersonEditActivity, BadgeListActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        //更新
    }
}