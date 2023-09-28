package com.suntend.arktoolbox.ui.forum

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.suntend.arktoolbox.R
import com.suntend.arktoolbox.base.BasicActivity

class ForumPublishActivity : BasicActivity() {
    private lateinit var mEditTitle: EditText
    private lateinit var mEditMessage: EditText
    private var selectIndex = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_publish)
        mEditTitle = findViewById<EditText>(R.id.edit_title)
        mEditMessage = findViewById<EditText>(R.id.edit_message)
        var mRv = findViewById<RecyclerView>(R.id.rv)

        findViewById<View>(R.id.iv_submit).setOnClickListener {
            finish()
        }
    }
}