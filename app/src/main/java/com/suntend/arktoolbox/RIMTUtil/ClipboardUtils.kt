package com.suntend.arktoolbox.RIMTUtil

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {
    //获取粘贴板的信息
    fun getContent(context: Context): String {
        try {
            val clipManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            //无数据时直接返回
            if (clipManager == null || !clipManager.hasPrimaryClip()) {
                return ""
            }
            //如果是文本信息
            if (clipManager.primaryClipDescription != null && clipManager.primaryClipDescription!!
                    .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            ) {
                val clip = clipManager.primaryClip
                if (clip != null) {
                    val item = clip.getItemAt(0)
                    if (item.text != null) {
                        //此处是TEXT文本信息
                        return item.text.toString()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun copyString(context: Context, content: String?) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (content != null) {
            cm.setPrimaryClip(ClipData.newPlainText("", content))
        } else {
        }
    }
}