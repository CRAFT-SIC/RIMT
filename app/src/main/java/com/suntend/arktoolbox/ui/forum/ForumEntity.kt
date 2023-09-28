package com.suntend.arktoolbox.ui.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForumEntity(var title: String?, var message:String) : Parcelable {

}
