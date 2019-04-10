package com.loopmoth.loopaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.MaskFilter
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.loopmoth.loopaint.FingerPath
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.rey.material.widget.Slider

import java.util.ArrayList


class PaintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var mPath: Path? = null
    private val mPaint: Paint
    private val paths = ArrayList<FingerPath>()
    private val removedPaths = ArrayList<FingerPath>()
    var currentColor: Int = 0
    var bgColor = DEFAULT_BG_COLOR
    var strokeWidth: Int = 0
    private var emboss: Boolean = false
    private var blur: Boolean = false
    private val mEmboss: MaskFilter
    private val mBlur: MaskFilter
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var menu: FloatingActionMenu? = null
    private var slider: Slider? = null

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = DEFAULT_COLOR
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.xfermode = null
        mPaint.alpha = 0xff

        mEmboss = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), 0.4f, 6f, 3.5f)
        mBlur = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
    }

    fun init(metrics: DisplayMetrics) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        currentColor = DEFAULT_COLOR
        strokeWidth = BRUSH_SIZE
    }

    fun normal() {
        emboss = false
        blur = false
    }

    fun emboss() {
        emboss = true
        blur = false
    }

    fun blur() {
        emboss = false
        blur = true
    }

    fun clear() {
        bgColor = DEFAULT_BG_COLOR
        paths.clear()
        normal()
        invalidate()
    }

    fun removeLastStroke(){
        if(paths.count()>0){
            removedPaths.add(paths[paths.lastIndex])
            paths.removeAt(paths.lastIndex)
        }
        normal()
        invalidate()
    }

    fun reviveLastStroke(){
        if(removedPaths.count()>0){
            paths.add(removedPaths[removedPaths.lastIndex])
            removedPaths.removeAt(removedPaths.lastIndex)
        }
        normal()
        invalidate()
    }

    fun getBitmap(): Bitmap {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        val bmp = Bitmap.createBitmap(this.drawingCache)
        this.isDrawingCacheEnabled = false


        return bmp
    }

    fun changeBackground(){
        mCanvas!!.save()
        mCanvas!!.drawColor(bgColor)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(bgColor)

        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.strokeWidth.toFloat()
            mPaint.maskFilter = null

            if (fp.emboss)
                mPaint.maskFilter = mEmboss
            else if (fp.blur)
                mPaint.maskFilter = mBlur

            mCanvas!!.drawPath(fp.path, mPaint)

        }

        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    fun hideAllMEnus(menu: FloatingActionMenu, slider: Slider){
        menu.close(true)
        slider.visibility = View.INVISIBLE
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = FingerPath(currentColor, emboss, blur, strokeWidth, mPath!!)
        paths.add(fp)

        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }

        performClick()

        return true
    }

    companion object {
        var BRUSH_SIZE = 20
        val DEFAULT_COLOR = Color.RED
        val DEFAULT_BG_COLOR = Color.WHITE
        private val TOUCH_TOLERANCE = 4f
    }
}