package com.xue2026.floatingx.model

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Canvas 2D 模型渲染器
 *
 * 将 [BuiltInModel.BuiltInModelData] 渲染到 Android Canvas 上，
 * 支持内置角色的 idle 动画（呼吸、摇摆、弹跳等）。
 *
 * 渲染流程:
 *   1. 按 zOrder 排序部件，从低到高绘制
 *   2. 每个部件根据 animTimeSec 计算动画偏移
 *   3. 整体支持 scale 缩放
 */
class ModelRenderer {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val tempRect = RectF()

    /**
     * 在 Canvas 上绘制内置模型
     * @param canvas 目标 Canvas
     * @param model 内置模型数据
     * @param cx 绘制中心 X
     * @param cy 绘制中心 Y
     * @param scale 缩放倍率
     * @param animTimeSec 动画时间（秒）
     */
    fun drawBuiltIn(
        canvas: Canvas,
        model: BuiltInModel.BuiltInModelData,
        cx: Float, cy: Float,
        scale: Float,
        animTimeSec: Float
    ) {
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(scale, scale)

        for (part in model.bodyParts) {
            val (dx, dy) = calcAnimOffset(part, animTimeSec)
            val drawX = part.localX + dx
            val drawY = part.localY + dy

            fillPaint.color = part.color
            fillPaint.alpha = (part.alpha * 255).toInt().coerceIn(0, 255)
            strokePaint.color = part.strokeColor
            strokePaint.strokeWidth = part.strokeWidth

            canvas.save()
            canvas.rotate(part.rotationDeg, drawX, drawY)

            when (part.shape) {
                BuiltInModel.Shape.CIRCLE -> drawCircle(canvas, drawX, drawY, part, animTimeSec)
                BuiltInModel.Shape.RECT -> drawRect(canvas, drawX, drawY, part, animTimeSec)
                BuiltInModel.Shape.ELLIPSE -> drawEllipse(canvas, drawX, drawY, part)
                BuiltInModel.Shape.TRIANGLE -> drawTriangle(canvas, drawX, drawY, part)
                BuiltInModel.Shape.ARC -> drawArc(canvas, drawX, drawY, part)
                BuiltInModel.Shape.LINE -> drawLine(canvas, drawX, drawY, part)
            }

            canvas.restore()
        }

        canvas.restore()
    }

    // ── 动画计算 ──────────────────────────────────────────────────────────

    private fun calcAnimOffset(part: BuiltInModel.BodyPart, t: Float): Pair<Float, Float> {
        val speed = part.animSpeed * 2f
        return when (part.animType) {
            BuiltInModel.AnimType.NONE -> 0f to 0f
            BuiltInModel.AnimType.BOUNCE -> {
                val v = sin(t * speed * PI.toFloat()) * part.animAmplitude
                0f to (v * 0.5f)
            }
            BuiltInModel.AnimType.SWAY -> {
                val v = sin(t * speed * PI.toFloat()) * part.animAmplitude
                (v * 0.7f) to 0f
            }
            BuiltInModel.AnimType.BREATH -> 0f to 0f
            BuiltInModel.AnimType.FLOAT_UP -> {
                0f to (-part.animAmplitude * (1f + sin(t * speed * PI.toFloat())) * 0.3f)
            }
            BuiltInModel.AnimType.SPIN -> 0f to 0f
            BuiltInModel.AnimType.HEAD_TILT -> {
                val v = sin(t * speed * PI.toFloat()) * part.animAmplitude * 0.5f
                v to 0f
            }
        }
    }

    // ── 形状绘制 ──────────────────────────────────────────────────────────

    private fun drawCircle(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart, t: Float) {
        val r = part.width / 2f
        val bs = breathScale(part, t)
        canvas.save()
        canvas.scale(bs, bs, x, y)
        canvas.drawCircle(x, y, r, fillPaint)
        if (part.strokeWidth > 0 && part.strokeColor != 0) {
            canvas.drawCircle(x, y, r, strokePaint)
        }
        canvas.restore()
    }

    private fun drawRect(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart, t: Float) {
        val bs = breathScale(part, t)
        val hw = part.width / 2f * bs
        val hh = part.height / 2f * bs
        tempRect.set(x - hw, y - hh, x + hw, y + hh)
        canvas.drawRoundRect(tempRect, part.cornerRadius, part.cornerRadius, fillPaint)
        if (part.strokeWidth > 0 && part.strokeColor != 0) {
            canvas.drawRoundRect(tempRect, part.cornerRadius, part.cornerRadius, strokePaint)
        }
    }

    private fun drawEllipse(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart) {
        tempRect.set(x - part.width / 2f, y - part.height / 2f, x + part.width / 2f, y + part.height / 2f)
        canvas.drawOval(tempRect, fillPaint)
        if (part.strokeWidth > 0 && part.strokeColor != 0) {
            canvas.drawOval(tempRect, strokePaint)
        }
    }

    private fun drawTriangle(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart) {
        val hw = part.width / 2f
        val hh = part.height / 2f
        val path = Path().apply {
            moveTo(x, y - hh)
            lineTo(x - hw, y + hh)
            lineTo(x + hw, y + hh)
            close()
        }
        canvas.drawPath(path, fillPaint)
        if (part.strokeWidth > 0 && part.strokeColor != 0) {
            canvas.drawPath(path, strokePaint)
        }
    }

    private fun drawArc(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart) {
        tempRect.set(x - part.width / 2f, y - part.height / 2f, x + part.width / 2f, y + part.height / 2f)
        canvas.drawArc(tempRect, 0f, 180f, false, fillPaint)
        if (part.strokeWidth > 0 && part.strokeColor != 0) {
            canvas.drawArc(tempRect, 0f, 180f, false, strokePaint)
        }
    }

    private fun drawLine(canvas: Canvas, x: Float, y: Float, part: BuiltInModel.BodyPart) {
        val hh = part.height / 2f
        canvas.drawLine(x, y - hh, x, y + hh, fillPaint)
    }

    private fun breathScale(part: BuiltInModel.BodyPart, t: Float): Float {
        return if (part.animType == BuiltInModel.AnimType.BREATH) {
            1f + sin(t * part.animSpeed * 2f * PI.toFloat()) * (part.animAmplitude * 0.005f)
        } else {
            1f
        }
    }

    /**
     * 绘制纳西妲风格测试角色的便捷方法
     * @param canvas 目标 Canvas
     * @param cx 绘制中心 X
     * @param cy 绘制中心 Y
     * @param size 期望的绘制尺寸（宽高中较大者）
     * @param animTimeSec 动画时间（秒）
     */
    fun drawNahida(canvas: Canvas, cx: Float, cy: Float, size: Float, animTimeSec: Float) {
        val model = BuiltInModel.generateNahidaTestModel()
        val s = size / maxOf(model.width, model.height)
        drawBuiltIn(canvas, model, cx, cy, s, animTimeSec)
    }
}