package com.silang.managelinechart;

import android.graphics.Point;

/**
 * author: zhongxiaohong
 * date: 2018/6/2.
 * desc:
 */

public class LineItem {
    public String time; //时间点
  //  public String name; //名称
    public double value;//数值
    public Point tempPoint; //温度的点坐标

    public Point getTempPoint() {
        return tempPoint;
    }

    public void setTempPoint(Point tempPoint) {
        this.tempPoint = tempPoint;
    }

  /*  public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
