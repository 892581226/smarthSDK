package com.xhwl.commonlib.uiutils.circleui;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;


/**
 * Created by ${符柱成} on 2016/10/24.
 */

public class UpCircleMenuLayout extends ViewGroup {

    private int mRadius;

    public static final float DEFAULT_BANNER_WIDTH = 750.0f;
    public static final float DEFAULT_BANNER_HEIGTH = 350.0f;

    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 60.0f;

    private static final float RADIO_TOP_CHILD_DIMENSION = 60.0f;

    /**
     * 菜单的中心child的默认尺寸
     */
    private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private static final float RADIO_PADDING_LAYOUT = 20;


    private static final int RADIO_MARGIN_LAYOUT = 18;
    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 10;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;

    /**
     * 布局时的开始角度
     */
    private float mStartAngle = 18;
    /**
     * 菜单项的图标
     */
    private int[] mItemImgs;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;

    /**
     * 菜单的个数
     */
    private int mMenuItemCount;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isTouchUp = true;

    /**
     * 当前中间顶部的那个
     */
    private int mCurrentPosition = 0;

    private int Total = 10;

    /**
     * 弧形划过的角度
     */
    private int sweepAngle;
    private RectF sectorRectF;

    /**
     * 圆心坐标
     */
    private int mCenter;

    private TextView mTextView;
    private ImageView mImageView;

    private int mMenuItemLayoutId = R.layout.circle_menu_item;

    public UpCircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        double startAngle = mStartAngle;

        double angle = 360 / Total;

        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mCenter = width / 2;

        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY
                || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度

            resWidth = getDefaultWidth();

            resHeight = (int) (resWidth * DEFAULT_BANNER_HEIGTH /
                    DEFAULT_BANNER_WIDTH);

        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得直径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize;

        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;

        // 迭代测量：根据孩子的数量进行遍历，为每一个孩子测量大小，设置监听回调。
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            startAngle = startAngle % 360;
            if (startAngle > 269 && startAngle < 271 && isTouchUp) {
                mOnMenuItemClickListener.itemClick(i);//设置监听回调。
                mCurrentPosition = i; //本次使用mCurrentPosition，只是把他作为一个temp变量。可以有更多的使用，比如动态设置每个孩子相隔的角度
                childSize = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);            //设置大小
            } else {
                childSize = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
                    childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
            startAngle += angle;
        }
        //item容器内边距
        mPadding = DensityUtil.dip2px(getContext(), RADIO_MARGIN_LAYOUT);

    }

    /**
     * MenuItem的点击事件接口
     *
     * @author zhy
     */
    public interface OnMenuItemClickListener {
        void itemClick(int pos);

        void itemCenterClick(View view, int position);
    }

    /**
     * MenuItem的点击/滑动事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    /**
     * 设置MenuItem的点击事件接口
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布置圆的半径
        int layoutRadius = mRadius;
        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth;

        //2.1设置每一个扇形的角度
        sweepAngle = 360 / Total;
        //2.2设置扇形绘制的范围
        sectorRectF = new RectF(getPaddingLeft(), getPaddingLeft(),
                mCenter * 2 - getPaddingLeft(), mCenter * 2 - getPaddingLeft());

        // 根据menu item的个数，计算角度
        float angleDelay = 360 / Total;
        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            //根据孩子遍历，设置中间顶部那个的大小以及其他图片大小。
            if (mStartAngle > 269 && mStartAngle < 271 && isTouchUp) {
                cWidth = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);
                child.setSelected(true);
            } else {
                cWidth = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
                child.setSelected(false);
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            //大于360就取余归于小于360度
            mStartAngle = mStartAngle % 360;

            float tmp = 0;
            //计算图片布置的中心点的圆半径。就是tmp
            tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
            //tmp cosa 即menu item中心点的横坐标。计算的是item的位置，是计算位置！！！
            left = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth) + DensityUtil
                    .dip2px(getContext(), 1);
            // tmp sina 即menu item的纵坐标
            top = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth) + DensityUtil
                    .dip2px(getContext(), 8);
            //接着当然是布置孩子的位置啦，就是根据小圆的来布置的
            child.layout(left, top, left + cWidth, top + cWidth);
            //child.device_smarthome_list_item_layout(0, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    private void backOrPre() {     //缓冲的角度。即我们将要固定几个位置，而不是任意位置。我们要设计一个可能的角度去自动帮他选择。
        isTouchUp = true;
        float angleDelay = 360 / Total;              //这个是每个图形相隔的角度
        if ((mStartAngle - 18) % angleDelay == 0) {
            return;
        }
        float angle = (float) ((mStartAngle - 18) % 36);  //angle就是那个不是18度开始布局，然后是36度的整数的多出来的部分角度
        if (angleDelay / 2 > angle) {
            mStartAngle -= angle;
        } else if (angleDelay / 2 < angle) {
            mStartAngle = mStartAngle - angle + angleDelay; //mStartAngle就是当前角度啦，取余36度就是多出来的角度，拿这个多出来的角度去数据处理。
        }
        requestLayout();
    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;


    //dispatchTouchEvent是处理触摸事件分发,事件(多数情况)是从Activity的dispatchTouchEvent开始的。执行super.dispatchTouchEvent(ev)，事件向下分发。
    //onTouchEvent是View中提供的方法，ViewGroup也有这个方法，view中不提供onInterceptTouchEvent。view中默认返回true，表示消费了这个事件。
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchUp = false;          //注意isTouchUp 这个标记量！！！
                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else
                // 二、三象限，色角度值是付值
                {
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                // 重新布局
                if (mTmpAngle != 0) {
                    requestLayout();
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                backOrPre();
                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    public void setAngle(int position) {
        float angleDelay = 360 / Total;
        if (position > mCurrentPosition) {
            mStartAngle += (mCurrentPosition - position) * angleDelay;
        } else {
            mStartAngle -= (position - mCurrentPosition) * angleDelay;
        }
        requestLayout();
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
        mItemImgs = resIds;
        mItemTexts = texts;

        // 参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;

        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }
        removeAllViews();
        addMenuItems();
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(String[] resIds) {
        mItemTexts = resIds;

        // 参数检查
        if (resIds == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? 0 : resIds.length;

        addMenuItems();
    }


    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数，初始化view
         */
        float rotation = 360 / Total;
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            mImageView = view.findViewById(R.id.id_circle_menu_item_image);
            mTextView = view.findViewById(R.id.id_circle_menu_item_text);

//            if (mMenuItemCount > 5) {
//                for (int k = 0; k < 5; k++) {
//                    tv.setRotation(i * 36);
//                }
//            } else {
//                tv.setRotation(i * 36);
//            }
            LinearLayout linearLayout = view.findViewById(R.id.circle_menu_item_liner);
            if (mImageView != null) {
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageResource(mItemImgs[i]);
            }
            if (mTextView != null) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(mItemTexts[i]);
            }
            if (linearLayout != null) {
                linearLayout.setOnClickListener(view1 -> {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemCenterClick(view1, j);
                    }
                });
            }
            // 添加view到容器中
            addView(view);
        }
    }

    /**
     * 使用path添加一个路径
     * 绘制文字的路径
     *
     * @param canvas
     * @param mString
     */
//    private void drawTexts(Canvas canvas, String mString) {
//        Path path = new Path();
//        //添加一个圆弧的路径
//        path.addArc(sectorRectF, mStartAngle, sweepAngle);
//        String startText = null;
//        String endText = null;
//        //水平偏移
//        int hOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - mTextView.getWidth() / 2);
//        //计算弧长 处理文字过长换行
//        int l = (int) ((360 / mCount) * Math.PI * mRadius / 180);
//        if (textWidth > l * 4 / 5) {
//            int index = mString.length() / 2;
//            startText = mString.substring(0, index);
//            endText = mString.substring(index, mString.length());
//
//            float startTextWidth = mTextPaint.measureText(startText);
//            float endTextWidth = mTextPaint.measureText(endText);
//            //水平偏移
//            hOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - startTextWidth / 2);
//            int endHOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - endTextWidth / 2);
//            //文字高度
//            int h = (int) ((mTextPaint.ascent() + mTextPaint.descent()) * 1.5);
//
//            //根据路径绘制文字
//            //hOffset 水平的偏移量 vOffset 垂直的偏移量
//            canvas.drawTextOnPath(startText, path, hOffset, mRadius / 6, mTextPaint);
//            canvas.drawTextOnPath(endText, path, endHOffset, mRadius / 6 - h, mTextPaint);
//        } else {
//            //根据路径绘制文字
//            canvas.drawTextOnPath(mString, path, hOffset, mRadius / 6, mTextPaint);
//        }
//
//    }


    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue
     */
    public void setFlingableValue(int mFlingableValue) {
        this.mFlingableValue = mFlingableValue;
    }

    /**
     * 设置内边距的比例
     *
     * @param mPadding
     */
    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

}
