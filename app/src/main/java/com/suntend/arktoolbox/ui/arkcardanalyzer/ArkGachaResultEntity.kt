package com.suntend.arktoolbox.ui.arkcardanalyzer

class ArkGachaResultEntity {
    var code = 0
    var data: ArkGachaDataEntity? = null

    inner class ArkGachaDataEntity {
        var list: ArrayList<ArkGachaDataItemEntity>? = null
        var pagination: ArkGachaPaginationEntity? = null
        inner class ArkGachaDataItemEntity {
            var ts = 0 //时间
            var pool: String="" //卡池
            var chars: ArrayList<ArkGachaCharEntity>? = null

            inner class ArkGachaCharEntity {
                var ts = 0 //时间 接口未返回,需要手动赋值
                var pool: String="" //卡池 接口未返回,需要手动赋值
                var name: String? = null
                var rarity = 0 //稀有登记,因为没有2星角色,所以3星角色返回的值为2,4星返回3
                var isNew = false
                val level: Int
                    get() = if (rarity == 1) 1 else rarity + 1
            }
        }

        inner class ArkGachaPaginationEntity {
            var current = 0
            var total = 0
            val hasNext:Boolean
                get() = maxPage > current
            val maxPage: Int
                get() = Math.ceil((total / 10f).toDouble()).toInt()
        }
    }



}