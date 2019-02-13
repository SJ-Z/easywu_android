package com.cose.easywu.message.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.db.Notification;
import com.cose.easywu.find.activity.FindGoodsInfoActivity;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.user.activity.MySellActivity;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.NotificationHelper;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import java.util.Date;
import java.util.List;

public class SystemMsgAdapter extends RecyclerView.Adapter<SystemMsgAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotificationList;
    private NoMsgListener noMsgListener;

    public SystemMsgAdapter(List<Notification> mNotificationList, NoMsgListener noMsgListener) {
        this.mNotificationList = mNotificationList;
        this.noMsgListener = noMsgListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.system_msg_style, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Notification notification = mNotificationList.get(position);
        Date time = notification.getTime();
        holder.mTvTime.setText(DateUtil.getShowTime(time));
        int type = notification.getType();
        if (type == NotificationHelper.TYPE_GOODS_COMMENT || type == NotificationHelper.TYPE_GOODS_REPLY ||
                type == NotificationHelper.TYPE_CONFIRM_ORDER_GOODS ||
                type == NotificationHelper.TYPE_REFUSE_ORDER_GOODS) {
            holder.mTvTitle.setText(NotificationHelper.GOODS);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_goodsmarket);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mTvTitle.setCompoundDrawables(drawable, null, null, null);
            // 设置条目的点击事件为跳转到商品详情页
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                    intent.putExtra(GoodsMessageHelper.CHATTYPE, true);
                    intent.putExtra(GoodsMessageHelper.GOODS_ID, notification.getG_id());
                    mContext.startActivity(intent);
                }
            });
        } else if (type == NotificationHelper.TYPE_NEW_ORDER_GOODS) {
            // 设置条目的点击事件为跳转到“我卖出的”
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MySellActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (type == NotificationHelper.TYPE_FIND_GOODS_COMMENT || type == NotificationHelper.TYPE_FIND_GOODS_REPLY) {
            holder.mTvTitle.setText(NotificationHelper.FIND_GOODS);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_lost_and_found);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mTvTitle.setCompoundDrawables(drawable, null, null, null);
            // 设置条目的点击事件为跳转到寻物启示详情页
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
                    intent.putExtra(GoodsMessageHelper.CHATTYPE, true);
                    intent.putExtra(GoodsMessageHelper.GOODS_ID, notification.getG_id());
                    intent.putExtra("isFindGoods", true);
                    mContext.startActivity(intent);
                }
            });
        } else if (type == NotificationHelper.TYPE_FIND_PEOPLE_COMMENT || type == NotificationHelper.TYPE_FIND_PEOPLE_REPLY) {
            holder.mTvTitle.setText(NotificationHelper.FIND_PEOPLE);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_lost_and_found);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mTvTitle.setCompoundDrawables(drawable, null, null, null);
            // 设置条目的点击事件为跳转到失物招领详情页
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
                    intent.putExtra(GoodsMessageHelper.CHATTYPE, true);
                    intent.putExtra(GoodsMessageHelper.GOODS_ID, notification.getG_id());
                    intent.putExtra("isFindGoods", false);
                    mContext.startActivity(intent);
                }
            });
        }
        holder.mTvContent.setText(notification.getContent());
        holder.mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.delete();
                mNotificationList.remove(position);
                notifyDataSetChanged();
                if (mNotificationList.size() == 0) {
                    noMsgListener.onNoMsg();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationList == null ? 0 : mNotificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTime, mTvTitle, mTvContent, mTvDelete;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_system_msg_time);
            mTvTitle = itemView.findViewById(R.id.tv_system_msg_title);
            mTvContent = itemView.findViewById(R.id.tv_system_msg_content);
            mTvDelete = itemView.findViewById(R.id.tv_system_msg_delete);
            cardView = itemView.findViewById(R.id.cardview_system_msg);
        }
    }

    public interface NoMsgListener {
        void onNoMsg();
    }
}
