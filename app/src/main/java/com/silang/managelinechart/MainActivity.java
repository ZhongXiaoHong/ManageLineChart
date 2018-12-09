package com.silang.managelinechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ZhugeIndexHorizontalScrollView zhugeIndexHorizontalScrollView;
    LineCompareView lineCompareView;

    List<ItemsBean> baseList;
    List<ItemsBean> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseList = new ArrayList<>();
        arrayList = new ArrayList<>();
        for(int  i = 0 ; i < 24;i++){
            ItemsBean e = new ItemsBean();
            ItemsBean e2 = new ItemsBean();
            if(i<=9){
                e.setTime("0"+i+":00");
                e2.setTime("0"+i+":00");
            }else{
                e.setTime(i+":00");
                e2.setTime(i+":00");
            }
            Random mRandom = new Random();
            e.setValue(20+ mRandom.nextInt(10));
            e2.setValue(20+ mRandom.nextInt(10));

            baseList.add(e);
            arrayList.add(e2);
        }

        zhugeIndexHorizontalScrollView = findViewById(R.id.indexHorizontalScrollView);
         lineCompareView = findViewById(R.id.lineCompareView);
        lineCompareView.setHorizontalScrollParentView(zhugeIndexHorizontalScrollView);
        zhugeIndexHorizontalScrollView.setLineCompareView(lineCompareView);



        if (baseList != null && arrayList != null && arrayList.size() == baseList.size()) {
            double baseArray[] = new double[baseList.size()];
            double array[] = new double[baseList.size()];
            String[] time = new String[baseList.size()];

            for (int i = 0; i < baseList.size(); i++) {
                baseArray[i] = baseList.get(i).getValue();
                array[i] = arrayList.get(i).getValue();
                time[i] = baseList.get(i).getTime();
            }
            lineCompareView.setMarkTitles(new String[]{"时间：", "实际营业额：", "有效订单数："});
            lineCompareView.setDatas(baseArray, array, time);
        }

    }
}
