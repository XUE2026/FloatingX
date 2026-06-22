package com.xue2026.floatingx.model

import android.graphics.Color

/**
 * 内置测试模型生成器
 *
 * 当用户尚未导入外部 PMX 模型时，使用此生成器创建一个
 * 程序化生成的"纳西妲风格"可爱角色作为内置测试用。
 *
 * 角色特征: 绿色主题、白色长发、白色连衣裙、小草王冠
 * 渲染方式: Canvas 2D 绘制（各部分分层绘制）
 *
 * 注意: 这是一个纯代码生成的内置模型，不涉及任何第三方资源。
 */
object BuiltInModel {

    /** 内置模型加载结果 */
    data class BuiltInModelData(
        val name: String,
        val bodyParts: List<BodyPart>,
        val width: Float,    // 模型原始宽度
        val height: Float    // 模型原始高度
    )

    /** 身体部件 */
    data class BodyPart(
        val id: String,              // 部件标识
        val shape: Shape,            // 形状类型
        val color: Int,              // 填充色
        val strokeColor: Int,        // 描边色
        val strokeWidth: Float,      // 描边宽度
        val localX: Float,           // 局部坐标 X (相对于模型中心)
        val localY: Float,           // 局部坐标 Y
        val width: Float,            // 宽度
        val height: Float,           // 高度
        val cornerRadius: Float,     // 圆角半径 (仅 Rect)
        val rotationDeg: Float,      // 旋转角度 (度)
        val alpha: Float,            // 透明度 0-1
        val animType: AnimType,      // 动画类型
        val animAmplitude: Float,    // 动画幅度 (像素)
        val animSpeed: Float,        // 动画速度 (倍率)
        val zOrder: Int              // 绘制层级 (越大越靠前)
    )

    /** 形状 */
    enum class Shape { CIRCLE, RECT, TRIANGLE, ELLIPSE, ARC, LINE }

    /** 动画类型 */
    enum class AnimType {
        NONE,        // 静止
        BOUNCE,      // 上下弹跳
        SWAY,        // 左右摇摆
        BREATH,      // 呼吸缩放
        FLOAT_UP,    // 向上漂浮
        SPIN,        // 旋转
        HEAD_TILT    // 头部倾斜
    }

    /** 部件颜色工具 */
    object Colors {
        // 纳西妲主题色
        val SKIN = Color.parseColor("#FFF5E6")           // 肤色
        val DRESS_GREEN = Color.parseColor("#FF7BC67E")   // 裙摆绿
        val DRESS_WHITE = Color.parseColor("#FFF8F8FF")   // 裙摆白
        val HAIR_WHITE = Color.parseColor("#FFFFF8F0")    // 白发
        val HAIR_LIGHT = Color.parseColor("#FFFFE0C0")    // 发丝浅色
        val EYE_GREEN = Color.parseColor("#FF4CAF50")     // 绿瞳
        val EYE_HIGHLIGHT = Color.parseColor("#FFFFFFFF") // 眼神光
        val CROWN_GOLD = Color.parseColor("#FFFFD700")    // 金冠
        val CROWN_LEAF = Color.parseColor("#FF2E7D32")    // 叶冠
        val SHOE_BROWN = Color.parseColor("#FF8B4513")    // 鞋子
        val MOUTH = Color.parseColor("#FFFF8A80")         // 嘴
        val BLUSH = Color.parseColor("#FFFFCDD2")         // 腮红
        val ACCENT = Color.parseColor("#FF6200EE")        // 紫色点缀
    }

    /**
     * 生成内置测试模型
     * 返回一个可爱的 chibi 风格角色（纳西妲风格）
     */
    fun generateNahidaTestModel(): BuiltInModelData {
        val parts = mutableListOf<BodyPart>()

        // ── 身体（连衣裙 - 底层）─
        // 裙子后层（白色大裙摆）
        parts += BodyPart(
            "skirt_back", Shape.RECT, Colors.DRESS_WHITE, Colors.DRESS_GREEN, 1.5f,
            0f, 30f, 60f, 70f, 12f, 0f, 1f, AnimType.BREATH, 2f, 0.8f, 0
        )

        // 裙摆装饰层（绿色渐变条）
        parts += BodyPart(
            "skirt_green", Shape.RECT, Colors.DRESS_GREEN, Color.TRANSPARENT, 0f,
            0f, 55f, 50f, 40f, 8f, 0f, 0.85f, AnimType.BREATH, 1.5f, 0.9f, 1
        )

        // 身体/上衣（白色小上衣）
        parts += BodyPart(
            "body", Shape.RECT, Colors.DRESS_WHITE, Colors.DRESS_GREEN, 1f,
            0f, 10f, 36f, 40f, 8f, 0f, 1f, AnimType.BREATH, 1f, 1f, 2
        )

        // ── 头部 ─
        // 头部底色
        parts += BodyPart(
            "head", Shape.CIRCLE, Colors.SKIN, Color.argb(80, 0, 0, 0), 1f,
            0f, -35f, 40f, 40f, 0f, 0f, 1f, AnimType.HEAD_TILT, 2f, 0.6f, 10
        )

        // ── 头发 ─
        // 后发（白色长发）
        parts += BodyPart(
            "hair_back", Shape.RECT, Colors.HAIR_WHITE, Colors.HAIR_LIGHT, 1f,
            0f, -20f, 44f, 60f, 10f, 0f, 0.95f, AnimType.FLOAT_UP, 3f, 0.7f, 3
        )

        // 左侧长发
        parts += BodyPart(
            "hair_left", Shape.RECT, Colors.HAIR_WHITE, Colors.HAIR_LIGHT, 1f,
            -22f, -10f, 16f, 55f, 6f, 15f, 0.9f, AnimType.SWAY, 4f, 0.5f, 4
        )

        // 右侧长发
        parts += BodyPart(
            "hair_right", Shape.RECT, Colors.HAIR_WHITE, Colors.HAIR_LIGHT, 1f,
            22f, -10f, 16f, 55f, 6f, -15f, 0.9f, AnimType.SWAY, 4f, 0.5f, 4
        )

        // 刘海
        parts += BodyPart(
            "hair_bangs", Shape.RECT, Colors.HAIR_WHITE, Colors.HAIR_LIGHT, 1f,
            0f, -50f, 38f, 18f, 8f, 0f, 0.95f, AnimType.NONE, 0f, 1f, 11
        )

        // ── 草冠（纳西妲标志性小草王冠）─
        // 左叶片
        parts += BodyPart(
            "leaf_left", Shape.TRIANGLE, Colors.CROWN_LEAF, Color.argb(100, 0, 60, 0), 1f,
            -12f, -62f, 14f, 16f, 0f, -20f, 0.95f, AnimType.SWAY, 3f, 0.8f, 12
        )
        // 中叶片（最高）
        parts += BodyPart(
            "leaf_mid", Shape.TRIANGLE, Colors.CROWN_LEAF, Color.argb(100, 0, 60, 0), 1f,
            0f, -67f, 14f, 20f, 0f, 0f, 0.95f, AnimType.SWAY, 2f, 0.9f, 13
        )
        // 右叶片
        parts += BodyPart(
            "leaf_right", Shape.TRIANGLE, Colors.CROWN_LEAF, Color.argb(100, 0, 60, 0), 1f,
            12f, -62f, 14f, 16f, 0f, 20f, 0.95f, AnimType.SWAY, 3f, 0.8f, 12
        )

        // 金冠环
        parts += BodyPart(
            "crown_ring", Shape.ELLIPSE, Colors.CROWN_GOLD, Color.argb(80, 180, 150, 0), 1.5f,
            0f, -53f, 30f, 6f, 0f, 0f, 0.9f, AnimType.NONE, 0f, 1f, 12
        )

        // ── 眼睛 ─
        // 左眼白
        parts += BodyPart(
            "eye_left_white", Shape.ELLIPSE, Color.WHITE, Color.argb(50, 0, 0, 0), 0.5f,
            -10f, -38f, 12f, 14f, 0f, 0f, 1f, AnimType.NONE, 0f, 1f, 14
        )
        // 右眼白
        parts += BodyPart(
            "eye_right_white", Shape.ELLIPSE, Color.WHITE, Color.argb(50, 0, 0, 0), 0.5f,
            10f, -38f, 12f, 14f, 0f, 0f, 1f, AnimType.NONE, 0f, 1f, 14
        )
        // 左瞳（绿色）
        parts += BodyPart(
            "eye_left_pupil", Shape.CIRCLE, Colors.EYE_GREEN, Color.BLACK, 0.5f,
            -10f, -37f, 6f, 6f, 0f, 0f, 1f, AnimType.NONE, 0f, 1f, 15
        )
        // 右瞳（绿色）
        parts += BodyPart(
            "eye_right_pupil", Shape.CIRCLE, Colors.EYE_GREEN, Color.BLACK, 0.5f,
            10f, -37f, 6f, 6f, 0f, 0f, 1f, AnimType.NONE, 0f, 1f, 15
        )
        // 左眼神光
        parts += BodyPart(
            "eye_left_highlight", Shape.CIRCLE, Colors.EYE_HIGHLIGHT, Color.TRANSPARENT, 0f,
            -8f, -39f, 2.5f, 2.5f, 0f, 0f, 0.9f, AnimType.NONE, 0f, 1f, 16
        )
        // 右眼神光
        parts += BodyPart(
            "eye_right_highlight", Shape.CIRCLE, Colors.EYE_HIGHLIGHT, Color.TRANSPARENT, 0f,
            12f, -39f, 2.5f, 2.5f, 0f, 0f, 0.9f, AnimType.NONE, 0f, 1f, 16
        )

        // ── 腮红 ─
        parts += BodyPart(
            "blush_left", Shape.ELLIPSE, Colors.BLUSH, Color.TRANSPARENT, 0f,
            -16f, -30f, 10f, 6f, 0f, 0f, 0.5f, AnimType.NONE, 0f, 1f, 13
        )
        parts += BodyPart(
            "blush_right", Shape.ELLIPSE, Colors.BLUSH, Color.TRANSPARENT, 0f,
            16f, -30f, 10f, 6f, 0f, 0f, 0.5f, AnimType.NONE, 0f, 1f, 13
        )

        // ── 嘴 ─
        parts += BodyPart(
            "mouth", Shape.ARC, Colors.MOUTH, Color.argb(80, 200, 50, 50), 1.5f,
            0f, -26f, 8f, 5f, 0f, 0f, 0.8f, AnimType.NONE, 0f, 1f, 15
        )

        // ── 手臂 ─
        // 左臂
        parts += BodyPart(
            "arm_left", Shape.RECT, Colors.SKIN, Color.argb(50, 0, 0, 0), 0.5f,
            -30f, 5f, 10f, 32f, 4f, 15f, 0.95f, AnimType.SWAY, 6f, 0.7f, 5
        )
        // 右臂
        parts += BodyPart(
            "arm_right", Shape.RECT, Colors.SKIN, Color.argb(50, 0, 0, 0), 0.5f,
            30f, 5f, 10f, 32f, 4f, -15f, 0.95f, AnimType.SWAY, 6f, 0.7f, 5
        )

        // ── 腿 ─
        parts += BodyPart(
            "leg_left", Shape.RECT, Colors.SKIN, Color.argb(50, 0, 0, 0), 0.5f,
            -10f, 70f, 12f, 25f, 4f, 5f, 0.95f, AnimType.SWAY, 3f, 0.6f, 2
        )
        parts += BodyPart(
            "leg_right", Shape.RECT, Colors.SKIN, Color.argb(50, 0, 0, 0), 0.5f,
            10f, 70f, 12f, 25f, 4f, -5f, 0.95f, AnimType.SWAY, 3f, 0.6f, 2
        )

        // ── 鞋子 ─
        parts += BodyPart(
            "shoe_left", Shape.ELLIPSE, Colors.SHOE_BROWN, Color.argb(80, 0, 0, 0), 0.5f,
            -10f, 88f, 16f, 10f, 0f, 0f, 0.95f, AnimType.BOUNCE, 2f, 0.8f, 3
        )
        parts += BodyPart(
            "shoe_right", Shape.ELLIPSE, Colors.SHOE_BROWN, Color.argb(80, 0, 0, 0), 0.5f,
            10f, 88f, 16f, 10f, 0f, 0f, 0.95f, AnimType.BOUNCE, 2f, 0.8f, 3
        )

        return BuiltInModelData(
            name = "内置测试角色 (Nahida-Style)",
            bodyParts = parts.sortedBy { it.zOrder },
            width = 80f,
            height = 120f
        )
    }
}