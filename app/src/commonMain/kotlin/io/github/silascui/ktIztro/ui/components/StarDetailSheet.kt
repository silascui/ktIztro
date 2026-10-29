package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.ui.theme.MajorStarColor
import io.github.silascui.ktIztro.ui.theme.MutagenLuColor
import io.github.silascui.ktIztro.ui.theme.MutagenQuanColor
import io.github.silascui.ktIztro.ui.theme.SapphireBlue
import io.github.silascui.ktIztro.ui.theme.SoftStarColor
import io.github.silascui.ktIztro.ui.theme.ToughStarColor
import io.github.silascui.ktIztro.ui.theme.VermilionRed
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.model.StarType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarDetailSheet(
    star: Star?,
    onDismiss: () -> Unit
) {
    if (star == null) return

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 头部：星名、分类、亮度、四化
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = star.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (star.type) {
                            StarType.MAJOR -> MajorStarColor
                            StarType.SOFT -> SoftStarColor
                            StarType.TOUGH -> ToughStarColor
                            else -> VermilionRed
                        }
                    )

                    if (star.brightness.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE2E8F0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "亮度: ${star.brightness}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                        }
                    }

                    if (star.mutagen.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (star.mutagen) {
                                        "禄" -> MutagenLuColor
                                        "权" -> MutagenQuanColor
                                        "科" -> SapphireBlue
                                        else -> Color(0xFF8B5CF6)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "化${star.mutagen}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = star.type.displayName,
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 星曜释义与格局说明
            val desc = getStarDescription(star.name)

            Text(
                text = "星曜特征",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VermilionRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc.first,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "象意与赋性",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VermilionRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc.second,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("关闭")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getStarDescription(starName: String): Pair<String, String> {
    return when (starName) {
        "紫微" -> Pair(
            "紫微星为中天帝王星，属己土，专司官禄主。具威严、尊贵、包容与领导气魄。",
            "若得左辅右弼、文昌文曲相夹或同度，称为百官朝拱，主贵显富达；若孤立无援则为孤君在野。"
        )
        "天机" -> Pair(
            "天机星属乙木，南斗第一星，司智慧与谋略，善变动、思虑敏锐、多才多艺。",
            "天机为动星，喜动不喜静，擅长企划、分析与周旋。化禄财运机敏，化忌易钻牛角尖失眠。"
        )
        "太阳" -> Pair(
            "太阳星属丙火，中天主星，主光明、博爱、声名与贵气。代表父、夫与子。",
            "日生于寅卯辰巳午未为庙旺，主慷慨好客、威望赫奕；若落申酉戌亥子丑则光芒内敛，辛劳奔波。"
        )
        "武曲" -> Pair(
            "武曲星属辛金，北斗第六星，正财星，刚毅果决、质朴勤劳、重诺守信。",
            "武曲入命财帛丰盈，主刚强干练，善于实务经营；化权增威，化忌易有财务纠纷或固执孤僻。"
        )
        "天同" -> Pair(
            "天同星属壬水，南斗第四星，福德主，化气为福。性格温和、知足常乐、有童心。",
            "天同主享福，喜好艺术娱乐；不忌煞星适度磨练激发斗志，化禄安乐自在，化忌心神不宁。"
        )
        "廉贞" -> Pair(
            "廉贞星属丁火、乙木，北斗第五星，次桃花星，化气为囚。性格傲岸、重感情、机智善辩。",
            "廉贞在命吉则通权达变、事业有成；凶则行事偏激、触犯官非。化禄主感情丰沛，化忌多是非波折。"
        )
        "天府" -> Pair(
            "天府星属戊土，南斗令星，财帛与田宅库主。稳重大度、保守谨慎、善理财蓄积。",
            "天府为财库，不化忌，遇禄存或化禄为实库；若无禄且逢空劫则为空库、露库，主外表体面实则拮据。"
        )
        "太阴" -> Pair(
            "太阴星属癸水，中天主星，化气为富。主贞洁、温柔细腻、富于文采。代表母、妻与女。",
            "太阴夜生人入亥子丑为极贵；在酉戌亥为月明，主财运亨通、心思细腻；落陷则内向敏感。"
        )
        "贪狼" -> Pair(
            "贪狼星属甲木、癸水，北斗第一星，正桃花星与欲望之宿。多才多艺、圆融善交际、喜神仙术数。",
            "遇火星、铃星构成【火贪格】【铃贪格】，主爆发横发；化禄财路广阔，化忌则情海生波或克制欲望。"
        )
        "巨门" -> Pair(
            "巨门星属癸水，北斗第二星，化气为暗。主口才、是非、观察敏锐、怀疑与研究钻研。",
            "巨门擅长辩才、教学、法律与传播；庙旺化禄化权可成口舌生财，落陷或化忌易招口角暗昧。"
        )
        "天相" -> Pair(
            "天相星属壬水，南斗第二星，化气为印，专司衣食官禄。敦厚守信、公正体面、热心助人。",
            "天相遇善则善，遇恶则恶。最喜得禄荫夹印，吉庆祥和；最忌刑忌夹印，易受连累或陷入官非。"
        )
        "天梁" -> Pair(
            "天梁星属戊土，南斗第三星，化气为荫，清高老成、长寿与逢凶化吉之星。正直无私。",
            "天梁具化解灾厄之能，必先逢险阻而后化解；喜得禄但自居清高，适合文教、医务、监察之职。"
        )
        "七杀" -> Pair(
            "七杀星属庚金，南斗第六星，上将星，化气为权。刚毅果敢、独断专行、雷厉风行、富开拓精神。",
            "七杀主成败孤克，宜历练军警、工程创业；与紫微同度为化杀为权，逢空劫易历经大起大落。"
        )
        "破军" -> Pair(
            "破军星属癸水，北斗第七星，化气为耗，破旧立新、开路先锋、冲劲十足、不拘小节。",
            "破军主改革变迁，喜冒险创新；遇煞星多波折，化禄化权主推陈出新、开创新格局。"
        )
        "左辅", "右弼" -> Pair(
            "六吉星之一，化气为善与助力。左辅主行事端正、贵人扶助；右弼主多才多智、灵活圆融。",
            "辅弼同宫或分守三方，能增强主星威势，化解凶煞，一生多得朋辈同事之鼎力相助。"
        )
        "文昌", "文曲" -> Pair(
            "六吉星之一，司科甲功名。文昌主正统文采、考学典章；文曲主口才舌辩、术数曲艺。",
            "昌曲入命主人聪明秀拔、博闻强识；喜与阳梁同度，忌逢化忌引发文书契约失误。"
        )
        "天魁", "天钺" -> Pair(
            "天乙贵人星。天魁为阳贵人，主明面提携、男子长辈襄助；天钺为阴贵人，主暗中扶持、女性异性贵助。",
            "魁钺在命身或三方交会，一生多逢机遇与伯乐赏识，遇难呈祥。"
        )
        "禄存" -> Pair(
            "北斗真禄星，属己土。司财富、寿元与解厄。主天禄稳健、衣食无忧。",
            "禄存所在宫位前有擎羊、后有陀罗相夹，故主人行事小心谨慎、甚至稍显自私防备。"
        )
        "天马" -> Pair(
            "中天驿马星，主奔波、出外、迁动、升迁。常居寅申巳亥四马之地。",
            "天马喜与禄存同度或会合构成【禄马交驰格】，主发财远乡、越动越富；逢煞则奔波劳碌。"
        )
        "擎羊", "陀罗" -> Pair(
            "六煞星之一。擎羊为阳金刑杖，主正面冲撞、血光刀伤、刚毅果决；陀罗为阴金暗箭，主拖延缠绕、暗中是非。",
            "羊陀入命性格坚韧顽强、好胜心切；煞星亦有开拓之勇，入辰戌丑未四墓之地称为吉化。"
        )
        "火星", "铃星" -> Pair(
            "六煞星之一。火星属阳火，性急如火、暴烈刚直；铃星属阴火，深沉内敛、记仇心重。",
            "火铃喜与贪狼同度构成爆发格局；单独落陷则主性格急躁、人生多起伏坎坷。"
        )
        "地空", "地劫" -> Pair(
            "六煞星之一，主空灵玄妙、精神世界超脱、物质聚散无常。",
            "空劫入命利于玄学、艺术、哲学思考与前沿创新；于世俗财富则主横发横破、过手成空。"
        )
        "红鸾", "天喜" -> Pair(
            "桃花吉曜。红鸾主婚姻嫁娶、人缘和美、喜气洋洋；天喜主生育添丁、心情畅快。",
            "入命宫或夫妻宫容貌秀美、惹人喜爱、早沐情缘。"
        )
        else -> Pair(
            "此星为传统紫微斗数星曜，主命理细节与时空微观吉凶映照。",
            "辅弼主吉，煞星主变，星曜配合命主格局参详方为精微。"
        )
    }
}
