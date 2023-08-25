package com.suntend.arktoolbox.ui.arkofficial

import android.os.Parcelable
import com.suntend.arktoolbox.enums.PlatformIconEnum
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArkOfficialEntity(
    var cover: String,
    var name: String,
    var subName: String,
    var createTime: Long,
    var desc: String,
    var links: ArrayList<ArkOfficialLinkEntity>,
) : Parcelable {
}

@Parcelize
data class ArkOfficialLinkEntity(var platform: PlatformIconEnum, var url: String) : Parcelable {

}