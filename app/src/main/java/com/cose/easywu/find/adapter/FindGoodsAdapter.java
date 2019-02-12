package com.cose.easywu.find.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.find.activity.FindGoodsInfoActivity;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cose.easywu.find.adapter.FindFragmentAdapter.GOODS_BEAN;

public class FindGoodsAdapter extends RecyclerView.Adapter<FindGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<FindDataBean.FindNewestInfo> goodsList;
    private boolean isFindGoods;

    public FindGoodsAdapter(Context context, List<FindDataBean.FindNewestInfo> goodsList, boolean isFindGoods) {
        this.mContext = context;
        this.goodsList = goodsList;
        this.isFindGoods = isFindGoods;
    }

    public void setDatas(List<FindDataBean.FindNewestInfo> goodsList) {
        this.goodsList = goodsList;
    }

    public void addDatas(List<FindDataBean.FindNewestInfo> moregoodsList) {
        goodsList.addAll(moregoodsList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_newest_item_main;
        CircleImageView iv_newest_item_userPhoto;
        TextView tv_newest_item_userNick;
        TextView tv_newest_item_name;
        ImageView iv_newest_item_sex;
        ImageView iv_newest_item_pic1;
        ImageView iv_newest_item_pic2;
        ImageView iv_newest_item_pic3;
        TextView tv_newest_item_like;
        TextView tv_newest_item_updateTime;

        public ViewHolder(@NonNull View view) {
            super(view);
            ll_newest_item_main = view.findViewById(R.id.ll_newest_item_main);
            iv_newest_item_userPhoto = view.findViewById(R.id.iv_newest_item_userPhoto);
            tv_newest_item_userNick = view.findViewById(R.id.tv_newest_item_userNick);
            view.findViewById(R.id.tv_newest_item_price).setVisibility(View.GONE);
            tv_newest_item_name = view.findViewById(R.id.tv_newest_item_name);
            iv_newest_item_sex = view.findViewById(R.id.iv_newest_item_sex);
            iv_newest_item_pic1 = view.findViewById(R.id.iv_newest_item_pic1);
            iv_newest_item_pic2 = view.findViewById(R.id.iv_newest_item_pic2);
            iv_newest_item_pic3 = view.findViewById(R.id.iv_newest_item_pic3);
            tv_newest_item_like = view.findViewById(R.id.tv_newest_item_like);
            tv_newest_item_updateTime = view.findViewById(R.id.tv_newest_item_updateTime);
        }
    }

    @NonNull
    @Override
    public FindGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newest_grid_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindGoodsAdapter.ViewHolder holder, int position) {
        // 根据位置得到对应的数据
        final FindDataBean.FindNewestInfo goods = goodsList.get(position);
        Glide.with(mContext).load(Constant.BASE_PHOTO_URL + goods.getFg_u_photo())
                .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                .into(holder.iv_newest_item_userPhoto);
        holder.tv_newest_item_userNick.setText(goods.getFg_u_nick());
        holder.tv_newest_item_name.setText(goods.getFg_name());
        holder.iv_newest_item_sex.setImageResource(goods.getFg_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male);
        holder.tv_newest_item_like.setText(String.valueOf(goods.getFg_like()));
        holder.tv_newest_item_updateTime.setText(DateUtil.getDatePoor(goods.getFg_updateTime(), new Date()) + "擦亮");
        if (!TextUtils.isEmpty(goods.getFg_pic1())) {
            holder.iv_newest_item_pic1.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
                    .into(holder.iv_newest_item_pic1);
        } else {
            holder.iv_newest_item_pic1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(goods.getFg_pic2())) {
            holder.iv_newest_item_pic2.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic2())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
                    .into(holder.iv_newest_item_pic2);
            holder.iv_newest_item_pic3.setVisibility(View.GONE);
        } else {
            holder.iv_newest_item_pic2.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(goods.getFg_pic3())) {
            holder.iv_newest_item_pic3.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic3())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
                    .into(holder.iv_newest_item_pic3);
        } else {
            holder.iv_newest_item_pic3.setVisibility(View.GONE);
        }

        holder.ll_newest_item_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
                intent.putExtra("isFindGoods", isFindGoods);
                intent.putExtra(GOODS_BEAN, goods);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsList == null ? 0 : goodsList.size();
    }

}
