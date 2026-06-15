package com.xue2026.floatingx.model

/**
 * VMD (Vocaloid Motion Data) 解析器
 *
 * VMD 是 MikuMikuDance 的标准动作文件格式，存储骨骼动画数据。
 * 包含三种关键帧类型:
 *   1. 骨骼帧 (BoneFrame)    — 位置 + 四元数旋转 + 贝塞尔插值
 *   2. 表情帧 (MorphFrame)   — 权重值
 *   3. 摄像机帧 (CameraFrame) — 位置 + 旋转 + 视角 + 视野
 *
 * 插值曲线: 每个骨骼帧有 4 条贝塞尔曲线 (x_trans, y_trans, z_trans, rotation),
 * 每条曲线 4 个控制点, 共 64 bytes (4*4*4)
 *
 * TODO: 实现 VMD 二进制文件解析
 * VMD 规范: http://mikumikudance.wikia.com/wiki/VMD_file_format
 */
class VmdParser {

    /** 完整的 VMD 动作数据 */
    data class VmdMotion(
        val header: String,       // "Vocaloid Motion Data 0002"
        val modelName: String,    // 目标模型名 (30 bytes, Shift-JIS)
        val boneFrames: List<BoneFrame>,
        val morphFrames: List<MorphFrame>,
        val cameraFrames: List<CameraFrame>
    )

    /** 骨骼关键帧 */
    data class BoneFrame(
        val boneName: String,         // 骨骼名 (Shift-JIS, 15 bytes -> 扩展)
        val frameIndex: Int,          // 帧号 (30fps 基准)
        val position: FloatArray,     // 位置 [x, y, z]
        val rotation: FloatArray,     // 旋转四元数 [x, y, z, w]
        val interpolation: ByteArray  // 贝塞尔插值曲线 (64 bytes)
    )

    /** 表情关键帧 */
    data class MorphFrame(
        val morphName: String,
        val frameIndex: Int,
        val weight: Float  // 0.0 ~ 1.0
    )

    /** 摄像机关键帧 */
    data class CameraFrame(
        val frameIndex: Int,
        val distance: Float,        // 到注视点的距离
        val position: FloatArray,   // 注视点位置
        val rotation: FloatArray,   // 欧拉角 [x, y, z]
        val interpolation: ByteArray, // 插值曲线
        val angle: Int,             // 视野角度 (0~180)
        val perspective: Boolean    // true=透视, false=正交
    )

    /**
     * 解析 VMD 字节数据
     * @param data 完整 VMD 文件字节
     * @return VmdMotion 对象
     */
    fun parse(data: ByteArray): VmdMotion? {
        // TODO: 实现 VMD 二进制格式解析
        return null
    }

    /**
     * 将 VMD 动作转换为通用的 AnimationClip
     * AnimationClip 可供积木编辑器使用
     */
    fun toAnimationClip(motion: VmdMotion): AnimationClip {
        return AnimationClip(
            name = motion.modelName,
            boneFrames = motion.boneFrames,
            morphFrames = motion.morphFrames,
            duration = motion.boneFrames.maxOfOrNull { it.frameIndex } ?: 0
        )
    }
}

/**
 * 通用动画剪辑 — 编辑器时间轴的核心数据模型
 *
 * 包含骨骼帧和表情帧, duration 表示总帧数。
 * 积木式编辑器可将这些帧拆分为可拖拽/编辑的块。
 */
data class AnimationClip(
    val name: String,
    val boneFrames: List<VmdParser.BoneFrame>,
    val morphFrames: List<VmdParser.MorphFrame>,
    val duration: Int  // 总帧数 (30fps)
)