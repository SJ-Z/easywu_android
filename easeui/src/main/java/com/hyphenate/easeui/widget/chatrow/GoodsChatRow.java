package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.GoodsMessageHelper;
import com.hyphenate.exceptions.HyphenateException;

public class GoodsChatRow extends EaseChatRow {

    protected ImageView iv_pic;
    protected TextView tv_name;
    protected TextView tv_price;

    public GoodsChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.goods_chat_row_received_message : R.layout.goods_chat_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        iv_pic = findViewById(R.id.iv_goods_chat_row);
        tv_name = findViewById(R.id.tv_goods_chat_row_name);
        tv_price = findViewById(R.id.tv_goods_chat_row_price);
    }

    @Override
    protected void onSetUpView() {
        try {
            if (message.getStringAttribute("CHATTYPE").equals(GoodsMessageHelper.CHATTYPE)) {
                tv_name.setText(message.getStringAttribute(GoodsMessageHelper.GOODS_NAME));
                String price = message.getStringAttribute(GoodsMessageHelper.GOODS_PRICE);
                if (TextUtils.isEmpty(price)) {
                    tv_price.setVisibility(GONE);
                    findViewById(R.id.tv_goods_chat_row_price_symbol).setVisibility(GONE);
                } else {
                    tv_price.setText(price);
                }
                Glide.with(context).load(message.getStringAttribute(GoodsMessageHelper.GOODS_PIC)).into(iv_pic);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {

    }
}
