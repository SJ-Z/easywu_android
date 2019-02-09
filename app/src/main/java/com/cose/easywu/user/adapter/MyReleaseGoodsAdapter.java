package com.cose.easywu.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.cose.easywu.widget.MessageDialog;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyReleaseGoodsAdapter extends RecyclerView.Adapter<MyReleaseGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<ReleaseGoods> releaseGoodsList;
    private String u_id;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private OnPolishClick onPolishClick;
    private OnDeleteClick onDeleteClick;
    private OnEditClick onEditClick;

    public static final String GOODS_BEAN = "goodsBean";

    public MyReleaseGoodsAdapter(Context context) {
        mContext = context;
        releaseGoodsList = LitePal.where("g_state=?", "0").order("g_updateTime desc").find(ReleaseGoods.class);
        u_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("u_id", "");
        com.cose.easywu.db.User user = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        u_nick = user.getU_nick();
        u_photo = user.getU_photo();
        u_sex = user.getU_sex();
    }

    @NonNull
    @Override
    public MyReleaseGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_myreleasegoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyReleaseGoodsAdapter.ViewHolder holder, final int position) {
        final ReleaseGoods goods = releaseGoodsList.get(position);
        holder.tvGoodsName.setText(goods.getG_name());
        holder.tvPrice.setText(String.valueOf(goods.getG_price()));
        holder.tvUpdateTime.setText(DateUtil.getDatePoor(goods.getG_updateTime(), new Date()) + "擦亮");
        Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivGoodsPic);

        if (DateUtil.isToday(goods.getG_updateTime())) { // 上一次更新日期为今天
            holder.tvPolish.setText("已擦亮");
            holder.tvPolish.setTextColor(mContext.getResources().getColor(R.color.colorHint));
        }
        holder.tvPolish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvPolish.getText().equals("擦亮")) {
                    if (onPolishClick != null) {
                        onPolishClick.click(holder.tvPolish, holder.tvUpdateTime, goods);
                    }
                } else {
                    ToastUtil.showMsgOnCenter(mContext, "一天只能擦亮一次", Toast.LENGTH_SHORT);
                }
            }
        });
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClick != null) {
                    onEditClick.click(goods);
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
    }

    @Override
    public int getItemCount() {
        return releaseGoodsList == null ? 0 : releaseGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsPic;
        TextView tvGoodsName, tvPrice, tvUpdateTime, tvPolish, tvEdit, tvDelete;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivGoodsPic = view.findViewById(R.id.iv_myreleasegoods_pic);
            tvGoodsName = view.findViewById(R.id.tv_myreleasegoods_name);
            tvPrice = view.findViewById(R.id.tv_myreleasegoods_price);
            tvUpdateTime = view.findViewById(R.id.tv_myreleasegoods_updateTime);
            tvPolish = view.findViewById(R.id.tv_myreleasegoods_polish);
            tvEdit = view.findViewById(R.id.tv_myreleasegoods_edit);
            tvDelete = view.findViewById(R.id.tv_myreleasegoods_delete);
            llMain = view.findViewById(R.id.ll_myreleasegoods_main);
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

    public void setReleaseGoodsList(List<ReleaseGoods> releaseGoodsList) {
        this.releaseGoodsList = releaseGoodsList;
    }

    public void setOnPolishClick(OnPolishClick onPolishClick) {
        this.onPolishClick = onPolishClick;
    }

    public void setOnDeleteClick(OnDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public void setOnEditClick(OnEditClick onEditClick) {
        this.onEditClick = onEditClick;
    }

    public interface OnPolishClick {
        void click(View view, TextView tvUpdateTime, ReleaseGoods goods);
    }

    public interface OnDeleteClick {
        void click(ReleaseGoods goods);
    }

    public interface OnEditClick {
        void click(ReleaseGoods goods);
    }

}
