package com.xluo.core.widget;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * Created by xuliwen on 2019/10/12
 *
 * 简介：与业务无关的缩放 ViewGroup，只能有一个直接子 View
 *
 * 实现过程：结合自身业务需要，参考了 LargeImageView、ScrollView 来实现
 *
 * 作用：
 * 1、单指、多指滑动及惯性滑动
 * 2、双击缩放
 * 3、多指缩放
 *
 * 注意点：
 * 1、如果子 View 宽、高小于 ZoomLayout，会将子 View 在宽、高方向上居中
 *
 */
public class ZoomView extends FrameLayout {
    private static final String TAG = "ZoomLayout";
    private static final float DEFAULT_MIN_ZOOM = 0.3f;
    private static final float DEFAULT_MAX_ZOOM = 5.0f;
    private static final float DEFAULT_DOUBLE_CLICK_ZOOM = 1.5f;
    private static final int SCROLL_MIN = -100;

    private float mDoubleClickZoom;
    private float mMinZoom;
    private float mMaxZoom;
    private float mCurrentZoom = 1;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private OverScroller mOverScroller;
    private ScaleRotateHelper mScaleHelper;
    private AccelerateInterpolator mAccelerateInterpolator;
    private DecelerateInterpolator mDecelerateInterpolator;
    private ZoomLayoutGestureListener mZoomLayoutGestureListener;
    private int mLastChildHeight;
    private int mLastChildWidth;
    private int mLastHeight;
    private int mLastWidth;
    private int mLastCenterX;
    private int mLastCenterY;
    private boolean mNeedReScale;
    // 屏蔽双击缩放，避免跟画笔冲突
    private boolean isDoubleClickScaleEnabled = false;

    private float rotation = 0; // 旋转角度
    private float degree = 0f; // 触摸时的角度

    public int mScale = 100;
    private float mTargetScale = 1.0f;

    public ZoomView(Context context) {
        super(context);
        init(context, null);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    private void init(Context context, @Nullable AttributeSet attrs) {
        mScaleDetector = new ScaleGestureDetector(context, mSimpleOnScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
        mOverScroller = new OverScroller(getContext());
        mScaleHelper = new ScaleRotateHelper();
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        setWillNotDraw(false);
        mMinZoom = DEFAULT_MIN_ZOOM;
        mMaxZoom = DEFAULT_MAX_ZOOM;
        mDoubleClickZoom = DEFAULT_DOUBLE_CLICK_ZOOM;
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!isEnabled()) {
                return false;
            }
            float newScale;
            newScale = mCurrentZoom * detector.getScaleFactor();
            if (newScale > mMaxZoom) {
                newScale = mMaxZoom;
            } else if (newScale < mMinZoom) {
                newScale = mMinZoom;
            }
            setScale(newScale, (int) detector.getFocusX(), (int) detector.getFocusY());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {


        @Override
        public boolean onDown(MotionEvent e) {
            if (!mOverScroller.isFinished()) {
                mOverScroller.abortAnimation();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isDoubleClickScaleEnabled) {
                return false;
            }
            float newScale;
            if (mCurrentZoom < 1) {
                newScale = 1;
            } else if (mCurrentZoom < mDoubleClickZoom) {
                newScale = mDoubleClickZoom;
            } else {
                newScale = 1;
            }
            smoothScale(newScale, (int) e.getX(), (int) e.getY());

            mScale = (int) (newScale*100);
            updateScaleRotation();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isEnabled()) {
                return false;
            }
            processScroll((int) distanceX, (int) distanceY, getScrollRangeX(), getScrollRangeY());
            return true;
        }

        /**
         *
         * @param velocityX 滑动的速度 = 滑动的距离(滑动的起点 - 滑动的终点) / 滑动的时长，所以向上滑是负的，向下滑是正的
         * @param velocityY 同上
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isEnabled()) {
                return false;
            }
            fling((int) -velocityX, (int) -velocityY);
            return true;
        }
    };


    private boolean fling(int velocityX, int velocityY) {

        if (Math.abs(velocityX) < mMinimumVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < mMinimumVelocity) {
            velocityY = 0;
        }
        final int scrollY = getScrollY();
        final int scrollX = getScrollX();
        // 只有在能够滚动的时候，才需要处理 Fling
        final boolean canFlingX = scrollX > 0 && scrollX < getScrollRangeX();
        final boolean canFlingY = scrollY > 0 && scrollY < getScrollRangeY();
        boolean canFling = canFlingY || canFlingX;
        if (canFling) {
            // 下面两行代码的作用是将 Fling 速度限制在  [-mMaximumVelocity, mMaximumVelocity] 之间
            velocityX = Math.max(-mMaximumVelocity, Math.min(velocityX, mMaximumVelocity));
            velocityY = Math.max(-mMaximumVelocity, Math.min(velocityY, mMaximumVelocity));
            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int width = getWidth() - getPaddingRight() - getPaddingLeft();
            int bottom = getContentHeight();
            int right = getContentWidth();
            mOverScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, Math.max(0, right - width), 0,
                    Math.max(0, bottom - height), 0, 0);
            notifyInvalidate();
            return true;
        }
        return false;
    }

    public void smoothScale(float newScale, int centerX, int centerY) {
        if (mCurrentZoom > newScale) {
            if (mAccelerateInterpolator == null) {
                mAccelerateInterpolator = new AccelerateInterpolator();
            }
            mScaleHelper.startScale(mCurrentZoom, newScale, centerX, centerY, mAccelerateInterpolator);
        } else {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mScaleHelper.startScale(mCurrentZoom, newScale, centerX, centerY, mDecelerateInterpolator);
        }
        notifyInvalidate();
    }

    private void notifyInvalidate() {
        // 效果和 invalidate 一样，但是会使得动画更平滑
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void resetScale() {
        setScale(mTargetScale, getWidth()/2, getHeight()/2);
    }

    public void initScaleSize(float targetScale) {
        this.mTargetScale = targetScale;
        setScale(targetScale, 0, 0);
    }

    public void resetRotation() {
        rotation = 0;
        setViewRotation();
        scrollTo(0, 0);
        if (mZoomLayoutGestureListener != null) {
            mZoomLayoutGestureListener.onZoomChanged(mScale, (int)rotation);
        }
    }

    private void setScale(float scale, int centerX, int centerY) {
        mScale = (int)(scale*100);
        mLastCenterX = centerX;
        mLastCenterY = centerY;
        mCurrentZoom = scale;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setScaleX(mCurrentZoom);
            child.setScaleY(mCurrentZoom);
        }
        notifyInvalidate();
        updateScaleRotation();
    }

    private void updateScaleRotation() {
        if (mZoomLayoutGestureListener != null) {
            int r = (int)rotation;
            // 旋转做下精确值处理，免得产品挑刺
            if (rotation > 0 && rotation < 1) {
                r = 1;
            }
            if (rotation < 0 && rotation > -1) {
                r = -1;
            }
            mZoomLayoutGestureListener.onZoomChanged(mScale, r);
        }
    }

    private void processScroll(int deltaX, int deltaY,
                               int scrollRangeX, int scrollRangeY) {
        int oldScrollX = getScrollX();
        int oldScrollY = getScrollY();
        int newScrollX = oldScrollX + deltaX;
        int newScrollY = oldScrollY + deltaY;
        final int left = SCROLL_MIN;
        // 允许滑动的最右侧值
        final int right = scrollRangeX + getContentWidth();
        final int top = SCROLL_MIN;
        // 滑动的最下侧值
        final int bottom = scrollRangeY + getContentHeight();
        if (newScrollX > right) {
            newScrollX = right;
        } else if (newScrollX < left) {
            newScrollX = left;
        }
        if (newScrollY > bottom) {
            newScrollY = bottom;
        } else if (newScrollY < top) {
            newScrollY = top;
        }
        // 允许滑到一定负值，否则画布边角不好处理
        if (newScrollX < SCROLL_MIN) {
            newScrollX = SCROLL_MIN;
        }
        if (newScrollY < SCROLL_MIN) {
            newScrollY = SCROLL_MIN;
        }
        scrollTo(newScrollX, newScrollY);
    }


    private int getScrollRangeX() {
        final int contentWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        return (getContentWidth() - contentWidth);
    }

    private int getContentWidth() {
        return (int) (child().getWidth() * mCurrentZoom);
    }

    private int getScrollRangeY() {
        final int contentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        return getContentHeight() - contentHeight;
    }

    private int getContentHeight() {
        return (int) (child().getHeight() * mCurrentZoom);
    }

    private View child() {
        return getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mNeedReScale) {
            // 需要重新刷新，因为宽高已经发生变化
            setScale(mCurrentZoom, mLastCenterX, mLastCenterY);
            mNeedReScale = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        child().setClickable(true);
        if (mLastChildWidth != child().getWidth() || mLastChildHeight != child().getHeight() || mLastWidth != getWidth()
                || mLastHeight != getHeight()) {
            // 宽高变化后，记录需要重新刷新，放在下次 onLayout 处理，避免 View 的一些配置：比如 getTop() 没有初始化好
            // 下次放在 onLayout 处理的原因是 setGravity 会在 onLayout 确定完位置，这时候去 setScale 导致位置的变化就不会导致用户看到
            // 闪一下的问题
            mNeedReScale = true;
        }
        mLastChildWidth = child().getWidth();
        mLastChildHeight = child().getHeight();
        mLastWidth = getWidth();
        mLastHeight = getHeight();
        if (mNeedReScale) {
            notifyInvalidate();
        }
    }

    /**
     * 通常配合 Scroller、OverScroller 实现平滑滚动。如 Fling 的时候进行平滑滚动。
     * Scroller、OverScroller 负责计算一段时间内的 ScrollX、ScrollY 的平滑变化
     * 然后调用 ViewCompat.postInvalidateOnAnimation(this); 之后就可以在
     * computeScroll() 不断去获取 ScrollX、ScrollY 的变化了，再通过 ScrollTo 设置给 View
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScaleHelper.computeScrollOffset()) {
            setScale(mScaleHelper.getCurScale(), mScaleHelper.getStartX(), mScaleHelper.getStartY());
        }
    }

    private int touchType = 1;
    private float lastRotation;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 处理旋转
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 单点触摸
                touchType = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 多点触摸
                touchType = 2;
                degree = mScaleHelper.getDegree(ev);
                lastRotation = getChildAt(0).getRotation();
                break;
            case MotionEvent.ACTION_MOVE:
                if(touchType == 2 && ev.getPointerCount() > 1) {
                    rotation = lastRotation + mScaleHelper.getDegree(ev) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    setViewRotation();
                }
                break;
        }
        if (touchType == 2) {
            // 手势缩放
            mGestureDetector.onTouchEvent(ev);
            mScaleDetector.onTouchEvent(ev);
            mZoomLayoutGestureListener.onZooming();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setViewRotation() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.setRotation(rotation);
        }
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int usedTotal = getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin +
                heightUsed;
        final int childHeightMeasureSpec;
        if (lp.height == WRAP_CONTENT) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.max(0, MeasureSpec.getSize(parentHeightMeasureSpec) - usedTotal),
                    MeasureSpec.UNSPECIFIED);
        } else {
            childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
                            + heightUsed, lp.height);
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    /**
     * 是否可以在水平方向上滚动
     * 举例: ViewPager 通过这个方法判断子 View 是否可以水平滚动，从而解决滑动冲突
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return getScrollX() < getScrollRangeX();
        } else {
            return getScrollX() > 0 && getScrollRangeX() > 0;
        }
    }

    /**
     * 是否可以在竖直方向上滚动
     * 举例: ViewPager 通过这个方法判断子 View 是否可以竖直滚动，从而解决滑动冲突
     */
    @Override
    public boolean canScrollVertically(int direction) {
        if (direction > 0) {
            return getScrollY() < getScrollRangeY();
        } else {
            return getScrollY() > 0 && getScrollRangeY() > 0;
        }
    }

    public void setZoomLayoutGestureListener(ZoomLayoutGestureListener zoomLayoutGestureListener) {
        mZoomLayoutGestureListener = zoomLayoutGestureListener;
    }

    public interface ZoomLayoutGestureListener {
        void onZoomChanged(int scale, int rotation);

        void onZooming();
    }
}
