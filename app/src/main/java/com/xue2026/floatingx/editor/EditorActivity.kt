package com.xue2026.floatingx.editor

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivityEditorBinding

/**
 * 编辑器 Activity (横屏)
 *
 * 提供模型/动作导入和可视编辑功能。
 * 布局分为三部分:
 * - 顶部工具栏 (退出 + 标题)
 * - 3D 预览区域 (FrameLayout 容器)
 * - 底部操作栏 (模型/动作/撤销/重做/保存)
 *
 * 快捷键:
 * - Ctrl+S: 保存
 * - Ctrl+Z: 撤销
 * - Ctrl+Y: 重做
 * - Ctrl+O: 导入模型
 * - Escape: 退出编辑器
 *
 * TODO: 接入 GLSurfaceView 进行 3D 渲染
 * TODO: 实现积木式 VMD 动作编辑面板
 */
class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupKeyboardShortcuts()
    }

    /** 绑定工具栏按钮事件 */
    private fun setupViews() {
        binding.btnImportModel.setOnClickListener {
            // TODO: 调用 SAF 文件选择器选择 PMX/FBX/Spine 文件
            Toast.makeText(this, R.string.editor_import_model, Toast.LENGTH_SHORT).show()
        }

        binding.btnImportMotion.setOnClickListener {
            // TODO: 选择 VMD 动作文件并加载到时间轴
            Toast.makeText(this, R.string.editor_import_motion, Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener {
            // TODO: 序列化编辑状态 -> Room 数据库
            Toast.makeText(this, R.string.editor_save, Toast.LENGTH_SHORT).show()
        }

        binding.btnUndo.setOnClickListener {
            // TODO: 撤销上一次操作
            Toast.makeText(this, R.string.editor_undo, Toast.LENGTH_SHORT).show()
        }

        binding.btnRedo.setOnClickListener {
            // TODO: 重做被撤销的操作
            Toast.makeText(this, R.string.editor_redo, Toast.LENGTH_SHORT).show()
        }

        binding.btnExit.setOnClickListener {
            finish()
        }
    }

    /** 初始化键盘快捷键映射 */
    private fun setupKeyboardShortcuts() {
        // onKeyDown 处理
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_S -> {
                if (event?.isCtrlPressed == true) { binding.btnSave.performClick(); true }
                else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_Z -> {
                if (event?.isCtrlPressed == true) { binding.btnUndo.performClick(); true }
                else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_Y -> {
                if (event?.isCtrlPressed == true) { binding.btnRedo.performClick(); true }
                else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_O -> {
                if (event?.isCtrlPressed == true) { binding.btnImportModel.performClick(); true }
                else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_ESCAPE -> { finish(); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}