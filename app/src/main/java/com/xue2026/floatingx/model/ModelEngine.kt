package com.xue2026.floatingx.model

import android.content.Context

/**
 * 模型引擎 - 负责加载和管理 3D/2D 模型
 *
 * 支持的格式:
 * - PMX (MikuMikuDance 模型)
 * - FBX (通用3D格式)
 * - VMD (MikuMikuDance 动作)
 * - Spine (2D 骨骼动画)
 * - BUILTIN (内建程序化测试模型)
 */
class ModelEngine(private val context: Context) {

    private val loadedModels = mutableMapOf<String, ModelData>()
    private val pmxParser = PmxParser()

    /** 引擎内部模型数据 */
    data class ModelData(
        val id: String,
        val name: String,
        val type: ModelType,
        val path: String,
        /** PMX 解析结果 (仅 PMX 类型) */
        val pmxModel: PmxParser.PmxModel? = null,
        /** 内置模型数据 (仅 BUILTIN 类型) */
        val builtInModel: BuiltInModel.BuiltInModelData? = null
    )

    enum class ModelType {
        PMX, FBX, SPINE, BUILTIN
    }

    // ── 加载接口 ──────────────────────────────────────────────────────────

    fun loadModel(path: String, type: ModelType): ModelData? {
        return when (type) {
            ModelType.PMX -> loadPmxModel(path)
            ModelType.FBX -> loadFbxModel(path)
            ModelType.SPINE -> loadSpineModel(path)
            ModelType.BUILTIN -> loadBuiltInModel()
        }
    }

    /**
     * 加载内置测试模型（纳西妲风格）
     * 无需外部文件，纯代码生成
     */
    fun loadBuiltInModel(): ModelData {
        val model = BuiltInModel.generateNahidaTestModel()
        val data = ModelData(
            id = "builtin_nahida",
            name = model.name,
            type = ModelType.BUILTIN,
            path = "builtin://nahida",
            builtInModel = model
        )
        loadedModels[data.id] = data
        return data
    }

    /** 加载 PMX 文件 */
    private fun loadPmxModel(path: String): ModelData? {
        return try {
            val file = java.io.File(path)
            if (!file.exists()) return null
            val bytes = file.readBytes()
            val pmx = pmxParser.parse(bytes) ?: return null
            val data = ModelData(
                id = file.name,
                name = pmx.name,
                type = ModelType.PMX,
                path = path,
                pmxModel = pmx
            )
            loadedModels[data.id] = data
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** 加载 PMX 文件（从 assets） */
    fun loadPmxFromAssets(assetPath: String): ModelData? {
        return try {
            val bytes = context.assets.open(assetPath).readBytes()
            val pmx = pmxParser.parse(bytes) ?: return null
            val data = ModelData(
                id = assetPath,
                name = pmx.name,
                type = ModelType.PMX,
                path = "asset://$assetPath",
                pmxModel = pmx
            )
            loadedModels[data.id] = data
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadFbxModel(path: String): ModelData? {
        // FBX 解析预留
        return null
    }

    private fun loadSpineModel(path: String): ModelData? {
        // Spine 解析预留
        return null
    }

    // ── 管理接口 ──────────────────────────────────────────────────────────

    fun getModel(id: String): ModelData? = loadedModels[id]

    fun getAllModels(): List<ModelData> = loadedModels.values.toList()

    fun unloadModel(id: String) {
        loadedModels.remove(id)
    }

    fun clearAll() {
        loadedModels.clear()
    }
}