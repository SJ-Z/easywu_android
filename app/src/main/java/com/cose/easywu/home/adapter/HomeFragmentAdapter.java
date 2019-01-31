package com.cose.easywu.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.activity.MoreGoodsActivity;
import com.cose.easywu.home.activity.TypeGoodsActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.NoScrollGridView;
import com.cose.easywu.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnLoadImageListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragmentAdapter extends RecyclerView.Adapter {

    public static final int SEARCHBAR = 0;
    public static final int BANNER = 1; // 广告条幅类型
    public static final int TYPE = 2; // 分类类型
    public static final int NEWEST = 3; // 最近发布类型

    public static final String GOODS_BEAN = "goodsBean";

    private int currentType = 0;
    private Context mContext;
    private HomeDataBean homeDataBean;
    private LayoutInflater mLayoutInflater; // 用于初始化布局

    private NewestViewHolder newestViewHolder;

    public HomeFragmentAdapter(Context mContext, HomeDataBean homeDataBean) {
        this.mContext = mContext;
        this.homeDataBean = homeDataBean;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEARCHBAR) {
            return new SearchBarViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.layout_searchbar, null));
        } else if (viewType == BANNER) {
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
        if (getItemViewType(position) == SEARCHBAR) {
            SearchBarViewHolder searchBarViewHolder = (SearchBarViewHolder) holder;
            searchBarViewHolder.init();
        } else if (getItemViewType(position) == BANNER) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            bannerViewHolder.setData(homeDataBean.getBanner_info());
        } else if (getItemViewType(position) == TYPE) {
            TypeViewHolder typeViewHolder = (TypeViewHolder) holder;
            typeViewHolder.setData(homeDataBean.getType_info());
        } else if (getItemViewType(position) == NEWEST) {
            newestViewHolder = (NewestViewHolder) holder;
            newestViewHolder.setData(homeDataBean.getNewest_info());
        }
    }

    class NewestViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private ImageView iv_newest_refresh;
        private TextView tv_newest_more;
        private NoScrollGridView gv_newest;
        private NewestGridViewAdapter adapter;

        public NewestViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            iv_newest_refresh = itemView.findViewById(R.id.iv_newest_refresh);
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
                    HomeDataBean.NewestInfoBean newestInfoBean = homeDataBean.getNewest_info().get(position);
                    // 商品信息类
                    startGoodsInfoActivity(newestInfoBean);
                }
            });

            // 设置刷新按钮的监听
            iv_newest_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestNewestGoods();
                }
            });

            // 设置查看更多按钮的监听
            tv_newest_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, MoreGoodsActivity.class));
                }
            });
        }

        public void requestNewestGoods() {
            HttpUtil.sendGetRequest(Constant.NEWEST_GOODS_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("最新发布商品的数据", "请求失败，原因：" + e.getMessage());
                    ToastUtil.showMsgOnCenter(mContext, "刷新失败", Toast.LENGTH_SHORT);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 解析数据
                    try {
                        Log.e("最新发布商品的数据", "请求成功");
                        String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                        processData(responseText);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void processData(String json) {
            homeDataBean.setNewest_info(JSON.parseArray(json, HomeDataBean.NewestInfoBean.class));
            adapter.setDatas(homeDataBean.getNewest_info());
            adapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(mContext, TypeGoodsActivity.class);
                    HomeDataBean.TypeInfoBean type = homeDataBean.getType_info().get(position);
                    intent.putExtra("type_id", type.getT_id());
                    intent.putExtra("type_name", type.getT_name());
                    mContext.startActivity(intent);
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
                    Glide.with(mContext).load(Constant.BASE_URL + url)
                            .apply(new RequestOptions().placeholder(R.drawable.pic_banner_loading))
                            .into(view);
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

    class SearchBarViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private TextView mTvSearch;
        private ImageButton mIbSearch;

        public SearchBarViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            mTvSearch = itemView.findViewById(R.id.tv_searchbar_search);
            mIbSearch = itemView.findViewById(R.id.ib_searchbar_search);
        }

        public void init() {
            // 设置点击事件
            mIbSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void requestNewestGoods() {
        newestViewHolder.requestNewestGoods();
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(HomeDataBean.NewestInfoBean goods) {
        Intent intent = new Intent(mContext, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case SEARCHBAR:
                currentType = SEARCHBAR;
                break;
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
        // 开发过程中从1-->4
        return 4;
    }
}
