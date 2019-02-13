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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.db.ReleaseFindGoods;
import com.cose.easywu.find.activity.FindGoodsInfoActivity;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.utils.Constant;

import org.litepal.LitePal;

import java.util.List;

import static com.cose.easywu.find.adapter.FindFragmentAdapter.GOODS_BEAN;

public class MyClosedFindGoodsAdapter extends RecyclerView.Adapter<MyClosedFindGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<ReleaseFindGoods> closedGoodsList;
    private String u_id;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private OnReleaseClick onReleaseClick;
    private OnDeleteClick onDeleteClick;

    public MyClosedFindGoodsAdapter(Context context) {
        mContext = context;
        closedGoodsList = LitePal.where("fg_state!=? and fg_state!=?", "0", "5")
                .order("fg_updateTime desc").find(ReleaseFindGoods.class);
        u_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("u_id", "");
        com.cose.easywu.db.User user = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        u_nick = user.getU_nick();
        u_photo = user.getU_photo();
        u_sex = user.getU_sex();
    }

    @NonNull
    @Override
    public MyClosedFindGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_myclosedgoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyClosedFindGoodsAdapter.ViewHolder holder, final int position) {
        final ReleaseFindGoods goods = closedGoodsList.get(position);
        holder.tvGoodsName.setText(goods.getFg_name());
        if (goods.getFg_state() == 2) {
            holder.tvReason.setText("被您关闭");
        } else if (goods.getFg_state() == 3) {
            holder.tvReason.setText("被管理员关闭");
            holder.tvRelease.setVisibility(View.GONE); // 被管理员关闭的寻物启示不可重新发布
        }
        Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1()).apply(new RequestOptions()
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
        return closedGoodsList == null ? 0 : closedGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsPic;
        TextView tvReason, tvDescReason, tvGoodsName, tvRelease, tvDelete;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivGoodsPic = view.findViewById(R.id.iv_myclosedgoods_item_pic);
            tvReason = view.findViewById(R.id.tv_myclosedgoods_item_reason);
            tvDescReason = view.findViewById(R.id.tv_myclosedgoods_item_desc_reason);
            tvGoodsName = view.findViewById(R.id.tv_myclosedgoods_item_name);
            view.findViewById(R.id.ll_myclosedgoods_item_price).setVisibility(View.GONE);
            tvRelease = view.findViewById(R.id.tv_myclosedgoods_item_release);
            tvDelete = view.findViewById(R.id.tv_myclosedgoods_item_delete);
            llMain = view.findViewById(R.id.ll_myclosedgoods_item_main);
        }
    }

    // 启动失物招领详情页面
    private void startGoodsInfoActivity(ReleaseFindGoods releaseGoods) {
        FindDataBean.FindNewestInfo goods = new FindDataBean.FindNewestInfo(releaseGoods.getFg_id(),
                releaseGoods.getFg_name(), releaseGoods.getFg_desc(), releaseGoods.getFg_pic1(),
                releaseGoods.getFg_pic2(), releaseGoods.getFg_pic3(), releaseGoods.getFg_state(),
                releaseGoods.getFg_like(), releaseGoods.getFg_updateTime(), releaseGoods.getFg_ft_id(),
                u_id, u_nick, u_photo, u_sex);
        Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        intent.putExtra("isFindGoods", true);
        mContext.startActivity(intent);
    }

    public void setClosedGoodsList(List<ReleaseFindGoods> closedGoodsList) {
        this.closedGoodsList = closedGoodsList;
    }

    public void setOnDeleteClick(OnDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public void setOnReleaseClick(OnReleaseClick onReleaseClick) {
        this.onReleaseClick = onReleaseClick;
    }

    public interface OnDeleteClick {
        void click(ReleaseFindGoods goods);
    }

    public interface OnReleaseClick {
        void click(ReleaseFindGoods goods);
    }

}
