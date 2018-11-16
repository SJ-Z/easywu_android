package com.cose.easywu.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.NoScrollGridView;
import com.cose.easywu.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnLoadImageListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentAdapter extends RecyclerView.Adapter {

    public static final int BANNER = 0; // 广告条幅类型
    public static final int TYPE = 1; // 分类类型
    public static final int NEWEST = 2; // 最近发布类型

    public static final String GOODS_BEAN = "goodsBean";

    private int currentType = 0;
    private Context mContext;
    private HomeDataBean homeDataBean;
    private LayoutInflater mLayoutInflater; // 用于初始化布局

    public HomeFragmentAdapter(Context mContext, HomeDataBean homeDataBean) {
        this.mContext = mContext;
        this.homeDataBean = homeDataBean;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BANNER) {
            return new BannerViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.banner_viewpager, null));
        } else if (viewType == TYPE) {
            return new TypeViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.type_item, null));
        } else if (viewType == NEWEST) {
            return new NewestViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.newest_item, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == BANNER) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            bannerViewHolder.setData(homeDataBean.getBanner_info());
        } else if (getItemViewType(position) == TYPE) {
            TypeViewHolder typeViewHolder = (TypeViewHolder) holder;
            typeViewHolder.setData(homeDataBean.getType_info());
        } else if (getItemViewType(position) == NEWEST) {
            NewestViewHolder newestViewHolder = (NewestViewHolder) holder;
            newestViewHolder.setData(homeDataBean.getNewest_info());
        }
    }
    
    class NewestViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private TextView tv_newest_more;
        private NoScrollGridView gv_newest;
        private NewestGridViewAdapter adapter;

        public NewestViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            tv_newest_more = itemView.findViewById(R.id.tv_newest_more);
            gv_newest = itemView.findViewById(R.id.gv_newest);
        }

        public void setData(final List<HomeDataBean.NewestInfoBean> newest_info) {
            adapter = new NewestGridViewAdapter(mContext, newest_info);
            gv_newest.setAdapter(adapter);

            // 设置item的监听
            gv_newest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ToastUtil.showMsgOnCenter(mContext, "goods_name=" + newest_info.get(position).getG_name(), Toast.LENGTH_SHORT);
//                    HomeDataBean.NewestInfoBean newestInfoBean = newest_info.get(position);
//                    // 商品信息类
//                    GoodsBean goodsBean = new GoodsBean();
//                    goodsBean.setCover_price(hotInfoBean.getCover_price());
//                    goodsBean.setFigure(hotInfoBean.getFigure());
//                    goodsBean.setName(hotInfoBean.getName());
//                    goodsBean.setProduct_id(hotInfoBean.getProduct_id());
//                    startGoodsInfoActivity(goodsBean);
                }
            });
        }
    }

    class TypeViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private GridView gv_type;
        private TypeAdapter adapter;

        public TypeViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            this.gv_type = itemView.findViewById(R.id.gv_home_type);

            // 设置item的点击事件
            gv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ToastUtil.showMsg(mContext, "position==" + position, Toast.LENGTH_SHORT);
                }
            });
        }

        public void setData(List<HomeDataBean.TypeInfoBean> type_info) {
            // 设置GridView的适配器
            adapter = new TypeAdapter(mContext, type_info);
            gv_type.setAdapter(adapter);
        }
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private Banner banner;

        public BannerViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            this.banner = itemView.findViewById(R.id.banner);
        }

        public void setData(List<HomeDataBean.BannerInfoBean> banner_info) {
            // 设置Banner的数据
            List<String> imagesUrl = new ArrayList<>();
            for (int i = 0; i < banner_info.size(); i++) {
                String imageUrl = banner_info.get(i).getBan_img();
                imagesUrl.add(imageUrl);
            }
            // 设置循环指示点
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            // 设置手风琴效果
            banner.setBannerAnimation(Transformer.Accordion);
            banner.setImages(imagesUrl, new OnLoadImageListener() {
                @Override
                public void OnLoadImage(ImageView view, Object url) {
                    // 联网请求图片 Glide
                    Glide.with(mContext).load(Constant.BASE_URL + url).into(view);
                }
            });

            // 设置item的点击事件
            banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case BANNER:
                currentType = BANNER;
                break;
            case TYPE:
                currentType = TYPE;
                break;
            case NEWEST:
                currentType = NEWEST;
                break;
        }
        return currentType;
    }

    @Override
    public int getItemCount() {
        // 开发过程中从1-->3
        return 3;
    }
}
