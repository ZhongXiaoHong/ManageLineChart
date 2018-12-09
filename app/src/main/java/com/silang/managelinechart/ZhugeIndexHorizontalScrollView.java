package com.silang.managelinechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;


/**
 * Created by user on 2016/10/19.
 */
public class ZhugeIndexHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "IndexHorizontal";
    private Paint textPaint;
    private LineCompareView LineCompareView;
    private int scrollY;

    public ZhugeIndexHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ZhugeIndexHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhugeIndexHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public int computeHorizontalScrollOffset() {
        int width = Math.max(0, super.computeHorizontalScrollOffset());
        System.out.println("###" + width);
        return Math.max(0, super.computeHorizontalScrollOffset());
    }


    private void init() {
        textPaint = new Paint();
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
        textPaint.setAntiAlias(true);
        textPaint.setColor(new Color().WHITE);

        setOnTouchListener(new OnTouchListener() {
            private int lastX = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    if (msg.what == touchEventId) {
                        View scroller = (View) msg.obj;
                        if (lastX == scroller.getScrollX()) {//前后两次总横向滑动距离相等视为停止划动
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 5);
                            lastX = scroller.getScrollX();
                        }
                    }
                }
            };


            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }


            private void handleStop(Object view) {


                if (LineCompareView != null) {
                    int offset = LineCompareView.getStopAdsorbOffset();
                    smoothScrollBy(offset, 0);
                    // scrollBy(offset,0);

                }


            }
        });


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int offset = computeHorizontalScrollOffset();  //当前的滑动距离
        int maxOffset = computeHorizontalScrollRange() - DisplayUtil.getScreenWidth(getContext());//horizatolView 最大滑动距离
        if (LineCompareView != null) {
            LineCompareView.setScrollOffset(offset, maxOffset);
            // LineCompareView.setScrollOffset(getScrollX(), maxOffset);
        }
    }

    public void setLineCompareView(LineCompareView LineCompareView) {
        this.LineCompareView = LineCompareView;

    }


}
