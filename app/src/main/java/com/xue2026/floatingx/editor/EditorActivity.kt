package com.xue2026.floatingx.editor

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.databinding.ActivityEditorBinding

class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupKeyboardShortcuts()
    }

    private fun setupViews() {
        // 工具栏按钮
        binding.btnImportModel.setOnClickListener {
            Toast.makeText(this, "模型导入功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.btnImportMotion.setOnClickListener {
            Toast.makeText(this, "动作导入功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "保存功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.btnUndo.setOnClickListener {
            Toast.makeText(this, "撤销功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.btnRedo.setOnClickListener {
            Toast.makeText(this, "重做功能开发中", Toast.LENGTH_SHORT).show()
        }

        // 退出按钮
        binding.btnExit.setOnClickListener {
            finish()
        }
    }

    private fun setupKeyboardShortcuts() {
        // 键盘快捷键通过 onKeyDown 实现
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_S -> {
                if (event?.isCtrlPressed == true) {
                    Toast.makeText(this, "保存 (Ctrl+S)", Toast.LENGTH_SHORT).show()
                    true
                } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_Z -> {
                if (event?.isCtrlPressed == true) {
                    Toast.makeText(this, "撤销 (Ctrl+Z)", Toast.LENGTH_SHORT).show()
                    true
                } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_Y -> {
                if (event?.isCtrlPressed == true) {
                    Toast.makeText(this, "重做 (Ctrl+Y)", Toast.LENGTH_SHORT).show()
                    true
                } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_O -> {
                if (event?.isCtrlPressed == true) {
                    binding.btnImportModel.performClick()
                    true
                } else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}