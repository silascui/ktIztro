package io.github.silascui.ktIztro.ui.components

import io.github.silascui.ktIztro.model.Astrolabe

/**
 * 紫微斗数经典格局检测器
 */
data class AstrologicalPattern(
    val name: String,
    val type: PatternType,
    val description: String
)

enum class PatternType {
    AUSPICIOUS,  // 吉格 / 富贵格
    NEUTRAL,     // 变格 / 杂格
    INFLUENCE    // 特殊格局
}

object PatternDetector {

    fun detect(astrolabe: Astrolabe): List<AstrologicalPattern> {
        val patterns = mutableListOf<AstrologicalPattern>()
        val soulPalace = astrolabe.palace("命宫") ?: return patterns
        val soulIndex = soulPalace.index
        val sanFang = astrolabe.getSanFangSiZheng(soulIndex)

        val selfStars = soulPalace.majorStars.map { it.name }.toSet()
        val allSanFangMajorStars = (sanFang.self.majorStars +
                sanFang.opposite.majorStars +
                sanFang.career.majorStars +
                sanFang.wealth.majorStars).map { it.name }.toSet()

        val allSanFangStars = (sanFang.self.allStars() +
                sanFang.opposite.allStars() +
                sanFang.career.allStars() +
                sanFang.wealth.allStars())

        // 1. 紫府同宫格
        if (selfStars.contains("紫微") && selfStars.contains("天府")) {
            patterns.add(
                AstrologicalPattern(
                    name = "紫府同宫格",
                    type = PatternType.AUSPICIOUS,
                    description = "紫微天府同守命宫，帝王双星并临，一生衣禄丰盈，稳重有威仪。"
                )
            )
        } else if (allSanFangMajorStars.contains("紫微") && allSanFangMajorStars.contains("天府")) {
            // 紫府朝垣格
            patterns.add(
                AstrologicalPattern(
                    name = "紫府朝垣格",
                    type = PatternType.AUSPICIOUS,
                    description = "紫微天府在三方朝拱命宫，主一生富贵双全，食禄无忧，贵人得力。"
                )
            )
        }

        // 2. 杀破狼格
        val spl = setOf("七杀", "破军", "贪狼")
        if (allSanFangMajorStars.containsAll(spl)) {
            patterns.add(
                AstrologicalPattern(
                    name = "杀破狼格",
                    type = PatternType.INFLUENCE,
                    description = "七杀破军贪狼相会，主人生充满突破与变动，开拓进取，大器晚成之栋梁。"
                )
            )
        }

        // 3. 府相朝垣格
        if (allSanFangMajorStars.contains("天府") && allSanFangMajorStars.contains("天相") && !selfStars.contains("天府")) {
            patterns.add(
                AstrologicalPattern(
                    name = "府相朝垣格",
                    type = PatternType.AUSPICIOUS,
                    description = "天府天相在三方朝会命宫，主富贵祥和，人际圆融，衣锦还乡。"
                )
            )
        }

        // 4. 机月同梁格
        val jytl = setOf("天机", "太阴", "天同", "天梁")
        if (allSanFangMajorStars.containsAll(jytl)) {
            patterns.add(
                AstrologicalPattern(
                    name = "机月同梁格",
                    type = PatternType.AUSPICIOUS,
                    description = "机月同梁作吏人，擅于策划组织、智囊分析、公务教职，一生稳步亨通。"
                )
            )
        }

        // 5. 日丽中天格 (太阳在午)
        if (selfStars.contains("太阳") && soulPalace.earthlyBranch == "午") {
            patterns.add(
                AstrologicalPattern(
                    name = "日丽中天格",
                    type = PatternType.AUSPICIOUS,
                    description = "太阳在午宫正位，光芒万丈，主位高权重，名扬四海，行事光明磊落。"
                )
            )
        }

        // 6. 月朗天门格 (太阴在亥)
        if (selfStars.contains("太阴") && soulPalace.earthlyBranch == "亥") {
            patterns.add(
                AstrologicalPattern(
                    name = "月朗天门格",
                    type = PatternType.AUSPICIOUS,
                    description = "太阴在亥宫守命，清辉映水，主人品端秀，才华卓绝，富贵双全。"
                )
            )
        }

        // 7. 巨日同宫格 (太阳巨门同宫)
        if (selfStars.contains("太阳") && selfStars.contains("巨门")) {
            patterns.add(
                AstrologicalPattern(
                    name = "巨日同宫格",
                    type = PatternType.AUSPICIOUS,
                    description = "太阳巨门同守，化暗为明，主能言善辩，利于涉外、司法、学术名扬。"
                )
            )
        }

        // 8. 石中隐玉格 (巨门在子午)
        if (selfStars.contains("巨门") && (soulPalace.earthlyBranch == "子" || soulPalace.earthlyBranch == "午")) {
            patterns.add(
                AstrologicalPattern(
                    name = "石中隐玉格",
                    type = PatternType.AUSPICIOUS,
                    description = "巨门在子午守命，如玉隐顽石，早岁历练艰辛，中晚年展露惊世才华。"
                )
            )
        }

        // 9. 火贪格 / 铃贪格
        for (p in astrolabe.palaces) {
            val names = p.allStars().map { it.name }.toSet()
            if (names.contains("贪狼") && names.contains("火星")) {
                patterns.add(
                    AstrologicalPattern(
                        name = "火贪爆发格",
                        type = PatternType.AUSPICIOUS,
                        description = "贪狼与火星在${p.name}同度，主横发暴发，财运机遇突飞猛进。"
                    )
                )
                break
            } else if (names.contains("贪狼") && names.contains("铃星")) {
                patterns.add(
                    AstrologicalPattern(
                        name = "铃贪爆发格",
                        type = PatternType.AUSPICIOUS,
                        description = "贪狼与铃星在${p.name}同度，运限逢之暗中崛起，财官皆遂。"
                    )
                )
                break
            }
        }

        // 10. 三奇加会格 (三方四正同时见化禄、化权、化科)
        val mutagens = allSanFangStars.mapNotNull { it.mutagen.ifEmpty { null } }.toSet()
        if (mutagens.contains("禄") && mutagens.contains("权") && mutagens.contains("科")) {
            patterns.add(
                AstrologicalPattern(
                    name = "三奇加会格",
                    type = PatternType.AUSPICIOUS,
                    description = "命逢三奇会化禄、化权、化科，贵气非常，声名远播，位极人臣之兆。"
                )
            )
        }

        // 11. 左右同宫 / 左右夹命
        val zuoFuPalace = astrolabe.findPalaceWithStar("左辅")
        val youBiPalace = astrolabe.findPalaceWithStar("右弼")
        if (zuoFuPalace != null && youBiPalace != null) {
            if (zuoFuPalace.index == soulIndex && youBiPalace.index == soulIndex) {
                patterns.add(
                    AstrologicalPattern(
                        name = "左右同宫格",
                        type = PatternType.AUSPICIOUS,
                        description = "左辅右弼同在命宫，多得众人辅佐，处事宽厚，一生贵人不断。"
                    )
                )
            }
        }

        return patterns.distinctBy { it.name }
    }

    private fun io.github.silascui.ktIztro.model.Palace.allStars(): List<io.github.silascui.ktIztro.model.Star> {
        return majorStars + minorStars + adjectiveStars
    }
}
