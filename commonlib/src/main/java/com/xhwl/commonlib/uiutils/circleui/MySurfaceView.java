package com.xhwl.commonlib.uiutils.circleui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.xhwl.commonlib.uiutils.UiTools;


/**
 * Created by Administrator on 2018/10/23.
 */

public class MySurfaceView extends SurfaceView {
    private Paint paint;
    private int widthSize;
    private Camera camera;
    private int height;



    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MySurfaceView(Context context) {
        super(context);
        initView();
    }


    private void initView() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //获取屏幕长宽比例，这样设置不会发生畸变，千万不要根据一个手机设定一个数
        //那样换一个手机就可能会出现显示的比例问题
        int screenWidth = UiTools.getScreenWidth(getContext());
        int screenHeight = UiTools.getScreenHeight(getContext());
        height=600;
        //可以理解为红色的背景盖住了大部分的区域，我们只能看到圆框里面的，如果还是按照原来的比例绘制surfaceview
        //需要把手机拿的很远才可以显示出整张脸，故而我用了一个比较取巧的办法就是，按比例缩小，试验了很多数后，感觉0.55
        //是最合适的比例
        double screenWidth1= 0.55*screenWidth;
        double screenHeight1= 0.55*screenHeight;
        Log.e("onMeasure", "widthSize="+widthSize);
        Log.e("onMeasure", "draw: widthMeasureSpec = " +screenWidth + "  heightMeasureSpec = " + screenHeight);
        //绘制的输入参数必须是整数型，做浮点型运算后为float型数据，故需要做取整操作
        setMeasuredDimension((int) screenWidth1, (int) screenHeight1);
        //setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    //绘制一个圆形的框，并设置圆框的坐标和半径大小
    //这个绘制在16:9的手机上显示很好，但是在更长的手机上（大于16/9）会偏上，
    public void draw(Canvas canvas) {
        Log.e("onDraw", "draw: test");
        Path path = new Path();
//        path.addCircle(widthSize / 2, height / 2, height / 2, Path.Direction.CCW);
        path.addCircle(widthSize / 2, widthSize / 2, widthSize / 2, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("onDraw", "onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int screenWidth = UiTools.getScreenWidth(getContext());
        int screenHeight = UiTools.getScreenHeight(getContext());
        Log.d("screenWidth",Integer.toString(screenWidth));
        Log.d("screenHeight",Integer.toString(screenHeight));
        w = screenWidth;
        h = screenHeight;
        super.onSizeChanged(w, h, oldw, oldh);

    }
}
