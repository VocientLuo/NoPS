package com.xluo.pen.core

import android.graphics.*
import android.view.MotionEvent
import com.xluo.core.constants.Constants
import com.xluo.pen.bean.ControllerPoint
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sin

abstract class BasePen : PenObject {

    val id: Int

    var name: String = ""

    var bitmap: Bitmap? = null

    private val random = Random()

    var lastPoint: ControllerPoint? = null

    constructor(paintId: Int): super() {
        id = paintId
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG) //创建抗锯齿画笔
        mPaint.isAntiAlias = true // 锯齿不优化
        mPaint.strokeJoin = Paint.Join.ROUND //接洽处为圆弧
        mPaint.strokeCap = Paint.Cap.ROUND //画笔样式圆形
    }

    /**
     * 笔尖形状开关
     */
    var penTopEnabled = false

    // stroke 绘制图形的边， fill绘制内容， fill_and_stroke绘制内容和边
    var style: Paint.Style = Paint.Style.FILL
    var styleEnabled = true

    var mPath: Path = Path()

    /**
     * 抗锯齿，默认true
     */
    var isAntiAlias = true

    /**
     * 笔冒样式
     */
    var strokeJoin = Paint.Join.ROUND

    /**
     * xfomode
     */
    var penMixMode: PenMixMode? = null

    /**
     * 画笔样式
     */
    var strokeCap = Paint.Cap.ROUND

    /**
     * 画笔
     */
    var mPaint: Paint = Paint()

    /**
     * 尺寸
     */
    var size: Int = Constants.DEFAULT_PEN_SIZE

    /**
     * 画笔透明度
     */
    var alpha: Int = 255

    /**
     * 流量
     */
    var flowValue: Int = 255
    var flowValueEnabled = true
    //
    var penShapeEnabled = false
    var penShapePicEnabled = true
    // 形状颜色开关
    var penShapeColorEnabled = false
    // 形状反转
    var penShapeReverse = false
    // rgb影响透明度
    var penShapeRGBEffectAlpha = false
    // 抗锯齿
    var penShapeGapEnabled = false

    /**
     * 笔头方向, 默认垂直
     */
    var penVertical = true
    var penVerticalEnabled = true

    /**
     * 方形笔触
     */
    var penSquare = false
    var penSquareEnabled = true

    /**
     * 笔头色值
     */
    var color: Int = Color.BLACK
    var colorEnabled = true

    var alphaEnabled = true
    var sizeEnabled = true

    // 圆度
    var roundness = 100
    var roundnessEnabled = false
    // 旋转
    var rotation = 0f
    var rotationEnabled = true

    // 硬度
    var hardness = 0
    var hardnessEnabled = false

    // 中空度
    var midPadding = 0
    var midPaddingEnabled = true
    // 中空透明度
    var midPaddingAlpha = 0f
    var midPaddingAlphaEnabled = true
    // 最小间距
    var minSpace = 3
    // 最大间距
    var maxSpace = Constants.MAX_PEN_SIZE*5
    // 两个贝塞尔点的距离，默认3，最大200
    var space = 3
    var spaceEnabled = true
    // 随机间距，在间距基础上再加个随机数
    var randomSpace = 0
    var randomSpaceEnabled = false

    // 是否沿轨迹方向旋转
    var isRotateByPenOriental = false
    var rotateByPenOrientalEnabled = true
    // 是否随机旋转
    var isRandomDegree = false
    var randomDegreeEnabled = false

    // 线性缩放
    var linearScale = true
    var linearScaleEnabled = false

    // 画笔渲染相关
    var colorMatrixEnabled = false
    // 饱和度[0,1]
    var saturationValue = 0f
    // 色相 [0,360]
    var hueValue = 0f
    // 亮度 [-1,1]
    var brightnessValue: Float = 0f

    private var hueSatBriMatrix = ColorMatrix()

    var scaleRed: Float = 0f
    var scaleBlue: Float = 0f
    var scaleGreen: Float = 0f

    // 压感
    var penPressEnabled = false
    // 默认最小直径
    val defMinDiam = 10f
    // 最小直径[]
    var minDiam = 1f
    // 速度模拟直径
    var speedMoniPress = false

    // 笔头流量[0-100]
    protected var flow = 100

    var mStartX = 0f
    var mStartY = 0f
    var mEndX = 0f
    var mEndY = 0f

    fun resetPath() {
        mStartX = 0f
        mStartY = 0f
        mEndX = 0f
        mEndY = 0f
        mPath.reset()
    }

    open fun reset() {
        resetPath()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG) //创建抗锯齿画笔
        mPaint.isAntiAlias = true // 锯齿不优化
        mPaint.strokeJoin = Paint.Join.ROUND //接洽处为圆弧
        mPaint.strokeCap = Paint.Cap.ROUND //画笔样式圆形

        penTopEnabled = false
        style = Paint.Style.FILL
        mPath = Path()
        isAntiAlias = true

        strokeJoin = Paint.Join.ROUND

        penMixMode = null

        strokeCap = Paint.Cap.ROUND

        size = Constants.DEFAULT_PEN_SIZE
        alpha = 255
        penShapeEnabled = false
        penShapeColorEnabled = false
        penShapeReverse = false
        penShapeRGBEffectAlpha = false
        penShapeGapEnabled = false
        penVertical = true

        penSquare = false

        color = Color.BLACK
        colorEnabled = true
        alphaEnabled = true
        sizeEnabled = true

        roundness = 100
        rotation = 0f
        hardness = 0

        midPadding = 0
        midPaddingAlpha = 0f
        minSpace = 3
        maxSpace = Constants.MAX_PEN_SIZE*5
        space = 3
        randomSpace = 0

        isRotateByPenOriental = false

        linearScale = true

        colorMatrixEnabled = false
        saturationValue = 0f
        hueValue = 0f
        brightnessValue = 0f
        hueSatBriMatrix = ColorMatrix()

        scaleRed = 0f
        scaleBlue = 0f
        scaleGreen = 0f

        penPressEnabled = false
        minDiam = 1f
        speedMoniPress = false
        flow = 100
    }

    fun getPressValue(): Float {
        return minDiam*size/100
    }

    open fun freshPen() {
        // 画笔不能小于1
        if (size < 1) {
            size = 1
        }
        mPaint.style = style
        mPaint.strokeWidth = size.toFloat() //设置默认笔宽
        mPaint.color = color //设置画笔默认颜色
        mPaint.isAntiAlias = penShapeGapEnabled
        penMixMode?.let {
            mPaint.xfermode = PorterDuffXfermode(it.value)
        }
        mPaint.alpha = alpha
        if (colorMatrixEnabled) {
            adjustColorFilterPaint(hueValue, saturationValue, brightnessValue)
        } else {
            hueSatBriMatrix = ColorMatrix()
        }
        mPaint.setColorFilter(ColorMatrixColorFilter(hueSatBriMatrix))
        lastPoint = null
    }

    /**
     * 交给子类实现像素替换
     */
    open fun replacePixelAlpha(origin: Int, posX: Int, posY: Int): Int {
        return origin
    }

    open fun getPaintBitmap(bmp: Bitmap):Bitmap {
        if (penShapeEnabled) {
            // 图片颜色生效
            if (penShapeColorEnabled) {
                return bmp
            }
        }
        if (!colorEnabled) {
            return (updateBmpSize(bmp))
        }
        // 颜色变换
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        val width = bmp.width
        val height = bmp.height
        val colorList = arrayListOf<Int>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                var a = Color.alpha(bmp.getPixel(x, y))
                // 流量模拟
                a = ((flowValue.toFloat()/255f)*a).toInt()
                // 颜色反转
                if (penShapeReverse) {
                    a = 255-a
                    red = 255-red
                    green = 255-green
                    blue = 255-blue
                }
                // 直接把图片里的颜色替换成目标色, 保留alpha通道
                val bitmapColor = Color.argb(a, red, green, blue)
                colorList.add(bitmapColor)
            }
        }
        return Bitmap.createBitmap(
            colorList.toIntArray(),
            bmp.width,
            bmp.height,
            Bitmap.Config.ARGB_8888
        )
    }

    /**
     * 随机旋转角度
     */
    fun randomDegree(): Float {
        val min = 0
        val max = 360
        var number = random.nextInt(max) % (max - min + 1) + min
        if (number > (min + max) / 2) {
            number = random.nextInt(max) % (max - min + 1) + min
        }
        return number.toFloat()
    }

    open fun getRotateBitmap(bitmap: Bitmap, rotate: Float = 0f): Bitmap {
        val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 圆形笔头
        if (!penSquare) {
            canvas.drawCircle(bitmap.width/2f, bitmap.height/2f, size/2f, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        } else {
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        }
        // 中空
        if (midPadding > 0) {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            paint.alpha = midPaddingAlpha.toInt()
            paint.color = Color.TRANSPARENT
            paint.style = Paint.Style.FILL
            val space = midPadding/110f*size
            canvas.drawCircle(bitmap.width/2f, bitmap.height/2f, space-5, paint)
        }
        // 旋转与缩放
        val matrix = Matrix()
        val minSize = min(bitmap.width, bitmap.height)
        val scale = size.toFloat()/minSize
        matrix.postScale(scale, scale)
        if (penVertical) {
            matrix.postRotate(rotation+180+rotate)
        } else {
            matrix.postRotate(rotation+90+rotate)
        }
        return Bitmap.createBitmap(bmp, 0, 0, bitmap.width,
            bitmap.height, matrix, false)
    }

    fun calRotate(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): Double {
        val degToRad: Double = PI / 180.0
        val phi1 = x1 * degToRad
        val phi2 = x2 * degToRad
        val lam1 = y1 * degToRad
        val lam2 = y2 * degToRad
        val x = StrictMath.cos(phi2) * sin(lam2 - lam1)
        val y =
            StrictMath.cos(phi1) * sin(phi2) - sin(phi1) * StrictMath.cos(phi2) * StrictMath.cos(
                lam2 - lam1
            )
        return abs(atan2(x, y) * 180 / PI)
    }

    fun updateBmpSize(bitmap: Bitmap): Bitmap {
        // 做最小限制，避免图片太小无法绘制
        if (size < 2) {
            size = 2
        }
        // 缩放倍数， size最大100，所以做大缩放4倍
        val scale: Float = size.toFloat()/25
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                freshPen()
                touchDown(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchUp(event.x, event.y)
                return true
            }
        }
        return false
    }


    open fun touchDown(startX: Float, startY: Float) {
        mStartX = startX
        mStartY = startY
    }

    open fun touchUp(endX: Float, endY: Float) {
        mEndX = endX
        mEndY = endY
    }

    open fun touchMove(currentX: Float, currentY: Float) {
    }

    abstract fun onDraw(canvas: Canvas)

    private fun adjustColorFilterPaint(hue: Float,
                                       saturation: Float,
                                       brightness: Float) {
        hueSatBriMatrix.reset()
        adjustHue(hue)
        adjustSaturation(saturation)
        // hueSatBriMatrix.setSaturation(saturation)
        adjustBrightness(brightness)
    }

    // 调整色相的方法
    private fun adjustHue(value: Float) {
        val hue = cleanValue(value, 180f) / 180f * Math.PI.toFloat()
        if (hue != 0f) {
            val cosVal = Math.cos(hue.toDouble()).toFloat()
            val sinVal = Math.sin(hue.toDouble()).toFloat()
            val lumR = 0.213f
            val lumG = 0.715f
            val lumB = 0.072f
            val mat = floatArrayOf(
                lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0f, 0f,
                lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0f, 0f,
                lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
                0f, 0f, 0f, 0f, 1f
            )
            hueSatBriMatrix.postConcat(ColorMatrix(mat))
        }
    }

    // 限制值的范围在0到max之间
    private fun cleanValue(value: Float, max: Float): Float {
        return Math.min(max, Math.max(-max, value))
    }

    // 调整饱和度的方法
    private fun adjustSaturation(value: Float) {
        val x = 1 + if (value > 0) 3 * value else value
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f
        val mat = floatArrayOf(
            lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0f, 0f,
            lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0f, 0f,
            lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        )
        hueSatBriMatrix.postConcat(ColorMatrix(mat))
    }

    // 调整亮度的方法
    private fun adjustBrightness(value: Float) {
        val mat = floatArrayOf(
            1f, 0f, 0f, 0f, value,
            0f, 1f, 0f, 0f, value,
            0f, 0f, 1f, 0f, value,
            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        )
        hueSatBriMatrix.postConcat(ColorMatrix(mat))
    }
}