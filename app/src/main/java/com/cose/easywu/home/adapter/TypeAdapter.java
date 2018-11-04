package com.cose.easywu.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;

import java.util.List;

public class TypeAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeDataBean.TypeInfoBean> datas;

    public TypeAdapter(Context mContext, List<HomeDataBean.TypeInfoBean> type_info) {
        this.mContext = mContext;
        this.datas = type_info;
    }

    @Override
    public int getCount() {
        return datas.size();
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
            convertView = View.inflate(mContext, R.layout.item_type, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_typeImg = convertView.findViewById(R.id.iv_home_type);
            viewHolder.tv_typeName = convertView.findViewById(R.id.tv_home_typeName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 根据位置得到对应的数据
        HomeDataBean.TypeInfoBean typeInfoBean = datas.get(position);
        Glide.with(mContext).load(Constant.BASE_URL + typeInfoBean.getT_pic())
                .into(viewHolder.iv_typeImg);
        viewHolder.tv_typeName.setText(typeInfoBean.getT_name());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_typeImg;
        TextView tv_typeName;
    }
}
