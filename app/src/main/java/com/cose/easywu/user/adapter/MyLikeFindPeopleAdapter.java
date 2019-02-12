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
import com.cose.easywu.db.LikeFindPeople;
import com.cose.easywu.find.activity.FindGoodsInfoActivity;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cose.easywu.find.adapter.FindFragmentAdapter.GOODS_BEAN;

public class MyLikeFindPeopleAdapter extends RecyclerView.Adapter<MyLikeFindPeopleAdapter.ViewHolder> {

    private Context mContext;
    private List<LikeFindPeople> likeGoodsList;
    private ArrayList<LikeFindPeople> removeGoodsList;
    private OnLikeClick onLikeClick;

    public MyLikeFindPeopleAdapter(Context context) {
        mContext = context;
        likeGoodsList = LitePal.findAll(LikeFindPeople.class);
        removeGoodsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyLikeFindPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mylikegoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyLikeFindPeopleAdapter.ViewHolder holder, final int position) {
        final LikeFindPeople goods = likeGoodsList.get(position);
        holder.tvUserNick.setText(goods.getFg_u_nick());
        holder.tvUserUpdateTime.setText(DateUtil.getDatePoor(goods.getFg_updateTime(), new Date()) + "来过");
        holder.tvGoodsName.setText(goods.getFg_name());
        Glide.with(mContext).load(Constant.BASE_PHOTO_URL + goods.getFg_u_photo()).apply(new RequestOptions()
                .placeholder(R.drawable.nav_icon)).into(holder.ivUserPhoto);
        Glide.with(mContext).load(goods.getFg_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male)
                .into(holder.ivUserSex);
        Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivPic1);
        if (goods.getFg_pic2() != null) {
            Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic2()).apply(new RequestOptions()
                    .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivPic2);
        } else {
            holder.ivPic2.setVisibility(View.GONE);
        }
        if (goods.getFg_pic3() != null) {
            Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic3()).apply(new RequestOptions()
                    .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivPic3);
        } else {
            holder.ivPic3.setVisibility(View.GONE);
        }
        if (goods.getFg_state() != 0) {
            holder.tvState.setText("该内容已失效");
            holder.tvState.setVisibility(View.VISIBLE);
        }
        holder.llLike.setVisibility(View.VISIBLE);
        holder.llDislike.setVisibility(View.GONE);
        holder.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClick != null) {
                    goods.delete();
                    removeGoodsList.add(goods);
                    onLikeClick.click(v, holder.llDislike, false, goods.getFg_id());
                }
            }
        });
        holder.llDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClick != null) {
                    goods.save();
                    removeGoodsList.remove(goods);
                    onLikeClick.click(v, holder.llLike, true, goods.getFg_id());
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
        return likeGoodsList == null ? 0 : likeGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto, ivUserSex, ivPic1, ivPic2, ivPic3, ivLike;
        TextView tvUserNick, tvUserUpdateTime, tvGoodsName, tvLike, tvState;
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
            view.findViewById(R.id.ll_mylikegoods_price).setVisibility(View.GONE);
            tvGoodsName = view.findViewById(R.id.tv_mylikegoods_name);
            tvLike = view.findViewById(R.id.tv_mylikegoods_like);
            tvState = view.findViewById(R.id.tv_mylikegoods_state);
            llMain = view.findViewById(R.id.ll_mylikegoods_main);
            llLike = view.findViewById(R.id.ll_mylikegoods_like);
            llDislike = view.findViewById(R.id.ll_mylikegoods_dislike);
        }
    }

    // 启动失物招领详情页面
    private void startGoodsInfoActivity(LikeFindPeople likeGoods) {
        FindDataBean.FindNewestInfo goods = new FindDataBean.FindNewestInfo(likeGoods.getFg_id(),
                likeGoods.getFg_name(), likeGoods.getFg_desc(), likeGoods.getFg_pic1(),
                likeGoods.getFg_pic2(), likeGoods.getFg_pic3(), likeGoods.getFg_state(),
                likeGoods.getFg_like(), likeGoods.getFg_updateTime(), likeGoods.getFg_ft_id(),
                likeGoods.getFg_u_id(), likeGoods.getFg_u_nick(), likeGoods.getFg_u_photo(),
                likeGoods.getFg_u_sex());
        Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        intent.putExtra("isFindGoods", false);
        mContext.startActivity(intent);
    }

    public void setLikeGoodsList(List<LikeFindPeople> likeGoodsList) {
        this.likeGoodsList = likeGoodsList;
    }

    public List<LikeFindPeople> getRemoveGoodsList() {
        return removeGoodsList;
    }

    public void setOnLikeClick(OnLikeClick onLikeClick) {
        this.onLikeClick = onLikeClick;
    }

    public interface OnLikeClick {
        void click(View view, LinearLayout linearLayout, boolean like, String fg_id);
    }

}
