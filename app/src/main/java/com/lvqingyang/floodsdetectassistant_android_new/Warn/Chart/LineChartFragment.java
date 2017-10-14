package com.lvqingyang.floodsdetectassistant_android_new.Warn.Chart;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idtk.smallchart.chart.LineChart;
import com.idtk.smallchart.data.LineData;
import com.lvqingyang.floodsdetectassistant_android_new.R;

import java.util.ArrayList;


public class LineChartFragment extends Fragment {

    private LineData mLineData = new LineData();
    private ArrayList<PointF> mPointArrayList = new ArrayList<>();

    private double[] mData;
    private String XUnit="";
    private String YUnit="";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getArguments();
        mData = bundle.getDoubleArray("data");
        XUnit=bundle.getString("x_unit");
        YUnit=bundle.getString("y_unit");
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        LineChart mLineChart = (LineChart) view.findViewById(R.id.line_chart);

        initData();

        mLineChart.isAnimated=false;
        mLineChart.setData(mLineData);
        mLineChart.setXAxisUnit(XUnit);
        mLineChart.setYAxisUnit(YUnit);

        return view;
    }

    private void initData() {
        //测试数据
        for (int i = 0; i < 32; i++) {
            mPointArrayList.add(new PointF(i,(float)(Math.abs(mData[i])+0.01)));
        }

        mLineData.setValue(mPointArrayList);
        mLineData.setColor(Color.MAGENTA);
        mLineData.setPaintWidth(2);
        mLineData.setTextSize(20);
    }


}
