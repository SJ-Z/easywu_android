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
import com.cose.easywu.db.ReleaseFindPeople;
import com.cose.easywu.find.activity.FindGoodsInfoActivity;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.ToastUtil;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

import static com.cose.easywu.find.adapter.FindFragmentAdapter.GOODS_BEAN;

public class MyReleaseFindPeopleAdapter extends RecyclerView.Adapter<MyReleaseFindPeopleAdapter.ViewHolder> {

    private Context mContext;
    private List<ReleaseFindPeople> goodsList;
    private String u_id;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private OnPolishClick onPolishClick;
    private OnDeleteClick onDeleteClick;
    private OnEditClick onEditClick;

    public MyReleaseFindPeopleAdapter(Context context) {
        mContext = context;
        goodsList = LitePal.where("fg_state=?", "0").order("fg_updateTime desc").find(ReleaseFindPeople.class);
        u_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("u_id", "");
        com.cose.easywu.db.User user = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        u_nick = user.getU_nick();
        u_photo = user.getU_photo();
        u_sex = user.getU_sex();
    }

    @NonNull
    @Override
    public MyReleaseFindPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_myreleasegoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyReleaseFindPeopleAdapter.ViewHolder holder, final int position) {
        final ReleaseFindPeople goods = goodsList.get(position);
        holder.tvGoodsName.setText(goods.getFg_name());
        holder.tvUpdateTime.setText(DateUtil.getDatePoor(goods.getFg_updateTime(), new Date()) + "擦亮");
        Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivGoodsPic);

        if (DateUtil.isToday(goods.getFg_updateTime())) { // 上一次更新日期为今天
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
        return goodsList == null ? 0 : goodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsPic;
        TextView tvGoodsName, tvUpdateTime, tvPolish, tvEdit, tvDelete;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivGoodsPic = view.findViewById(R.id.iv_myreleasegoods_pic);
            tvGoodsName = view.findViewById(R.id.tv_myreleasegoods_name);
            view.findViewById(R.id.ll_myreleasegoods_price).setVisibility(View.GONE);
            tvUpdateTime = view.findViewById(R.id.tv_myreleasegoods_updateTime);
            tvPolish = view.findViewById(R.id.tv_myreleasegoods_polish);
            tvEdit = view.findViewById(R.id.tv_myreleasegoods_edit);
            tvDelete = view.findViewById(R.id.tv_myreleasegoods_delete);
            llMain = view.findViewById(R.id.ll_myreleasegoods_main);
        }
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(ReleaseFindPeople releaseGoods) {
        FindDataBean.FindNewestInfo goods = new FindDataBean.FindNewestInfo(releaseGoods.getFg_id(),
                releaseGoods.getFg_name(), releaseGoods.getFg_desc(), releaseGoods.getFg_pic1(),
                releaseGoods.getFg_pic2(), releaseGoods.getFg_pic3(), releaseGoods.getFg_state(),
                releaseGoods.getFg_like(), releaseGoods.getFg_updateTime(), releaseGoods.getFg_ft_id(),
                u_id, u_nick, u_photo, u_sex);
        Intent intent = new Intent(mContext, FindGoodsInfoActivity.class);
        intent.putExtra("isFindGoods", false);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    public void setGoodsList(List<ReleaseFindPeople> goodsList) {
        this.goodsList = goodsList;
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
        void click(View view, TextView tvUpdateTime, ReleaseFindPeople goods);
    }

    public interface OnDeleteClick {
        void click(ReleaseFindPeople goods);
    }

    public interface OnEditClick {
        void click(ReleaseFindPeople goods);
    }

}
