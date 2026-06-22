package com.xue2026.floatingx.model

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.xue2026.floatingx.model.BuiltInModel
import com.xue2026.floatingx.model.ModelRenderer

/**
 * 悬浮窗宠物渲染 View
 *
 * 在 Canvas 上绘制内置的"纳西妲风格"测试角色，
 * 包含 idle 动画（呼吸、摇摆等）。
 *
 * 工作流程:
 *   1. 通过 [ModelRenderer] 将内置模型数据绘制到 Canvas
 *   2. 使用 System.nanoTime() 驱动动画，持续刷新
 *   3. 通过 postInvalidateOnAnimation() 实现 vsync 刷新
 */
class FloatingPetView(context: Context) : View(context) {

    private val renderer = ModelRenderer()
    private var startTime = 0L

    init {
        // 禁止硬件加速（Canvas 2D 绘制在悬浮窗中更稳定）
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTime = System.nanoTime()
        // 开始动画循环
        postOnAnimation(frameCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 停止动画回调
        removeCallbacks(frameCallback)
    }

    private val frameCallback = object : Runnable {
        override fun run() {
            invalidate()
            postOnAnimation(this) // 持续请求下一帧
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // 计算动画时间（秒）
        val animTime = if (startTime > 0) {
            (System.nanoTime() - startTime) / 1_000_000_000f
        } else {
            0f
        }

        // 清除背景（透明）
        canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

        // 绘制测试角色 — 居中，大小适配 View
        val drawSize = minOf(w, h) * 0.85f
        renderer.drawNahida(canvas, w / 2f, h / 2f, drawSize, animTime)
    }
}