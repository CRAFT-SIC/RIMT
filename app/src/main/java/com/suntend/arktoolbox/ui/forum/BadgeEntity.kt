package com.suntend.arktoolbox.ui.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BadgeEntity(var title: String?, var message:String) : Parcelable {

}
