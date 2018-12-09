package com.cose.easywu.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class NestedExpandableListView extends ExpandableListView {

    public NestedExpandableListView(Context context) {
        super(context);
    }

    public NestedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = heightMeasureSpec;

        //when height is wrap_content
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
