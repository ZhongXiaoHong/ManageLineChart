package com.silang.managelinechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: zhongxiaohong
 * date: 2018/6/2.
 * desc:
 */

public class LineCompareView extends View {


    double minY_1 = 22;
    double maxY_1 = 30;

    double minY_2 = 27;
    double maxY_2 = 37;
    private int itemSize;  //24小时
    private static int ITEM_WIDTH = (int) (80 * SupportMultipleScreensUtil.scale);//80; //每个Item的宽度
    private static int MARGIN_LEFT_ITEM = (int) (70 * SupportMultipleScreensUtil.scale);// 70; //左边预留宽度
    private static int MARGIN_RIGHT_ITEM = (int) (70 * SupportMultipleScreensUtil.scale);//70; //右边预留宽度


    private int mHeight, mWidth;
    private int tempBaseTop;  //温度折线的上边Y坐标
    private int tempBaseBottom; //温度折线的下边Y坐标
    private Paint bitmapPaint, linePaint_1, linePaint_2, pointPaint_1,
            pointPaint_2, dashLinePaint;
    private TextPaint textPaint;

    private List<LineItem> listItems_1;//TODO  两组模拟的数据
    private List<LineItem> listItems_2;
    private int maxScrollOffset = 1000;//滚动条最长滚动距离-----------------------------------------------
    private int scrollOffset = 0; //滚动条偏移量
    private int currentItemIndex = 0; //当前滚动的位置所对应的item下标
    private int currentWeatherRes = -1;

    private int color1 = Color.parseColor("#FF2B5798");
    private int color2 = Color.parseColor("#FFFF0000");
    double[] arrayConvert_1;
    double[] arrayConvert_2;
    double array_1[];
    double array_2[];
    String[] time;


    boolean emptyData = true;
    private String[] markTitles;

    public void setMarkTitles(String[] markTitles) {
        if (markTitles == null || markTitles.length != 3) {
            throw new RuntimeException("MarkTitle 必须是3个！！！");
        }
        this.markTitles = markTitles;
    }


    public void setDatas(double array_1[], double array_2[], String[] time) {
        if (array_1.length == array_2.length && time.length == array_2.length && array_1.length > 0) {
            System.out.println("事件开始：" + new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis())));
            this.array_1 = array_1;
            this.array_2 = array_2;
            this.time = time;
            this.arrayConvert_1 = new double[array_1.length];
            this.arrayConvert_2 = new double[array_1.length];
            emptyData = false;
            init(context);
            System.out.println("事件结束：" + new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis())));
            postInvalidate();
        }


    }

    private boolean isHorizontalAxisLineDrawed;
    private int rectTop;
    private Context context;
    private int marginBottom;
    private int horizontalAxisTextEndY;  //X轴的结束y坐标
    int bottomTextHeight;
    int horizontalAxiLineEndY;
    int horizontalAxiLineStartY;
    private int markViewWidth;
    private int markViewHeight;
    private int firstItemX;


    Bitmap cacheBitmap;
    Canvas cacheCanvas;
    boolean isFirstLoad = true;


    public LineCompareView(Context context) {
        this(context, null, 0);
    }

    public LineCompareView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineCompareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private void init(Context context) {

        this.context = context;
        itemSize = array_1.length;
        arrayConvert_1 = convert(array_1, minY_1, maxY_1);
        arrayConvert_2 = convert(array_2, minY_2, maxY_2);
        mWidth = MARGIN_LEFT_ITEM + MARGIN_RIGHT_ITEM + itemSize * ITEM_WIDTH;
        mHeight = 600; //暂时先写死

        markViewHeight = (int) (131 * SupportMultipleScreensUtil.scale);
        markViewWidth = (int) (214 * SupportMultipleScreensUtil.scale);
        marginBottom = (int) (3 * SupportMultipleScreensUtil.scale);
        rectTop = mHeight - marginBottom;
        horizontalAxisTextEndY = rectTop - (int) (23 * SupportMultipleScreensUtil.scale);
        tempBaseTop = markViewHeight + 20;


        bottomTextHeight = (int) (27 * SupportMultipleScreensUtil.scale);
        horizontalAxiLineEndY = horizontalAxisTextEndY - bottomTextHeight - (int) (7 * SupportMultipleScreensUtil.scale);
        horizontalAxiLineStartY = horizontalAxiLineEndY - (int) (1 * SupportMultipleScreensUtil.scale + 0.5);

        tempBaseBottom = (horizontalAxiLineEndY - 20);

        initLineItems();
        initPaint();
    }

    double[] convert(double[] temp, double minY, double maxY) {
        double minX = 0, maxX = 0;
        double[] convert = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            if (i == 0) {
                minX = temp[0];
                maxX = temp[0];
            } else {
                if (minX > temp[i]) {
                    minX = temp[i];
                }
                if (maxX < temp[i]) {
                    maxX = temp[i];
                }
            }
        }
//        minX * k + b = minY;
//        maxX * k + b = maxY;
//        k *=;
        double k, b;

        if ((maxX - minX != 0) && maxX != 0) {
            k = (maxY - minY) / (maxX - minX);
            b = maxY - (maxX * k);
        } else {
            k = 0;
            b = minY;
        }

        for (int i = 0; i < temp.length; i++) {
            convert[i] = k * temp[i] + b;
        }

        return convert;


    }

    private void initPaint() {
        pointPaint_1 = new Paint();
        pointPaint_1.setColor(color1);
        pointPaint_1.setAntiAlias(true);
        pointPaint_1.setTextSize(8);

        pointPaint_2 = new Paint();
        pointPaint_2.setColor(color2);
        pointPaint_2.setAntiAlias(true);
        pointPaint_2.setTextSize(8);

        linePaint_1 = new Paint();
        linePaint_1.setColor(new Color().WHITE);
        linePaint_1.setAntiAlias(true);
        linePaint_1.setStyle(Paint.Style.STROKE);
        linePaint_1.setStrokeWidth(5);

        linePaint_2 = new Paint();
        linePaint_2.setColor(new Color().WHITE);
        linePaint_2.setAntiAlias(true);
        linePaint_2.setStyle(Paint.Style.STROKE);
        linePaint_2.setStrokeWidth(5);

        dashLinePaint = new Paint();
        dashLinePaint.setColor(new Color().WHITE);
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        dashLinePaint.setPathEffect(effect);
        dashLinePaint.setStrokeWidth(3);
        dashLinePaint.setAntiAlias(true);
        dashLinePaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
        textPaint.setColor(Color.parseColor("#DCE4EC"));
        textPaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }


    //简单初始化下，后续改为由外部传入
    private void initLineItems() {

        listItems_1 = new ArrayList<>();
        listItems_2 = new ArrayList<>();

        for (int i = 0; i < itemSize; i++) {
            int left = MARGIN_LEFT_ITEM + i * ITEM_WIDTH;
            int right = left + ITEM_WIDTH - 1;
            firstItemX = (MARGIN_LEFT_ITEM + MARGIN_LEFT_ITEM + ITEM_WIDTH - 1) / 2;
            Point point_1 = calculateTempPoint(left, right, arrayConvert_1[i]);
            Point point_2 = calculateTempPoint(left, right, arrayConvert_2[i]);
            LineItem lineItem_1 = new LineItem();
            // lineItem_1.setName("新客户");
            lineItem_1.setTime(time[i]);
            lineItem_1.setValue(arrayConvert_1[i]);
            lineItem_1.setTempPoint(point_1);

            LineItem lineItem_2 = new LineItem();
            // lineItem_2.setName("老客户");
            lineItem_2.setTime(time[i]);
            lineItem_2.setValue(arrayConvert_2[i]);
            lineItem_2.setTempPoint(point_2);

            listItems_1.add(lineItem_1);
            listItems_2.add(lineItem_2);
        }
        System.out.println("----");
    }

    private Point calculateTempPoint(int left, int right, double temp) {
        double minHeight = tempBaseTop;
        double maxHeight = tempBaseBottom;
        double minY = minY_2 < minY_1 ? minY_2 : minY_1;
        double maxY = maxY_1 > maxY_2 ? maxY_1 : maxY_2;
        double tempY = maxHeight - (temp - minY) * 1.0 / (maxY - minY) * (maxHeight - minHeight);
        Point point = new Point((left + right) / 2, (int) tempY);
        return point;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (emptyData) {
            return;
        }

        if (isFirstLoad) {
            cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);//创建内存位图
            cacheCanvas = new Canvas(cacheBitmap);//创建绘图画布

            onDrawBackgrounRect(cacheCanvas);

            List<PointF> pointList_1 = new ArrayList<>();
            List<PointF> pointList_2 = new ArrayList<>();
            for (LineItem item : listItems_1) {
                pointList_1.add(new PointF(item.getTempPoint().x, item.getTempPoint().y));
            }
            for (LineItem item : listItems_2) {
                pointList_2.add(new PointF(item.getTempPoint().x, item.getTempPoint().y));
            }


            preparePoints(pointList_1);
            //TODO 画平滑的温度线 onDrawLine
            drawPoints(cacheCanvas, color1);
            preparePoints(pointList_2);
            //TODO 画平滑的温度线 onDrawLine
            drawPoints(cacheCanvas, color2);

            for (int i = 0; i < listItems_1.size(); i++) {
                //TODO 绘制底部时间  --必须先调用
                onDrawBottomTextAndHorizontalAxisLine(cacheCanvas, i);

                //TODO 温度圆点 onDrawTemp
                onDrawTempDot(cacheCanvas, i);


            }

            if (mZhugeIndexHorizontalScrollView != null) {
                Date date = new Date();
                String strDateFormat = "HH";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                String dxStr = sdf.format(date);
                int dx = -1;
                try {
                    dx = Integer.valueOf(dxStr);
                } catch (Exception e) {

                } finally {
                    if (dx != -1) {

                        int resultx = maxScrollOffset * listItems_1.get(dx).getTempPoint().x
                                / listItems_1.get(listItems_1.size() - 1).getTempPoint().x;

                        mZhugeIndexHorizontalScrollView.smoothScrollBy(resultx, 0);
                        // 这样没有触发滑动事件，不会导致下文“吸附”函数被调用
                        //所以这里我使用模拟滑动事件来代替  触发“吸附”函数被调用


                        long downTime = System.currentTimeMillis();
                        long eventTime = System.currentTimeMillis() + 100;
                        int x = 10;
                        int y = 10;
                        // List of meta states found here:
                        // developer.android.com/reference/android/view/KeyEvent.
                        // html#getMetaState(
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                                MotionEvent.ACTION_DOWN, x, y, metaState);






                        long downTime2 = System.currentTimeMillis();
                        long eventTime2 = System.currentTimeMillis() + 100;
                        int x2 = 10+resultx;
                        int y2 = 10;
                        // List of meta states found here:
                        // developer.android.com/reference/android/view/KeyEvent.
                        // html#getMetaState(
                        MotionEvent motionEvent2 = MotionEvent.obtain(downTime2, eventTime2,
                                MotionEvent.ACTION_UP, x2, y2, metaState);
                        mZhugeIndexHorizontalScrollView.dispatchTouchEvent(motionEvent2);


                    }
                }

            }


            isFirstLoad = false;
        }
        canvas.drawBitmap(cacheBitmap, 0, 0, new Paint());
        //TODO  绘制MarkView+Line
        onDrawMarkViewAndMarkLine(canvas);


    }

    ZhugeIndexHorizontalScrollView mZhugeIndexHorizontalScrollView;

    public void setHorizontalScrollParentView(ZhugeIndexHorizontalScrollView mZhugeIndexHorizontalScrollView) {
        this.mZhugeIndexHorizontalScrollView = mZhugeIndexHorizontalScrollView;
    }

    private void onDrawBackgrounRect(Canvas canvas) {
        double minHeight = tempBaseTop;
        double maxHeight = tempBaseBottom;
        double maxY = maxY_2 > maxY_1 ? maxY_2 : maxY_1;
        double minY = minY_2 > minY_1 ? minY_2 : minY_1;


        int left = MARGIN_LEFT_ITEM;
        int top = (int) (maxHeight - (maxY + 10 - minY) * 1.0 / (maxY - minY) * (maxHeight - minHeight));
        int right = mWidth - MARGIN_RIGHT_ITEM;
        int bottom = horizontalAxiLineEndY;
        textPaint.setColor(ContextCompat.getColor(context, R.color.cB3F1F5F9));
        canvas.drawRect(left, top, right, bottom, textPaint);
    }

    //绘制底部时间
    private void onDrawBottomTextAndHorizontalAxisLine(Canvas canvas, int i) {
        //此处的计算是为了文字能够居中


        // TODO 横轴


        String timeText = listItems_1.get(i).time;


        int strHalfWidth = (int) textPaint.measureText(timeText) / 2;
        textPaint.setColor(ContextCompat.getColor(context, R.color.cFF666666));
        canvas.drawText(timeText, listItems_1.get(i).getTempPoint().x - strHalfWidth, horizontalAxisTextEndY, textPaint);

        if (!isHorizontalAxisLineDrawed) {

            textPaint.setColor(ContextCompat.getColor(context, R.color.cDCE4EC));
            canvas.drawLine(MARGIN_LEFT_ITEM, horizontalAxiLineStartY, mWidth - MARGIN_RIGHT_ITEM, horizontalAxiLineEndY, textPaint);
            isHorizontalAxisLineDrawed = true;
            //画背景


        }


    }


    private Path mPath;

    public void preparePoints(List<PointF> pointFList) {
        pointFList.add(0, new PointF(pointFList.get(0).x, pointFList.get(0).y));
        pointFList.add(new PointF(pointFList.get(pointFList.size() - 1).x,
                pointFList.get(pointFList.size() - 1).y));
        pointFList.add(new PointF(pointFList.get(pointFList.size() - 1).x,
                pointFList.get(pointFList.size() - 1).y));

        mPath = new Path();
        mPath.moveTo(pointFList.get(0).x, pointFList.get(0).y);

        for (int i = 1; i < pointFList.size() - 3; i++) {
            PointF ctrlPointA = new PointF();
            PointF ctrlPointB = new PointF();
            getCtrlPoint(pointFList, i, ctrlPointA, ctrlPointB);
            mPath.cubicTo(ctrlPointA.x, ctrlPointA.y, ctrlPointB.x, ctrlPointB.y,
                    pointFList.get(i + 1).x, pointFList.get(i + 1).y);
        }
    }


    public void drawPoints(Canvas canvas, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        canvas.drawPath(mPath, paint);
    }

    private static final float CTRL_VALUE_A = 0.15f;
    private static final float CTRL_VALUE_B = 0.15f;

    private void getCtrlPoint(List<PointF> pointFList, int currentIndex,
                              PointF ctrlPointA, PointF ctrlPointB) {
        ctrlPointA.x = pointFList.get(currentIndex).x +
                (pointFList.get(currentIndex + 1).x - pointFList.get(currentIndex - 1).x) * CTRL_VALUE_A;
        ctrlPointA.y = pointFList.get(currentIndex).y +
                (pointFList.get(currentIndex + 1).y - pointFList.get(currentIndex - 1).y) * CTRL_VALUE_A;
        ctrlPointB.x = pointFList.get(currentIndex + 1).x -
                (pointFList.get(currentIndex + 2).x - pointFList.get(currentIndex).x) * CTRL_VALUE_B;
        ctrlPointB.y = pointFList.get(currentIndex + 1).y -
                (pointFList.get(currentIndex + 2).y - pointFList.get(currentIndex).y) * CTRL_VALUE_B;
    }

    private void onDrawTempDot(Canvas canvas, int i) {
        LineItem item_1 = listItems_1.get(i);
        LineItem item_2 = listItems_2.get(i);

        Point point_1 = item_1.tempPoint;
        Point point_2 = item_2.tempPoint;

        canvas.drawCircle(point_1.x, point_1.y, 5, pointPaint_1);
        canvas.drawCircle(point_2.x, point_2.y, 5, pointPaint_2);
        System.out.println(i + "-- x = " + point_1.x);
    }

    private void onDrawMarkViewAndMarkLine(Canvas canvas) {


        // if (currentItemIndex == i) {
        //计算提示文字的运动轨迹
        int Y = getTempBarY();


        System.out.println("-------markView---scale--" + SupportMultipleScreensUtil.scale);
        TextPaint textPaint = new TextPaint();
        textPaint.setARGB(0xFF, 0, 0, 0);
        textPaint.setTextSize((18 * SupportMultipleScreensUtil.scale));
        textPaint.setAntiAlias(true);


        int index = (int) (((double) getScrollBarX() / ITEM_WIDTH) + 0.2);
        if (index >= itemSize) {
            index = itemSize - 1;
        }


        StringBuilder sb = new StringBuilder(markTitles[0])
                .append(time[index])
                .append("\r\n")
                .append(markTitles[1])
                .append(String.format("%.2f", array_2[index]))
                .append("\r\n")
                .append(markTitles[2])
                .append((int) array_1[index]);
        String text = sb.toString();//"时间：2018/6/2  20:00\r\n实际营业额：111.33\r\n有效订单数：45";
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        int w = (int) (216 * SupportMultipleScreensUtil.scale);
        int h = (int) (131 * SupportMultipleScreensUtil.scale);


        int currentOffesetX = getScrollBarX() + firstItemX;
        if (currentOffesetX > (firstItemX + (itemSize - 1) * ITEM_WIDTH)) {
            currentOffesetX = (firstItemX + (itemSize - 1) * ITEM_WIDTH);
        }
        int left = currentOffesetX - (int) (w / 2);

        int right = left + w;
        int bottom = Y;
        int top = Y - h;

        Path path2 = new Path();
        int halfLen = (int) (20 * SupportMultipleScreensUtil.scale);
        int roundLen = (int) (6 * SupportMultipleScreensUtil.scale);
        top = top - halfLen;
        bottom = bottom - halfLen;
        int centerX = (left + right) / 2;
        int centerY = bottom + halfLen / 2;

        RectF rectF = new RectF(left,
                top,
                right,
                bottom);
        textPaint.setColor(ContextCompat.getColor(context, R.color.c2B5798));
        canvas.drawRoundRect(rectF, roundLen, roundLen, textPaint);


        path2.moveTo(centerX - halfLen / 2, bottom);
        path2.lineTo(centerX, centerY);
        path2.lineTo(centerX + halfLen / 2, bottom);
        path2.close();


        canvas.drawPath(path2, textPaint);//画三角形


        //绘制虚线

        DashPathEffect pathEffect = new DashPathEffect(new float[]{3, 3}, 1);
        Paint paint = new Paint();
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(ContextCompat.getColor(context, R.color.c2B5798));
        paint.setAntiAlias(true);
        paint.setPathEffect(pathEffect);
        Path path3 = new Path();
        path3.moveTo(centerX, horizontalAxiLineEndY);
        path3.lineTo(centerX, centerY - 2);
        canvas.drawPath(path3, paint);


        //绘制文字


        float spacingadd = 15 * SupportMultipleScreensUtil.scale;
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18 * SupportMultipleScreensUtil.scale);
        StaticLayout layout = new StaticLayout(text,
                textPaint, w,
                Layout.Alignment.ALIGN_NORMAL, 1.0F, spacingadd, false);


        canvas.save();
        int startX = left + (int) (13 * SupportMultipleScreensUtil.scale);
        int startY = top + (int) (18 * SupportMultipleScreensUtil.scale);
        canvas.translate(startX, startY);//从20，20开始画
        layout.draw(canvas);
        canvas.restore();//别忘了restore


    }


    //设置scrollerView的滚动条的位置，通过位置计算当前的时段
    public void setScrollOffset(int offset, int maxScrollOffset) {
        this.maxScrollOffset = maxScrollOffset;
        scrollOffset = offset;
        int index = calculateItemIndex(offset);
        currentItemIndex = index;
        invalidate();
    }

    //通过滚动条偏移量计算当前选择的时刻
    private int calculateItemIndex(int offset) {
//        Log.d(TAG, "maxScrollOffset = " + maxScrollOffset + "  scrollOffset = " + scrollOffset);
        int x = getScrollBarX();
        int sum = MARGIN_LEFT_ITEM - ITEM_WIDTH / 2;
        for (int i = 0; i < itemSize; i++) {
            sum += ITEM_WIDTH;
            if (x < sum)
                return i;
        }
        return itemSize - 1;
    }


    private int getScrollBarX() {


        //(itemSize - 1) * ITEM_WIDTH    游标View 可以滑动的最大位移
        //maxScrollOffset     HorizantolScrollView可以滑动的最大位移
        //scrollOffset    HorizantolScrollView如今已经滑动的距离
        //(itemSize - 1) * ITEM_WIDTH * scrollOffset / maxScrollOffset       游标View 如今已经滑动的距离

        int x = (itemSize - 1) * ITEM_WIDTH * scrollOffset / maxScrollOffset;//这关系如何得来的
        //x = x + MARGIN_LEFT_ITEM;
        System.out.println("--偏移 = " + x);
        return x;
    }


    /***
     * 停止滑动之后的吸附偏移
     */
    public int getStopAdsorbOffset() {

        double currentScrollX = getScrollBarX();
        double currentIndex = currentScrollX / ITEM_WIDTH;

        int fixIndex = (int) (currentIndex + 0.5);

        int fixX = fixIndex * ITEM_WIDTH;//不是这个fixIndex * ITEM_WIDTH + firstItemX;，MarkView从初始点 滑到这个位置  其实滑了fixindex个ITEM_WIDTH

        int parentFixscrollOffset = fixX * maxScrollOffset / ((itemSize - 1) * ITEM_WIDTH);


        int offset = parentFixscrollOffset - scrollOffset;


        return offset;
    }

    //计算温度提示文字的运动轨迹
    private int getTempBarY() {
        int currentScrolledX = getScrollBarX();//已经滑动的距离
        int index = currentScrolledX / ITEM_WIDTH;//当前Index,只会大于0
        System.out.println("***************" + index + "-----------" + (currentScrolledX % ITEM_WIDTH));

        //刚好到节点
        //节点附近+- 10

        if ((currentScrolledX % ITEM_WIDTH) == 0 || ITEM_WIDTH - (currentScrolledX % ITEM_WIDTH) < 10) {

            if ((currentScrolledX % ITEM_WIDTH) == 0 && index == 0) {
                LineItem item_1 = listItems_1.get(index);
                LineItem item_2 = listItems_2.get(index);
                int averageY_1 = item_1.getTempPoint().y;
                int averageY_2 = item_2.getTempPoint().y;
                System.out.println("*************** 1" + (averageY_1 < averageY_2 ? averageY_1 : averageY_2));
                return averageY_1 < averageY_2 ? averageY_1 : averageY_2;

            } else if (index + 1 < itemSize) {

                LineItem behindItem_1 = listItems_1.get(index + 1);
                LineItem behindItem_2 = listItems_2.get(index + 1);

                int averageY_1 = behindItem_1.getTempPoint().y;

                int averageY_2 = behindItem_2.getTempPoint().y;

                System.out.println("*************** 2" + (averageY_1 < averageY_2 ? averageY_1 : averageY_2));
                return averageY_1 < averageY_2 ? averageY_1 : averageY_2;
            } else {
                LineItem item_1 = listItems_1.get(index);
                LineItem item_2 = listItems_2.get(index);
                int averageY_1 = item_1.getTempPoint().y;
                int averageY_2 = item_2.getTempPoint().y;
                System.out.println("*************** 3" + (averageY_1 < averageY_2 ? averageY_1 : averageY_2));
                return averageY_1 < averageY_2 ? averageY_1 : averageY_2;
            }
        }

        if (index + 1 < itemSize) {
            LineItem preItem_1 = listItems_1.get(index);
            LineItem behindItem_1 = listItems_1.get(index + 1);

            LineItem preItem_2 = listItems_2.get(index);
            LineItem behindItem_2 = listItems_2.get(index + 1);

            int y1 = behindItem_1.getTempPoint().y - ((behindItem_1.getTempPoint().x - getScrollBarX() - firstItemX) *
                    (behindItem_1.getTempPoint().y - preItem_1.getTempPoint().y) / (behindItem_1.getTempPoint().x - preItem_1.getTempPoint().x));

            int y2 = behindItem_2.getTempPoint().y - ((behindItem_2.getTempPoint().x - getScrollBarX() - firstItemX) *


                    (behindItem_2.getTempPoint().y - preItem_2.getTempPoint().y) / (behindItem_2.getTempPoint().x - preItem_2.getTempPoint().x));
            System.out.println("***************4" + (y1 < y2 ? y1 : y2));
            return y1 < y2 ? y1 : y2;

        } else {
            if (index >= itemSize) {
                index = itemSize - 1;
            }
            LineItem item_1 = listItems_1.get(index);
            LineItem item_2 = listItems_2.get(index);
            int averageY_1 = item_1.getTempPoint().y;
            int averageY_2 = item_2.getTempPoint().y;
            System.out.println("*************** 5" + (averageY_1 < averageY_2 ? averageY_1 : averageY_2));
            return averageY_1 < averageY_2 ? averageY_1 : averageY_2;
        }

    }
}
