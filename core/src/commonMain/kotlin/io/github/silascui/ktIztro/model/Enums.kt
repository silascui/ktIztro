package io.github.silascui.ktIztro.model

/**
 * 十天干
 */
enum class HeavenlyStem(val chinese: String, val yinYang: String, val fiveElements: String) {
    JIA("甲", "阳", "木"),
    YI("乙", "阴", "木"),
    BING("丙", "阳", "火"),
    DING("丁", "阴", "火"),
    WU("戊", "阳", "土"),
    JI("己", "阴", "土"),
    GENG("庚", "阳", "金"),
    XIN("辛", "阴", "金"),
    REN("壬", "阳", "水"),
    GUI("癸", "阴", "水");

    companion object {
        fun fromChinese(name: String): HeavenlyStem {
            return entries.firstOrNull { it.chinese == name }
                ?: throw IllegalArgumentException("Unknown HeavenlyStem: $name")
        }
    }
}

/**
 * 十二地支
 */
enum class EarthlyBranch(
    val chinese: String,
    val yinYang: String,
    val fiveElements: String,
    val soul: String,
    val body: String,
    val animal: String
) {
    ZI("子", "阳", "水", "贪狼", "火星", "鼠"),
    CHOU("丑", "阴", "土", "巨门", "天相", "牛"),
    YIN("寅", "阳", "木", "禄存", "天梁", "虎"),
    MAO("卯", "阴", "木", "文曲", "天同", "兔"),
    CHEN("辰", "阳", "土", "廉贞", "文昌", "龙"),
    SI("巳", "阴", "火", "武曲", "天机", "蛇"),
    WU("午", "阳", "火", "破军", "火星", "马"),
    WEI("未", "阴", "土", "武曲", "天相", "羊"),
    SHEN("申", "阳", "金", "廉贞", "天梁", "猴"),
    YOU("酉", "阴", "金", "文曲", "天同", "鸡"),
    XU("戌", "阳", "土", "禄存", "文昌", "狗"),
    HAI("亥", "阴", "水", "巨门", "天机", "猪");

    companion object {
        fun fromChinese(name: String): EarthlyBranch {
            return entries.firstOrNull { it.chinese == name }
                ?: throw IllegalArgumentException("Unknown EarthlyBranch: $name")
        }
    }
}

/**
 * 五行局
 */
enum class FiveElementsClass(val number: Int, val chinese: String) {
    WATER_2ND(2, "水二局"),
    WOOD_3RD(3, "木三局"),
    METAL_4TH(4, "金四局"),
    EARTH_5TH(5, "土五局"),
    FIRE_6TH(6, "火六局");

    companion object {
        fun fromNumber(num: Int): FiveElementsClass = entries.first { it.number == num }
    }
}

/**
 * 性别
 */
enum class Gender(val chinese: String, val yinYang: String) {
    MALE("男", "阳"),
    FEMALE("女", "阴");

    companion object {
        fun fromChinese(name: String): Gender = if (name == "女") FEMALE else MALE
    }
}

/**
 * 四化类型
 */
enum class Mutagen(val chinese: String) {
    LU("禄"),
    QUAN("权"),
    KE("科"),
    JI("忌");

    companion object {
        fun fromChinese(name: String): Mutagen? = entries.firstOrNull { it.chinese == name }
    }
}

/**
 * 星曜亮度
 */
enum class Brightness(val chinese: String) {
    MIAO("庙"),
    WANG("旺"),
    DE("得"),
    LI("利"),
    PING("平"),
    BU("不"),
    XIAN("陷");

    companion object {
        fun fromChinese(name: String): Brightness? = entries.firstOrNull { it.chinese == name }
    }
}

/**
 * 星曜分类
 */
enum class StarType(val code: String, val displayName: String) {
    MAJOR("major", "主星"),
    SOFT("soft", "吉星"),
    TOUGH("tough", "煞星"),
    LUCUN("lucun", "禄存"),
    TIANMA("tianma", "天马"),
    FLOWER("flower", "桃花星"),
    HELPER("helper", "解神"),
    ADJECTIVE("adjective", "杂曜"),
    HOROSCOPE("horoscope", "流耀")
}

/**
 * 运限层级
 */
enum class HoroscopeScope(val code: String, val displayName: String) {
    ORIGIN("origin", "本盘"),
    DECADAL("decadal", "大限"),
    YEARLY("yearly", "流年"),
    MONTHLY("monthly", "流月"),
    DAILY("daily", "流日"),
    HOURLY("hourly", "流时"),
    AGE("age", "小限")
}
