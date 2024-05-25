package com.xluo.pen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.xluo.draw_core.R;
import com.xluo.pen.bean.ControllerPoint;
import com.xluo.pen.shape.PenBezier;


/**
 * @author xluo
 * @version v1.0 create at 2022/12/10
 * @des 粉笔
 */
public class PenChalk extends PenBezier {

    private Bitmap mBitmap;
    //第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
    protected RectF mNeedDrawRect = new RectF();
    protected Bitmap mOriginBitmap;
    private Context mContext;

    public PenChalk(Context context, int paintId) {
        super(paintId);
        mContext = context;
        setDensity(3);
        setAlphaEnabled(false);
    }

    @Override
    public void freshPen() {
        super.freshPen();
        // 透明度从color值中取，否则不生效。且原值是0-255值太大，0-25比较合适
        setAlpha(Color.alpha(getColor())/10);
        mOriginBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.small_circle);
        mBitmap = getPaintBitmap(mOriginBitmap);
    }

    private void doNeetToDo(Canvas canvas, ControllerPoint point, Paint paint) {
        paint.setAlpha(getAlpha()/4);
        canvas.drawBitmap(mBitmap, point.x-getSize()/2, point.y-getSize()/2, paint);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        while (getMHWPointList().size() > 0) {
            ControllerPoint point = getMHWPointList().pop();
            doNeetToDo(canvas, point, getMPaint());
            setMCurPoint(point);
        }
    }
}
