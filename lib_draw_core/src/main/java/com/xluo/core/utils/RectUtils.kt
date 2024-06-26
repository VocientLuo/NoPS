package com.xluo.core.utils

import android.graphics.*
import com.xluo.core.constants.Constants
import com.xluo.core.entity.PvsShapeLayer


object RectUtils {


    fun getShowRect(viewWidth: Int, viewHeight: Int, photoWidth: Int, photoHeight: Int): Rect {
        var targetWidth = 0
        var targetHeight = 0
        var vRatio = viewWidth / viewHeight.toFloat()
        var pRatio = photoWidth / photoHeight.toFloat()

        if (viewWidth >= viewHeight) {
            if (vRatio >= pRatio) {
                targetHeight = viewHeight
                targetWidth = (targetHeight * pRatio).toInt()
            } else {
                targetWidth = viewWidth
                targetHeight = (targetWidth / pRatio).toInt()
            }
        } else {
            if (vRatio >= pRatio) {
                targetHeight = viewHeight
                targetWidth = (targetHeight * pRatio).toInt()
            } else {
                targetWidth = viewWidth
                targetHeight = (targetWidth / pRatio).toInt()
            }
        }
        if (targetWidth > viewWidth) {
            targetWidth = viewWidth
        }
        if (targetHeight > viewHeight) {
            targetHeight = viewHeight
        }

        val showRectF = Rect()
        showRectF.left = (viewWidth - targetWidth) / 2
        showRectF.right = showRectF.left + targetWidth
        showRectF.top = (viewHeight - targetHeight) / 2
        showRectF.bottom = showRectF.top + targetHeight
        return showRectF
    }



    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    fun scaleRect(rect: RectF, scale: Float) {
        val w = rect.width()
        val h = rect.height()
        val newW = scale * w
        val newH = scale * h
        val dx = (newW - w) / 2
        val dy = (newH - h) / 2
        rect.left -= dx
        rect.top -= dy
        rect.right += dx
        rect.bottom += dy
    }

    fun scaleRect(rect: Rect, scale: Float) {
        val w = rect.width()
        val h = rect.height()
        val newW = scale * w
        val newH = scale * h
        val dx = (newW - w) / 2
        val dy = (newH - h) / 2
        rect.left = (rect.left.toFloat() - dx).toInt()
        rect.top = (rect.top.toFloat() - dy).toInt()
        rect.right = (rect.right.toFloat() + dx).toInt()
        rect.bottom = (rect.bottom.toFloat() + dy).toInt()
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    fun rotateRect(
        rect: RectF, center_x: Float, center_y: Float,
        roatetAngle: Float
    ) {
        val x = rect.centerX()
        val y = rect.centerY()
        val sinA = Math.sin(Math.toRadians(roatetAngle.toDouble())).toFloat()
        val cosA = Math.cos(Math.toRadians(roatetAngle.toDouble())).toFloat()
        val newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA
        val newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA
        val dx = newX - x
        val dy = newY - y
        rect.offset(dx, dy)
    }

    /**
     * 旋转Point点
     * @param p
     * @param center_x
     * @param center_y
     * @param roatetAngle
     */
    fun rotatePoint(
        p: Point, center_x: Float, center_y: Float,
        roatetAngle: Float
    ) {
        val sinA = Math.sin(Math.toRadians(roatetAngle.toDouble())).toFloat()
        val cosA = Math.cos(Math.toRadians(roatetAngle.toDouble())).toFloat()
        // calc new point
        val newX = center_x + (p.x - center_x) * cosA - (p.y - center_y) * sinA
        val newY = center_y + (p.y - center_y) * cosA + (p.x - center_x) * sinA
        p[newX.toInt()] = newY.toInt()
    }


    fun getTranXByMatrix(matrix: Matrix): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MTRANS_X]
    }

    fun getTranYByMatrix(matrix: Matrix): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MTRANS_Y]
    }

    fun getTranScaleXByMatrix(matrix: Matrix): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    fun getTranScaleYByMatrix(matrix: Matrix): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return values[Matrix.MSCALE_Y]
    }

    val tempRectF = RectF()

    const val mPiDouble = 2 * Math.PI

    /**
     * 获取对应区域path形状数据
     * @param shapeType Int
     * @param viewWidth Int
     * @param viewHeight Int
     * @param canvasWidth Int
     * @param canvasHeight Int
     * @param rectF RectF
     * @param path Path
     * @return Path?
     */
    fun initShapePath(
        shapeType: Int,
        rectF: RectF, path: Path
    ) {
        path.reset()
        when (shapeType) {
            PvsShapeLayer.SHAPE_CIRCLE -> {
                path.addOval(rectF, Path.Direction.CW)
            }
            PvsShapeLayer.SHAPE_CIRCLE_RING -> {
                tempRectF.set(rectF)
                tempRectF.inset(Constants.RING_WIDTH, Constants.RING_WIDTH)
                path.addOval(tempRectF, Path.Direction.CW)
            }
            PvsShapeLayer.SHAPE_HEXAGON -> {
                val mCenterAngle = (mPiDouble / 6)
                val mRadius = (rectF.width() / 2f)
                for (i in 1..6) {
                    //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
                    val pointX =
                        (Math.cos(i * mCenterAngle) * mRadius).toFloat() + rectF.left + mRadius
                    //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
                    val pointY =
                        (Math.sin(i * mCenterAngle) * mRadius).toFloat() + rectF.top + mRadius
                    //如果是一个点，则移动到这个点，作为起点
                    if (i == 1) {
                        path.moveTo(pointX, pointY)
                    } else {
                        //其他的点，就可以连线了
                        path.lineTo(pointX, pointY)
                    }
                }
                path.close()
            }
            PvsShapeLayer.SHAPE_PENTAGON -> {
                val mCenterAngle = (mPiDouble / 5)
                val mRadius = (rectF.width() / 2f)
                for (i in 1..5) {
                    //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
                    val pointX =
                        (Math.cos(i * mCenterAngle) * mRadius).toFloat() + rectF.left + mRadius
                    //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
                    val pointY =
                        (Math.sin(i * mCenterAngle) * mRadius).toFloat() + rectF.top + mRadius
                    //如果是一个点，则移动到这个点，作为起点
                    if (i == 1) {
                        path.moveTo(pointX, pointY)
                    } else {
                        //其他的点，就可以连线了
                        path.lineTo(pointX, pointY)
                    }
                }
                path.close()
            }
            PvsShapeLayer.SHAPE_RECTANGLE -> {
                path.addRect(rectF, Path.Direction.CW)
            }
            PvsShapeLayer.SHAPE_RECTANGLE_RING -> {
                tempRectF.set(rectF)
                tempRectF.inset(Constants.RING_WIDTH, Constants.RING_WIDTH)
                path.addRoundRect(
                    tempRectF,
                    rectF.width() / 5,
                    rectF.width() / 5,
                    Path.Direction.CW
                )
            }
            PvsShapeLayer.SHAPE_ROUND_RECTANGLE -> {
                path.addRoundRect(rectF, rectF.width() / 5, rectF.width() / 5, Path.Direction.CW)
            }
            PvsShapeLayer.SHAPE_STAR -> {
                initStarPath(path, rectF)
            }
            PvsShapeLayer.SHAPE_TRIANGLE -> {
                path.moveTo(rectF.width() / 2 + rectF.left, rectF.top)
                path.lineTo(rectF.right, rectF.bottom)
                path.lineTo(rectF.left, rectF.bottom)
                path.lineTo(rectF.width() / 2 + rectF.left, rectF.top)
            }
        }
    }


    fun initStarPath(path: Path, rect: RectF) {
        //计算平均角度，例如360度分5份，每一份角都为72度
        //计算外边大圆的半径
        val halfWidth = (rect.width() / 2f)
        val mOutCircleRadius = (rect.width() / 2f) * 0.95f;
        //计算里面小圆的的半径
        val mInnerCircleRadius = (rect.width() / 2f) * 0.5f;
        val mAngleNum = 5
        val averageAngle: Float = 360f / mAngleNum
        //计算大圆的外角的角度，从右上角为例计算，90度的角减去一份角，得出剩余的小角的角度，例如90 - 72 = 18 度
        val outCircleAngle = 90 - averageAngle
        //一份平均角度的一半，例如72 / 2 = 36度
        val halfAverageAngle = averageAngle / 2f
        //计算出小圆内角的角度，36 + 18 = 54 度
        val internalAngle = halfAverageAngle + outCircleAngle
        //创建2个点
        //创建2个点
        val outCirclePoint = Point()
        val innerCirclePoint = Point()
        for (i in 0 until mAngleNum) {
            //计算大圆上的点坐标
            //x = Math.cos((18 + 72 * i) / 180f * Math.PI) * 大圆半径
            outCirclePoint.x =
                (Math.cos(angleToRadian(outCircleAngle + i * averageAngle)) * mOutCircleRadius).toInt() + (halfWidth + rect.left).toInt()
            outCirclePoint.y =
                -(Math.sin(angleToRadian(outCircleAngle + i * averageAngle)) * mOutCircleRadius).toInt() + (halfWidth + rect.top).toInt()
            //计算小圆上的点坐标
            //x = Math.cos((54 + 72 * i) / 180f * Math.PI ) * 小圆半径
            innerCirclePoint.x =
                (Math.cos(angleToRadian(internalAngle + i * averageAngle)) * mInnerCircleRadius).toInt() + (halfWidth + rect.left).toInt()
            innerCirclePoint.y =
                -(Math.sin(angleToRadian(internalAngle + i * averageAngle)) * mInnerCircleRadius).toInt() + (halfWidth + rect.top).toInt()
            //第一次，先移动到第一个大圆上的点
            if (i == 0) {
                path.moveTo(outCirclePoint.x.toFloat(), outCirclePoint.y.toFloat())
            }
            //坐标连接，先大圆角上的点，再到小圆角上的点
            path.lineTo(outCirclePoint.x.toFloat(), outCirclePoint.y.toFloat())
            path.lineTo(innerCirclePoint.x.toFloat(), innerCirclePoint.y.toFloat())
        }
        path.close()
    }

    private fun angleToRadian(angle: Float): Double {
        return angle / 180f * Math.PI
    }

}