package io.github.silascui.ktIztro.ai.prompt

import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Horoscope

object AiPromptBuilder {

    /**
     * 系统级人设与指令
     */
    fun buildSystemPrompt(): String {
        return """
你是一位精通传统紫微斗数（三合派与飞星派理法）、结合现代认知心理学与职业生涯规划的顶级命理大师。
你的任务是根据用户提供的【客观紫微斗数排盘数据事实】，进行专业、通俗、深入且具有指导意义的解读。

【分析原则与规范】
1. 绝对忠实于提供的星盘事实：主星庙旺平陷、吉煞会聚、生年四化（禄权科忌）以及运限流耀，切勿自行捏造星曜。
2. 辩证客观看待：吉星非全吉，煞星非全凶（如擎羊主果决开创，火贪主爆发力，文昌化忌防文书疏漏）。避免宿命论或恐吓性言论，重在“明局、知势、趋吉避凶”。
3. 结构化输出：采用清晰的 Markdown 标题、小节与要点，层次分明，逻辑严密。
4. 提供务实建议：在分析性格、事业、财富、感情或运势之后，必须结合现代生活给出切实可行的行动与规划建议。
        """.trimIndent()
    }

    /**
     * 格式化排盘数据为紧凑严谨的上下文字符串
     */
    fun buildChartContext(astrolabe: Astrolabe, horoscope: Horoscope?): String {
        val sb = StringBuilder()
        sb.appendLine("### 【命盘基础数据】")
        sb.appendLine("- 性别: ${astrolabe.gender.yinYang}${astrolabe.gender.chinese}")
        sb.appendLine("- 阳历生日: ${astrolabe.solarDate} ${astrolabe.time} (${astrolabe.timeRange})")
        sb.appendLine("- 农历生日: ${astrolabe.lunarDate}")
        sb.appendLine("- 生辰干支八字: ${astrolabe.chineseDate}")
        sb.appendLine("- 生肖星座: ${astrolabe.zodiac} (${astrolabe.sign})")
        sb.appendLine("- 五行局: ${astrolabe.fiveElementsClass}")
        sb.appendLine("- 命主: ${astrolabe.soul}，身主: ${astrolabe.body}")
        sb.appendLine("- 命宫所在: ${astrolabe.earthlyBranchOfSoulPalace}宫，身宫所在: ${astrolabe.earthlyBranchOfBodyPalace}宫")
        sb.appendLine()

        sb.appendLine("### 【十二宫位详列】")
        astrolabe.palaces.forEach { p ->
            val majorStr = if (p.majorStars.isEmpty()) "空宫(无主星)" else p.majorStars.joinToString("、") { s ->
                val b = if (s.brightness.isNotEmpty()) "(${s.brightness})" else ""
                val m = if (s.mutagen.isNotEmpty()) "[化${s.mutagen}]" else ""
                "${s.name}$b$m"
            }

            val minorStr = if (p.minorStars.isEmpty()) "无" else p.minorStars.joinToString("、") { s ->
                val b = if (s.brightness.isNotEmpty()) "(${s.brightness})" else ""
                val m = if (s.mutagen.isNotEmpty()) "[化${s.mutagen}]" else ""
                "${s.name}$b$m"
            }

            val adjStr = p.adjectiveStars.take(4).joinToString("、") { it.name }
            val bodyTag = if (p.isBodyPalace) " [身宫]" else ""

            sb.appendLine("- 【${p.name}$bodyTag】(${p.heavenlyStem}${p.earthlyBranch}宫 | 大限: ${p.decadal.range.first}-${p.decadal.range.second}岁):")
            sb.appendLine("  * 主星: $majorStr")
            sb.appendLine("  * 辅煞星曜: $minorStr")
            if (adjStr.isNotEmpty()) sb.appendLine("  * 杂曜神煞: $adjStr | 长生: ${p.changsheng12} | 博士: ${p.boshi12} | 岁建: ${p.suiqian12}")
        }
        sb.appendLine()

        val mingGong = astrolabe.palace("命宫") ?: astrolabe.palaces[0]
        val sanFang = astrolabe.getSanFangSiZheng(mingGong.index)
        sb.appendLine("### 【命宫三方四正格局】")
        sb.appendLine("- 本宫: 命宫(${sanFang.self.heavenlyStem}${sanFang.self.earthlyBranch}) -> ${sanFang.self.majorStars.joinToString { it.name }}")
        sb.appendLine("- 对宫: 迁移(${sanFang.opposite.heavenlyStem}${sanFang.opposite.earthlyBranch}) -> ${sanFang.opposite.majorStars.joinToString { it.name }}")
        sb.appendLine("- 官禄位: 官禄(${sanFang.career.heavenlyStem}${sanFang.career.earthlyBranch}) -> ${sanFang.career.majorStars.joinToString { it.name }}")
        sb.appendLine("- 财帛位: 财帛(${sanFang.wealth.heavenlyStem}${sanFang.wealth.earthlyBranch}) -> ${sanFang.wealth.majorStars.joinToString { it.name }}")
        sb.appendLine()

        if (horoscope != null) {
            sb.appendLine("### 【当前运限分析时刻 (${horoscope.solarDate})】")
            sb.appendLine("- 当前虚岁: ${horoscope.age.nominalAge}岁")
            sb.appendLine("- 当前大限: ${horoscope.decadal.name}(${horoscope.decadal.palaceNames[0]}宫 - ${horoscope.decadal.heavenlyStem}${horoscope.decadal.earthlyBranch})，大限四化: ${horoscope.decadal.mutagen.joinToString()}")
            sb.appendLine("- 当前流年: ${horoscope.yearly.name}(${horoscope.yearly.palaceNames[0]}宫 - ${horoscope.yearly.heavenlyStem}${horoscope.yearly.earthlyBranch})，流年四化: ${horoscope.yearly.mutagen.joinToString()}")
        }

        return sb.toString()
    }

    /**
     * 构建解盘问题提示词
     */
    fun buildQuestionPrompt(topic: String, customQuestion: String? = null): String {
        return when (topic) {
            "全盘总论" -> """
请根据以上命盘数据，为我进行【全盘性格与人生格局综合总论】：
1. 【核心性格特质与心性模型】：分析命宫与身宫主星、气性、优缺点、内在动机。
2. 【格局高低与成就倾向】：分析三方四正会合星曜，看是否有特殊格局（如杀破狼、机月同梁、紫府朝垣、火贪格等），分析人生整体成就上限与发展阻力。
3. 【关键人生转折与建议】：指出命盘中最大的优势天赋与需要注意的人生盲点，并提供 3 条核心修身与成长建议。
            """.trimIndent()

            "事业官禄" -> """
请根据以上命盘数据，深度剖析我的【事业发展与职业方向】：
1. 【事业宫与命宫配合】：分析官禄宫主辅星与生年四化，适合体制内考公/国企、大厂职场深耕，还是独立创业/自由职业？
2. 【行业与赛道推荐】：结合命格特质，最容易发挥天赋、有所建树的行业或岗位领域是什么？
3. 【职场人际与升迁运】：职场中贵人运（魁钺、左右）与小人阻力（擎羊、陀罗、空劫等）如何？如何把握升迁节点？
            """.trimIndent()

            "财运财富" -> """
请根据以上命盘数据，详细解读我的【一生财富格局与理财运势】：
1. 【正财与偏财格局】：财帛宫与田宅宫会合星曜如何？属于细水长流稳健积攒型，还是爆发型或劳碌求财型？
2. 【进财渠道与资产积累】：命中是否有禄存、化禄或火贪禄马交驰？不动产购置（田宅宫）运势如何？
3. 【破财陷阱与避险建议】：命盘中是否有化忌冲财、地劫空耗等破耗隐患？在投资、合伙与理财方面应注意什么？
            """.trimIndent()

            "婚恋感情" -> """
请根据以上命盘数据，详细解读我的【婚姻情感与相处之道】：
1. 【夫妻宫特质与配偶画像】：夫妻宫星曜、对宫官禄配合，推导未来正缘配偶的性格特质、外形气质或相识契机。
2. 【情感模式与波折提醒】：命盘中是否有桃花星（贪狼、廉贞、红鸾、天喜）或孤克星曜（天刑、孤辰、寡宿、化忌）？情感生活中容易遇到的主要矛盾点是什么？
3. 【良性相处之道与择偶建议】：在亲密关系中如何沟通相处，怎样的伴侣类型与相处节奏最有利于婚姻长久美满？
            """.trimIndent()

            "流年运势" -> """
请重点结合当前运限数据，为我深度推演【当前大限与流年运势】：
1. 【大限十年大势】：分析当前所在大限的整体运势基调、重点关注宫位（如财帛、官禄或疾厄）。
2. 【当前流年吉凶详解】：分析当前流年干支、流年四化（流禄、流权、流科、流忌）落入何宫，对生活产生何种实质影响？
3. 【流年具体避坑指南与行动建议】：今年适合积极进攻、转换跑道，还是宜守成充电、谨言慎行？给出年度关键决策建议。
            """.trimIndent()

            "健康疾厄" -> """
请根据疾厄宫、命宫及五行局数据，进行【体质保养与健康提示】：
1. 【先天五行偏旺偏弱】：结合五行局与疾厄宫星曜，先天较为敏感或易疲劳的身体脏腑系统是什么？
2. 【日常起居与保健方向】：适合的生活作息、运动方式与身心调理建议。
（注：仅作传统命理养生参考，非医疗诊断）
            """.trimIndent()

            else -> customQuestion ?: "请综合分析我的紫微斗数命盘核心格局与关键特征。"
        }
    }
}
