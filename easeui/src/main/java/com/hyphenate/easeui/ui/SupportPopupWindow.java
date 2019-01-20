package com.hyphenate.easeui.ui;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

public class SupportPopupWindow extends PopupWindow {

    public SupportPopupWindow(View contentView, int width, int height){
        super(contentView,width,height);
    }

    /**
     *
     * @param anchor v
     * @param xoff   x轴偏移
     * @param yoff   y轴偏移
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
            super.showAsDropDown(anchor, xoff, yoff);
        } else {
            super.showAsDropDown(anchor, xoff, yoff);
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        if(Build.VERSION.SDK_INT >= 24){
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }
        super.showAsDropDown(anchor);
    }
}
