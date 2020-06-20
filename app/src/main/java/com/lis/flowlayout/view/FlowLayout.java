package com.lis.flowlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    List<View> lineViews; //一行中的view
    List<List<View>> views;   //总行
    List<Integer> lineHeights; //每行的高度

    private void init() {
        lineViews = new ArrayList<>();
        views = new ArrayList<>();
        lineHeights = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //记录当前行的宽度和高度
        int curWidth = 0;//当前行view宽度之行
        int curHeight = 0;//当前行View高度的最大值

        //整个流式布局的宽度和高度
        int flowLayoutWidth = 0; //所有行中宽度的最大值
        int flowLayoutHeight = 0;//所有行中高度的累加
        //初始化
        init();
        int itemCount = getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View view = getChildAt(i);

            //1
            // ViewGroup.LayoutParams lp = view.getLayoutParams();
            // int widthSpec = getChildMeasureSpec(widthMeasureSpec,0,lp.width);
            // int heightSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);
            // view.measure(widthSpec, heightSpec);
            //2 测量子View
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            //3
            // measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
            //获得子View宽高
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            //需要换行时
            if (childWidth + curWidth > widthSize) {
                views.add(lineViews);
                lineViews = new ArrayList<>();//创建新一行，不能用clear()
                //取所有行中最宽的一行
                flowLayoutWidth = Math.max(flowLayoutWidth, curWidth);
                //行高相加
                flowLayoutHeight += curHeight;
                //这里添加一行最高度
                lineHeights.add(curHeight);
                curWidth = 0;
                curHeight = 0;
            }
            lineViews.add(view);
            //宽度相加
            curWidth += childWidth;
            //取最高的一行高
            curHeight = Math.max(curHeight, childHeight);


        }
        //FlowLayout最终宽高
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : flowLayoutWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : flowLayoutHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int lineCount = views.size();
        int curX = 0;
        int curY = 0;
        //一行一行的布局
        for (int i = 0; i < lineCount; i++) {
            //取出一行的View
            List<View> lineViews = views.get(i);
            int lineHeight = lineHeights.get(i);
            int childCount = lineViews.size();
            //每行的View
            for (int i1 = 0; i1 < childCount; i1++) {
                int left = 0, top = 0, right = 0, bottom = 0;
                View view = lineViews.get(i1);
                left = curX;
                top = curY;
                right = left + view.getMeasuredWidth();
                bottom = top + view.getMeasuredHeight();
                view.layout(left, top, right, bottom);
                //确定下一个view的left
                curX += view.getMeasuredWidth();
            }
            //重置
            curX = 0;
            // 高度累加，确定下行的top
            curY += lineHeight;
        }

    }
}
