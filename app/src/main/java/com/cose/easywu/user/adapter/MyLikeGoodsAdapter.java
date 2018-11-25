package com.cose.easywu.user.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyLikeGoodsAdapter extends RecyclerView.Adapter<MyLikeGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<LikeGoods> likeGoodsList;
    private List<LikeGoods> removeGoodsList;
    private OnLikeClick onLikeClick;

    public static final String GOODS_BEAN = "goodsBean";

    public MyLikeGoodsAdapter(Context context) {
        mContext = context;
        likeGoodsList = LitePal.findAll(LikeGoods.class);
        removeGoodsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyLikeGoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mylikegoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyLikeGoodsAdapter.ViewHolder holder, final int position) {
        final LikeGoods goods = likeGoodsList.get(position);
        holder.tvUserNick.setText(goods.getG_u_nick());
        holder.tvUserUpdateTime.setText(DateUtil.getDatePoor(goods.getG_updateTime(), new Date()) + "来过");
        holder.tvPrice.setText(String.valueOf(goods.getG_price()));
        holder.tvGoodsName.setText(goods.getG_name());
        Glide.with(mContext).load(Constant.BASE_PHOTO_URL + goods.getG_u_photo()).apply(new RequestOptions()
                .placeholder(R.drawable.nav_icon)).into(holder.ivUserPhoto);
        Glide.with(mContext).load(goods.getG_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male)
                .into(holder.ivUserSex);
        Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic)).into(holder.ivPic1);
        if (goods.getG_pic2() != null) {
            Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic2()).apply(new RequestOptions()
                    .placeholder(R.drawable.ic_loading_pic)).into(holder.ivPic2);
        } else {
            holder.ivPic2.setVisibility(View.GONE);
        }
        if (goods.getG_pic3() != null) {
            Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic3()).apply(new RequestOptions()
                    .placeholder(R.drawable.ic_loading_pic)).into(holder.ivPic3);
        } else {
            holder.ivPic3.setVisibility(View.GONE);
        }
        holder.llLike.setVisibility(View.VISIBLE);
        holder.llDislike.setVisibility(View.GONE);
        holder.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClick != null) {
                    goods.delete();
                    removeGoodsList.add(goods);
                    onLikeClick.click(v, holder.llDislike, false, goods.getG_id());
                }
            }
        });
        holder.llDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClick != null) {
                    goods.save();
                    removeGoodsList.remove(goods);
                    onLikeClick.click(v, holder.llLike, true, goods.getG_id());
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
        return likeGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto, ivUserSex, ivPic1, ivPic2, ivPic3, ivLike;
        TextView tvUserNick, tvUserUpdateTime, tvPrice, tvGoodsName, tvLike;
        LinearLayout llMain, llLike, llDislike;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivUserPhoto = view.findViewById(R.id.iv_mylikegoods_user_photo);
            ivUserSex = view.findViewById(R.id.iv_mylikegoods_user_sex);
            ivPic1 = view.findViewById(R.id.iv_mylikegoods_pic1);
            ivPic2 = view.findViewById(R.id.iv_mylikegoods_pic2);
            ivPic3 = view.findViewById(R.id.iv_mylikegoods_pic3);
            ivLike = view.findViewById(R.id.iv_mylikegoods_like);
            tvUserNick = view.findViewById(R.id.tv_mylikegoods_user_nick);
            tvUserUpdateTime = view.findViewById(R.id.tv_mylikegoods_user_updateTime);
            tvPrice = view.findViewById(R.id.tv_mylikegoods_price);
            tvGoodsName = view.findViewById(R.id.tv_mylikegoods_name);
            tvLike = view.findViewById(R.id.tv_mylikegoods_like);
            llMain = view.findViewById(R.id.ll_mylikegoods_main);
            llLike = view.findViewById(R.id.ll_mylikegoods_like);
            llDislike = view.findViewById(R.id.ll_mylikegoods_dislike);
        }
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(LikeGoods likeGoods) {
        HomeDataBean.NewestInfoBean goods = new HomeDataBean.NewestInfoBean(likeGoods.getG_id(),
                likeGoods.getG_name(), likeGoods.getG_desc(), likeGoods.getG_price(),
                likeGoods.getG_originalPrice(), likeGoods.getG_pic1(), likeGoods.getG_pic2(),
                likeGoods.getG_pic3(), likeGoods.getG_state(), likeGoods.getG_like(),
                likeGoods.getG_updateTime(), likeGoods.getG_u_id(), likeGoods.getG_u_nick(),
                likeGoods.getG_u_photo(), likeGoods.getG_u_sex());
        Intent intent = new Intent(mContext, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    public void setLikeGoodsList(List<LikeGoods> likeGoodsList) {
        this.likeGoodsList = likeGoodsList;
    }

    public List<LikeGoods> getRemoveGoodsList() {
        return removeGoodsList;
    }

    public void setOnLikeClick(OnLikeClick onLikeClick) {
        this.onLikeClick = onLikeClick;
    }

    public interface OnLikeClick {
        void click(View view, LinearLayout linearLayout, boolean like, String g_id);
    }

}
