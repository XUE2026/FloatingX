package com.xue2026.floatingx.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

/**
 * PMX 2.0/2.1 模型解析器
 *
 * PMX (Polygon Model eXtended) 是 MikuMikuDance(MMD) 使用的扩展模型格式。
 * 本解析器支持 PMX 2.0 和 2.1 规格。
 *
 * 参考:
 *   - https://gist.github.com/felixjones/f8a06bd48f9da9a4539f
 *   - MikuMikuFormats 开源项目
 *
 * 字节序: 小端序 (Little-Endian)
 */
class PmxParser {

    // ── 数据模型 ──────────────────────────────────────────────────────────

    /** PMX 模型 — 顶层容器 */
    data class PmxModel(
        val version: Float,              // 2.0 或 2.1
        val name: String,                // 模型名称（本地化）
        val nameEn: String,              // 模型名称（英文）
        val comment: String,             // 注释（本地化）
        val commentEn: String,           // 注释（英文）
        val vertices: List<Vertex>,      // 顶点列表
        val faces: List<Face>,           // 面列表（三角形）
        val textures: List<String>,      // 纹理路径列表
        val materials: List<Material>,   // 材质列表
        val bones: List<Bone>,           // 骨骼列表
        val morphs: List<Morph>,         // 表情/变形列表
        val displayFrames: List<DisplayFrame>, // 显示框架
        val rigidBodies: List<RigidBody>,      // 刚体列表
        val joints: List<Joint>          // 关节列表
    )

    /** 顶点 */
    data class Vertex(
        val position: FloatArray,        // [x, y, z] 模型空间坐标
        val normal: FloatArray,          // [nx, ny, nz] 法线
        val uv: FloatArray,              // [u, v] 纹理坐标
        val additionalUvs: List<FloatArray>, // 额外UV (PMX 2.1)
        val boneIndices: IntArray,       // 骨骼索引（最多4个）
        val boneWeights: FloatArray,     // 骨骼权重（最多4个，和为1）
        val edgeScale: Float             // 边缘缩放倍数
    )

    /** 三角形面 */
    data class Face(val indices: IntArray) // 3个顶点索引

    /** 材质 */
    data class Material(
        val name: String,                // 材质名称
        val nameEn: String,              // 材质名称（英文）
        val diffuse: FloatArray,         // [r, g, b, a] 漫反射色
        val specular: FloatArray,        // [r, g, b] 高光色
        val specularPower: Float,        // 高光强度
        val ambient: FloatArray,         // [r, g, b] 环境光色
        val flag: Int,                   // 位标志
        val edgeColor: FloatArray,       // [r, g, b, a] 边缘色
        val edgeSize: Float,             // 边缘尺寸
        val textureIndex: Int,           // 纹理索引 (-1=无)
        val sphereTextureIndex: Int,     // 球体纹理索引 (-1=无)
        val sphereMode: Int,             // 球体模式 (0=禁用,1=乘算,2=加算,3=子纹理)
        val toonFlag: Int,               // 卡通着色标志 (0=索引,1=纹理)
        val toonTextureIndex: Int,       // 卡通纹理索引/值
        val memo: String,                // 备注
        val faceCount: Int               // 面数（该材质使用）
    )

    /** 骨骼 */
    data class Bone(
        val name: String,
        val nameEn: String,
        val position: FloatArray,        // [x, y, z]
        val parentIndex: Int,            // 父骨骼索引 (-1=无)
        val layer: Int,                  // 变形层级
        val flag: Int,                   // 位标志
        val connectPosition: FloatArray, // 连接目标位置 (flag & 0x0001)
        val inheritParentIndex: Int,     // 继承元骨骼索引 (flag & 0x0100)
        val inheritRatio: Float,         // 继承比例
        val fixedAxis: FloatArray,       // 固定轴方向 (flag & 0x0200)
        val localVector: FloatArray,     // 局部向量 (flag & 0x0800)
        val externalParentIndex: Int,    // 外部父骨骼索引 (flag & 0x2000)
        val ikTargetIndex: Int,          // IK 目标骨骼索引 (flag & 0x0020)
        val ikLoop: Int,                 // IK 循环次数
        val ikAngleLimit: Float          // IK 角度限制 (rad)
    )

    /** 表情/变形 */
    data class Morph(
        val name: String,
        val nameEn: String,
        val type: Int,                   // 0=组,1=顶点,2=骨骼,3=UV,4=附加UV1,5=附加UV2,6=附加UV3,7=附加UV4,8=材质
        val offsets: List<Any>           // 变形偏移数据（类型相关）
    )

    /** 显示框架 */
    data class DisplayFrame(
        val name: String,
        val nameEn: String,
        val flag: Int,                   // 0=内部框架,1=外部框架
        val elements: List<DisplayElement>
    )

    data class DisplayElement(
        val type: Int,                   // 0=骨骼,1=表情
        val index: Int
    )

    /** 刚体 */
    data class RigidBody(
        val name: String,
        val boneIndex: Int,              // 关联骨骼 (-1=无)
        val group: Int,                  // 碰撞组 (0-15)
        val mask: Int,                   // 碰撞掩码
        val shape: Int,                  // 0=球体,1=盒子,2=胶囊
        val size: FloatArray,            // 尺寸 [w, h, d] 或 [r] 或 [r, h]
        val position: FloatArray,        // [x, y, z]
        val rotation: FloatArray,        // [rx, ry, rz] (rad)
        val mass: Float,                 // 质量
        val linearDamping: Float,        // 移动衰减
        val angularDamping: Float,       // 旋转衰减
        val restitution: Float,          // 反弹系数
        val friction: Float,             // 摩擦系数
        val physicsMode: Int             // 0=物理跟随,1=物理+骨骼,2=物理模拟
    )

    /** 关节 */
    data class Joint(
        val name: String,
        val rigidBodyA: Int,             // 刚体A索引
        val rigidBodyB: Int,             // 刚体B索引
        val position: FloatArray,        // [x, y, z]
        val rotation: FloatArray,        // [rx, ry, rz] (rad)
        val lowerLimit: FloatArray,      // [x, y, z] 移动下限
        val upperLimit: FloatArray,      // [x, y, z] 移动上限
        val angularLower: FloatArray,    // [x, y, z] 旋转下限
        val angularUpper: FloatArray,    // [x, y, z] 旋转上限
        val springConstant: FloatArray,  // [x, y, z] 弹簧常数
        val springRotation: FloatArray   // [x, y, z] 旋转弹簧常数
    )

    // ── 解析状态 ──────────────────────────────────────────────────────────

    private var header = ByteBuffer.allocate(0)
    private var vertexIndexSize = 1      // 顶点索引字节数
    private var textureIndexSize = 1     // 纹理索引字节数
    private var materialIndexSize = 1    // 材质索引字节数
    private var boneIndexSize = 1        // 骨骼索引字节数
    private var morphIndexSize = 1       // 变形索引字节数
    private var rigidIndexSize = 1       // 刚体索引字节数

    // ── 主入口 ────────────────────────────────────────────────────────────

    /**
     * 解析 PMX 二进制数据
     * @param data 完整的 PMX 文件字节数组
     * @return 解析成功返回 PmxModel，失败返回 null
     */
    fun parse(data: ByteArray): PmxModel? {
        return try {
            header = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            parseHeader()        // 读文件头
            val name = parseString()
            val nameEn = parseString()
            val comment = parseString()
            val commentEn = parseString()
            val vertices = parseVertices()
            val faces = parseFaces()
            val textures = parseTextures()
            val materials = parseMaterials()
            val bones = parseBones()
            val morphs = parseMorphs()
            val displayFrames = parseDisplayFrames()
            val rigidBodies = parseRigidBodies()
            val joints = parseJoints()

            PmxModel(
                version = header.getFloat(4), // 从header中已读取
                name = name, nameEn = nameEn,
                comment = comment, commentEn = commentEn,
                vertices = vertices, faces = faces,
                textures = textures, materials = materials,
                bones = bones, morphs = morphs,
                displayFrames = displayFrames,
                rigidBodies = rigidBodies, joints = joints
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ── 头部解析 ──────────────────────────────────────────────────────────

    /** 读取文件头并设置索引尺寸 */
    private fun parseHeader() {
        header.rewind()
        val sig = ByteArray(4)
        header.get(sig)
        if (String(sig) != "PMX ") throw IllegalArgumentException("Invalid PMX signature: ${String(sig)}")
        // version at bytes 4-7 (float)
        header.position(8)
        val encoding = header.get().toInt() and 0xFF  // 0=UTF-16LE, 1=UTF-8
        if (encoding == 0) {
            stringCharset = Charset.forName("UTF-16LE")
        } else {
            stringCharset = Charsets.UTF_8
        }
        val additionalUV = header.get().toInt() and 0xFF
        vertexIndexSize = header.get().toInt() and 0xFF
        textureIndexSize = header.get().toInt() and 0xFF
        materialIndexSize = header.get().toInt() and 0xFF
        boneIndexSize = header.get().toInt() and 0xFF
        morphIndexSize = header.get().toInt() and 0xFF
        rigidIndexSize = header.get().toInt() and 0xFF
    }

    private var stringCharset: Charset = Charsets.UTF_16LE

    /** 读取 PMX 编码字符串: int32 length + bytes */
    private fun parseString(): String {
        val len = header.getInt()
        if (len <= 0) return ""
        val bytes = ByteArray(len)
        header.get(bytes)
        return String(bytes, stringCharset)
    }

    /** 读取索引（通用） */
    private fun readIndex(size: Int): Int {
        return when (size) {
            1 -> header.get().toInt() and 0xFF
            2 -> header.getShort().toInt() and 0xFFFF
            4 -> header.getInt()
            else -> header.getInt()
        }
    }

    /** 读取 float 数组 */
    private fun readFloats(n: Int): FloatArray = FloatArray(n) { header.getFloat() }

    // ── 顶点解析 ──────────────────────────────────────────────────────────

    private fun parseVertices(): List<Vertex> {
        val count = header.getInt()
        val list = ArrayList<Vertex>(count)
        repeat(count) {
            val pos = readFloats(3)
            val normal = readFloats(3)
            val uv = readFloats(2)
            // 附加UV（PMX 2.1 可配置）
            val additionalUVCount = (header.get(10).toInt() and 0xFF)
            val additionalUvs = List(additionalUVCount) { readFloats(4) }
            // 骨骼信息: BDEF1 / BDEF2 / BDEF4 / SDEF / QDEF
            val boneType = header.get().toInt() and 0xFF
            val boneIndices: IntArray
            val boneWeights: FloatArray
            when (boneType) {
                0 -> { // BDEF1
                    boneIndices = intArrayOf(readIndex(boneIndexSize))
                    boneWeights = floatArrayOf(1f)
                }
                1 -> { // BDEF2
                    boneIndices = intArrayOf(readIndex(boneIndexSize), readIndex(boneIndexSize))
                    boneWeights = readFloats(2)
                }
                2 -> { // BDEF4
                    boneIndices = IntArray(4) { readIndex(boneIndexSize) }
                    boneWeights = readFloats(4)
                }
                3 -> { // SDEF
                    boneIndices = intArrayOf(readIndex(boneIndexSize), readIndex(boneIndexSize))
                    boneWeights = readFloats(2)
                    // SDEF 参数（C/R0/R1 向量），跳过
                    readFloats(3); readFloats(3); readFloats(3)
                }
                4 -> { // QDEF (PMX 2.1)
                    boneIndices = IntArray(4) { readIndex(boneIndexSize) }
                    boneWeights = readFloats(4)
                }
                else -> {
                    boneIndices = intArrayOf()
                    boneWeights = floatArrayOf()
                }
            }
            val edgeScale = header.getFloat()
            list.add(Vertex(pos, normal, uv, additionalUvs, boneIndices, boneWeights, edgeScale))
        }
        return list
    }

    // ── 面解析 ────────────────────────────────────────────────────────────

    private fun parseFaces(): List<Face> {
        val count = header.getInt() // 顶点索引总数（3的倍数）
        val triangleCount = count / 3
        val list = ArrayList<Face>(triangleCount)
        repeat(triangleCount) {
            val i0 = readIndex(vertexIndexSize)
            val i1 = readIndex(vertexIndexSize)
            val i2 = readIndex(vertexIndexSize)
            list.add(Face(intArrayOf(i0, i1, i2)))
        }
        return list
    }

    // ── 纹理解析 ──────────────────────────────────────────────────────────

    private fun parseTextures(): List<String> {
        val count = header.getInt()
        return List(count) { parseString() }
    }

    // ── 材质解析 ──────────────────────────────────────────────────────────

    private fun parseMaterials(): List<Material> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val nameEn = parseString()
            val diffuse = readFloats(4)
            val specular = readFloats(3)
            val specularPower = header.getFloat()
            val ambient = readFloats(3)
            val flag = header.get().toInt() and 0xFF
            val edgeColor = readFloats(4)
            val edgeSize = header.getFloat()
            val texIdx = readIndex(textureIndexSize)
            val sphereIdx = readIndex(textureIndexSize)
            val sphereMode = header.get().toInt() and 0xFF
            val toonFlag = header.get().toInt() and 0xFF
            val toonIdx = if (toonFlag == 0) readIndex(textureIndexSize) else header.get().toInt() and 0xFF
            val memo = parseString()
            val faceCount = header.getInt() // 面数（顶点索引数/3）
            Material(name, nameEn, diffuse, specular, specularPower, ambient,
                flag, edgeColor, edgeSize, texIdx, sphereIdx, sphereMode,
                toonFlag, toonIdx, memo, faceCount)
        }
    }

    // ── 骨骼解析 ──────────────────────────────────────────────────────────

    private fun parseBones(): List<Bone> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val nameEn = parseString()
            val pos = readFloats(3)
            val parentIdx = readIndex(boneIndexSize)
            val layer = header.getInt()
            val flag = header.get().toInt() and 0xFFFF
            // flag位决定后续字段
            val connPos = if (flag and 0x0001 != 0) readFloats(3) else floatArrayOf(readIndex(boneIndexSize).toFloat())
            val inheritParent = if (flag and 0x0100 != 0) readIndex(boneIndexSize) else -1
            val inheritRatio = if (flag and 0x0100 != 0) header.getFloat() else 0f
            val fixedAxis = if (flag and 0x0200 != 0) readFloats(3) else floatArrayOf()
            val localVec = if (flag and 0x0800 != 0) readFloats(3) else floatArrayOf()
            val extParent = if (flag and 0x2000 != 0) readIndex(boneIndexSize) else -1
            val ikTarget = if (flag and 0x0020 != 0) readIndex(boneIndexSize) else -1
            val ikLoop = if (flag and 0x0020 != 0) header.getInt() else 0
            val ikLimit = if (flag and 0x0020 != 0) header.getFloat() else 0f
            Bone(name, nameEn, pos, parentIdx, layer, flag, connPos, inheritParent, inheritRatio,
                fixedAxis, localVec, extParent, ikTarget, ikLoop, ikLimit)
        }
    }

    // ── 表情/变形解析 ──────────────────────────────────────────────────────

    private fun parseMorphs(): List<Morph> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val nameEn = parseString()
            val type = header.get().toInt() and 0xFF
            val offsetCount = header.getInt()
            val offsets = List<Any>(offsetCount) {
                when (type) {
                    0 -> { // 组变形
                        MorphGroupOffset(
                            readIndex(morphIndexSize),
                            header.getFloat()
                        )
                    }
                    1 -> { // 顶点变形
                        MorphVertexOffset(
                            readIndex(vertexIndexSize),
                            readFloats(3)
                        )
                    }
                    2 -> { // 骨骼变形
                        MorphBoneOffset(
                            readIndex(boneIndexSize),
                            readFloats(3), // 移动
                            readFloats(4)  // 旋转 (quat)
                        )
                    }
                    3, 4, 5, 6, 7 -> { // UV 变形
                        MorphUVOffset(
                            readIndex(vertexIndexSize),
                            readFloats(4)
                        )
                    }
                    8 -> { // 材质变形
                        MorphMaterialOffset(
                            readIndex(materialIndexSize),
                            header.get().toInt() and 0xFF, // op
                            readFloats(4), // diffuse
                            readFloats(3), // specular
                            header.getFloat(), // specular power
                            readFloats(3), // ambient
                            readFloats(4), // edge color
                            header.getFloat(), // edge size
                            readFloats(4), // texture factor
                            readFloats(4), // sphere factor
                            readFloats(4)  // toon factor
                        )
                    }
                    else -> emptyList<Any>()
                }
            }
            Morph(name, nameEn, type, offsets)
        }
    }

    data class MorphGroupOffset(val morphIndex: Int, val ratio: Float)
    data class MorphVertexOffset(val vertexIndex: Int, val offset: FloatArray)
    data class MorphBoneOffset(val boneIndex: Int, val translate: FloatArray, val rotate: FloatArray)
    data class MorphUVOffset(val vertexIndex: Int, val offset: FloatArray)
    data class MorphMaterialOffset(
        val materialIndex: Int, val op: Int,
        val diffuse: FloatArray, val specular: FloatArray, val specularPower: Float,
        val ambient: FloatArray, val edgeColor: FloatArray, val edgeSize: Float,
        val texFactor: FloatArray, val sphereFactor: FloatArray, val toonFactor: FloatArray
    )

    // ── 显示框架 ──────────────────────────────────────────────────────────

    private fun parseDisplayFrames(): List<DisplayFrame> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val nameEn = parseString()
            val flag = header.get().toInt() and 0xFF
            val elemCount = header.getInt()
            val elements = List(elemCount) {
                val elemType = header.get().toInt() and 0xFF
                val elemIdx = readIndex(if (elemType == 0) boneIndexSize else morphIndexSize)
                DisplayElement(elemType, elemIdx)
            }
            DisplayFrame(name, nameEn, flag, elements)
        }
    }

    // ── 刚体解析 ──────────────────────────────────────────────────────────

    private fun parseRigidBodies(): List<RigidBody> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val boneIdx = readIndex(boneIndexSize)
            val group = header.get().toInt() and 0xFF
            val mask = header.get().toInt() and 0xFF
            val shape = header.get().toInt() and 0xFF
            val size = readFloats(3)
            val pos = readFloats(3)
            val rot = readFloats(3)
            val mass = header.getFloat()
            val linearDamp = header.getFloat()
            val angularDamp = header.getFloat()
            val restitution = header.getFloat()
            val friction = header.getFloat()
            val physicsMode = header.get().toInt() and 0xFF
            RigidBody(name, boneIdx, group, mask, shape, size, pos, rot,
                mass, linearDamp, angularDamp, restitution, friction, physicsMode)
        }
    }

    // ── 关节解析 ──────────────────────────────────────────────────────────

    private fun parseJoints(): List<Joint> {
        val count = header.getInt()
        return List(count) {
            val name = parseString()
            val bodyA = readIndex(rigidIndexSize)
            val bodyB = readIndex(rigidIndexSize)
            val pos = readFloats(3)
            val rot = readFloats(3)
            val lower = readFloats(3)
            val upper = readFloats(3)
            val angLower = readFloats(3)
            val angUpper = readFloats(3)
            val spring = readFloats(3)
            val springRot = readFloats(3)
            Joint(name, bodyA, bodyB, pos, rot, lower, upper, angLower, angUpper, spring, springRot)
        }
    }
}