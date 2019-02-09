package com.cose.easywu.find.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindNewestGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<FindDataBean.FindNewestInfo> datas;

    public FindNewestGridViewAdapter(Context mContext, List<FindDataBean.FindNewestInfo> newest_info) {
        this.mContext = mContext;
        this.datas = newest_info;
    }

    public void setDatas(List<FindDataBean.FindNewestInfo> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_newest_grid_view, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_newest_item_userPhoto = convertView.findViewById(R.id.iv_newest_item_userPhoto);
            viewHolder.tv_newest_item_userNick = convertView.findViewById(R.id.tv_newest_item_userNick);
            viewHolder.tv_newest_item_price = convertView.findViewById(R.id.tv_newest_item_price);
            viewHolder.tv_newest_item_name = convertView.findViewById(R.id.tv_newest_item_name);
            viewHolder.iv_newest_item_sex = convertView.findViewById(R.id.iv_newest_item_sex);
            viewHolder.iv_newest_item_pic1 = convertView.findViewById(R.id.iv_newest_item_pic1);
            viewHolder.iv_newest_item_pic2 = convertView.findViewById(R.id.iv_newest_item_pic2);
            viewHolder.iv_newest_item_pic3 = convertView.findViewById(R.id.iv_newest_item_pic3);
            viewHolder.tv_newest_item_like = convertView.findViewById(R.id.tv_newest_item_like);
            viewHolder.tv_newest_item_updateTime = convertView.findViewById(R.id.tv_newest_item_updateTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 根据位置得到对应的数据
//        HomeDataBean.NewestInfoBean newest_info = datas.get(position);
//        Glide.with(mContext).load(Constant.BASE_PHOTO_URL + newest_info.getG_u_photo())
//                .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
//                .into(viewHolder.iv_newest_item_userPhoto);
//        viewHolder.tv_newest_item_userNick.setText(newest_info.getG_u_nick());
//        viewHolder.tv_newest_item_price.setText("￥" + newest_info.getG_price());
//        viewHolder.tv_newest_item_name.setText(newest_info.getG_name());
//        viewHolder.iv_newest_item_sex.setImageResource(newest_info.getG_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male);
//        viewHolder.tv_newest_item_like.setText(String.valueOf(newest_info.getG_like()));
//        viewHolder.tv_newest_item_updateTime.setText(DateUtil.getDatePoor(newest_info.getG_updateTime(), new Date()) + "擦亮");
//        if (!TextUtils.isEmpty(newest_info.getG_pic1())) {
//            viewHolder.iv_newest_item_pic1.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(Constant.BASE_PIC_URL + newest_info.getG_pic1())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
//                    .into(viewHolder.iv_newest_item_pic1);
//        } else {
//            viewHolder.iv_newest_item_pic1.setVisibility(View.GONE);
//        }
//        if (!TextUtils.isEmpty(newest_info.getG_pic2())) {
//            viewHolder.iv_newest_item_pic2.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(Constant.BASE_PIC_URL + newest_info.getG_pic2())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
//                    .into(viewHolder.iv_newest_item_pic2);
//            viewHolder.iv_newest_item_pic3.setVisibility(View.GONE);
//        } else {
//            viewHolder.iv_newest_item_pic2.setVisibility(View.GONE);
//        }
//        if (!TextUtils.isEmpty(newest_info.getG_pic3())) {
//            viewHolder.iv_newest_item_pic3.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(Constant.BASE_PIC_URL + newest_info.getG_pic3())
//                    .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
//                    .into(viewHolder.iv_newest_item_pic3);
//        } else {
//            viewHolder.iv_newest_item_pic3.setVisibility(View.GONE);
//        }
        return convertView;
    }

    static class ViewHolder {
        CircleImageView iv_newest_item_userPhoto;
        TextView tv_newest_item_userNick;
        TextView tv_newest_item_price;
        TextView tv_newest_item_name;
        ImageView iv_newest_item_sex;
        ImageView iv_newest_item_pic1;
        ImageView iv_newest_item_pic2;
        ImageView iv_newest_item_pic3;
        TextView tv_newest_item_like;
        TextView tv_newest_item_updateTime;
    }
}
