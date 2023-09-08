package com.suntend.arktoolbox.enums

enum class ArkCardPoolTypeEnum(val poolName: String) {
    ALL("所有数据"),
    BOOT("新人特惠寻访"),
    NORM("常驻标准寻访"), SINGLE("定向标准寻访"),
    CLASSIC("常规中坚寻访"), FESCLASSIC("中坚甄选"),
    LIMITED("限定寻访"), LINKAGE("联动寻访"),
    ATTAIN("跨年欢庆寻访"), NOTHING("未知");

    companion object {
        fun poolNameOf(poolName: String): ArkCardPoolTypeEnum {
            ArkCardPoolTypeEnum.values().forEach {
                if (it.poolName == poolName) {
                    return it
                }
            }
            return NOTHING
        }

        fun isSimpleNormal(type: ArkCardPoolTypeEnum): Boolean {
            return type == NORM || type == SINGLE || type == CLASSIC || type == FESCLASSIC || type == BOOT
        }

        fun isNormalPool(type: ArkCardPoolTypeEnum): Boolean {
            return type == NORM || type == SINGLE
        }

        fun isClassicPool(type: ArkCardPoolTypeEnum): Boolean {
            return type == CLASSIC || type == FESCLASSIC
        }
    }
}