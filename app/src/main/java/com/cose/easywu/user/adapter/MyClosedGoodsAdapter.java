package com.cose.easywu.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.ToastUtil;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class MyClosedGoodsAdapter extends RecyclerView.Adapter<MyClosedGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<ReleaseGoods> closedGoodsList;
    private String u_id;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private OnReleaseClick onReleaseClick;
    private OnDeleteClick onDeleteClick;

    public static final String GOODS_BEAN = "goodsBean";

    public MyClosedGoodsAdapter(Context context) {
        mContext = context;
        closedGoodsList = LitePal.where("g_state!=? and g_state!=?", "0", "5").order("g_updateTime desc").find(ReleaseGoods.class);
        u_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("u_id", "");
        com.cose.easywu.db.User user = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        u_nick = user.getU_nick();
        u_photo = user.getU_photo();
        u_sex = user.getU_sex();
    }

    @NonNull
    @Override
    public MyClosedGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_myclosedgoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyClosedGoodsAdapter.ViewHolder holder, final int position) {
        final ReleaseGoods goods = closedGoodsList.get(position);
        holder.tvGoodsName.setText(goods.getG_name());
        holder.tvPrice.setText(String.valueOf(goods.getG_price()));
        if (goods.getG_state() == 1) {
            holder.tvReason.setText("售出下架");
        } else if (goods.getG_state() == 2) {
            holder.tvReason.setText("被您下架");
        } else if (goods.getG_state() == 3) {
            holder.tvReason.setText("被管理员下架");
            holder.tvRelease.setVisibility(View.GONE); // 被管理员下架的商品不可重新发布
        }
        Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivGoodsPic);

        holder.tvRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onReleaseClick != null) {
                    onReleaseClick.click(goods);
                }
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClick != null) {
                    onDeleteClick.click(goods);
                }
            }
        });
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoodsInfoActivity(goods);
            }
        });
        holder.tvDescReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return closedGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsPic;
        TextView tvReason, tvDescReason, tvGoodsName, tvPrice, tvRelease, tvDelete;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivGoodsPic = view.findViewById(R.id.iv_myclosedgoods_item_pic);
            tvReason = view.findViewById(R.id.tv_myclosedgoods_item_reason);
            tvDescReason = view.findViewById(R.id.tv_myclosedgoods_item_desc_reason);
            tvGoodsName = view.findViewById(R.id.tv_myclosedgoods_item_name);
            tvPrice = view.findViewById(R.id.tv_myclosedgoods_item_price);
            tvRelease = view.findViewById(R.id.tv_myclosedgoods_item_release);
            tvDelete = view.findViewById(R.id.tv_myclosedgoods_item_delete);
            llMain = view.findViewById(R.id.ll_myclosedgoods_item_main);
        }
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(ReleaseGoods releaseGoods) {
        HomeDataBean.NewestInfoBean goods = new HomeDataBean.NewestInfoBean(releaseGoods.getG_id(),
                releaseGoods.getG_name(), releaseGoods.getG_desc(), releaseGoods.getG_price(),
                releaseGoods.getG_originalPrice(), releaseGoods.getG_pic1(), releaseGoods.getG_pic2(),
                releaseGoods.getG_pic3(), releaseGoods.getG_state(), releaseGoods.getG_like(),
                releaseGoods.getG_updateTime(), releaseGoods.getG_t_id(), u_id, u_nick, u_photo, u_sex);
        Intent intent = new Intent(mContext, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    public void setClosedGoodsList(List<ReleaseGoods> closedGoodsList) {
        this.closedGoodsList = closedGoodsList;
    }

    public void setOnDeleteClick(OnDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public void setOnReleaseClick(OnReleaseClick onReleaseClick) {
        this.onReleaseClick = onReleaseClick;
    }

    public interface OnDeleteClick {
        void click(ReleaseGoods goods);
    }

    public interface OnReleaseClick {
        void click(ReleaseGoods goods);
    }

}
