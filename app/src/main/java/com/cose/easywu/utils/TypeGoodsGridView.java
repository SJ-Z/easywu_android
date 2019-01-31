package com.cose.easywu.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class TypeGoodsGridView extends GridView {

    //嵌套在ScrollView中全展开，重写onMeasure导致adapter的getView方法反复执行
    //设置一个boolean变量，onMeasure时设为ture，onLayout时设为false
    public boolean isOnMeasure ;
    public TypeGoodsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public TypeGoodsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeGoodsGridView(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOnMeasure = true;
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,
                MeasureSpec.AT_MOST);
        //AT_MOST(表示子控件的高度能扩展多高就扩展多高，但要小于给出的size)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }

}
