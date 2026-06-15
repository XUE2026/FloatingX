package com.xue2026.floatingx.model

class ModelEngine {
    private val loadedModels = mutableMapOf<String, ModelData>()
    data class ModelData(val id: String, val name: String, val type: ModelType, val path: String)
    enum class ModelType { PMX, FBX, SPINE }

    fun loadModel(path: String, type: ModelType): ModelData? = when (type) {
        ModelType.PMX -> loadPmxModel(path); ModelType.FBX -> loadFbxModel(path); ModelType.SPINE -> loadSpineModel(path)
    }
    private fun loadPmxModel(path: String): ModelData? = null // TODO: PmxParser
    private fun loadFbxModel(path: String): ModelData? = null  // TODO: FbxParser
    private fun loadSpineModel(path: String): ModelData? = null // TODO: SpineRenderer
    fun getModel(id: String): ModelData? = loadedModels[id]
    fun unloadModel(id: String) { loadedModels.remove(id) }
    fun clearAll() { loadedModels.clear() }
}