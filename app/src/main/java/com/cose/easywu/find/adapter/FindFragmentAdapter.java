package com.cose.easywu.find.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.db.FindType;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.activity.MoreGoodsActivity;
import com.cose.easywu.home.activity.SearchResultActivity;
import com.cose.easywu.home.activity.TypeGoodsActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.NoScrollGridView;
import com.cose.easywu.utils.ToastUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindFragmentAdapter extends RecyclerView.Adapter {

    public static final int SEARCHBAR = 0;
    public static final int TYPE = 1; // 分类类型
    public static final int NEWEST = 2; // 最近发布类型

    public static final String GOODS_BEAN = "findGoodsBean";

    private int currentType = 0;
    private Context mContext;
    private FindDataBean findDataBean;
    private LayoutInflater mLayoutInflater; // 用于初始化布局

    private NewestViewHolder newestViewHolder;

    public FindFragmentAdapter(Context mContext, FindDataBean findDataBean) {
        this.mContext = mContext;
        this.findDataBean = findDataBean;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEARCHBAR) {
            return new SearchBarViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.layout_searchbar, null));
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
        } else if (getItemViewType(position) == TYPE) {
            TypeViewHolder typeViewHolder = (TypeViewHolder) holder;
            typeViewHolder.setData(findDataBean.getFindTypeList());
        } else if (getItemViewType(position) == NEWEST) {
            newestViewHolder = (NewestViewHolder) holder;
            newestViewHolder.setData(findDataBean.getFindNewestInfoList());
        }
    }

    class NewestViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private ImageView iv_newest_refresh;
        private TextView tv_newest_more;
        private NoScrollGridView gv_newest;
        private FindNewestGridViewAdapter adapter;

        public NewestViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            iv_newest_refresh = itemView.findViewById(R.id.iv_newest_refresh);
            tv_newest_more = itemView.findViewById(R.id.tv_newest_more);
            gv_newest = itemView.findViewById(R.id.gv_newest);
        }

        public void setData(final List<FindDataBean.FindNewestInfo> newest_info) {
            adapter = new FindNewestGridViewAdapter(mContext, newest_info);
            gv_newest.setAdapter(adapter);

            // 设置item的监听
            gv_newest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    HomeDataBean.NewestInfoBean newestInfoBean = homeDataBean.getNewest_info().get(position);
//                    // 商品信息类
//                    startGoodsInfoActivity(newestInfoBean);
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
            // 设置刷新按钮的动画
            Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            anim.setInterpolator(lin);
            anim.setDuration(500); // 设置动画持续周期
            anim.setRepeatCount(Animation.INFINITE); // 设置重复次数无限
            anim.setRepeatMode(Animation.RESTART);
            anim.setFillAfter(true); // 动画执行完后是否停留在执行完的状态
            iv_newest_refresh.startAnimation(anim);

            HttpUtil.sendGetRequest(Constant.NEWEST_GOODS_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("最新发布商品的数据", "请求失败，原因：" + e.getMessage());
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_newest_refresh.getAnimation().cancel(); // 取消刷新按钮的动画
                            ToastUtil.showMsgOnCenter(mContext, "刷新失败", Toast.LENGTH_SHORT);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 解析数据
                    try {
                        Log.e("最新发布商品的数据", "请求成功");
                        final String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_newest_refresh.getAnimation().cancel(); // 取消刷新按钮的动画
                                processData(responseText);
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void processData(String json) {
//            homeDataBean.setNewest_info(JSON.parseArray(json, HomeDataBean.NewestInfoBean.class));
//            adapter.setDatas(homeDataBean.getNewest_info());
//            adapter.notifyDataSetChanged();
        }
    }

    class TypeViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private GridView gv_type;
        private FindTypeAdapter adapter;

        public TypeViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            this.gv_type = itemView.findViewById(R.id.gv_home_type);

            // 设置item的点击事件
            gv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, TypeGoodsActivity.class);
                    FindType type = findDataBean.getFindTypeList().get(position);
                    intent.putExtra("type_id", type.getFt_id());
                    intent.putExtra("type_name", type.getFt_name());
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(List<FindType> findTypeList) {
            // 设置GridView的适配器
            adapter = new FindTypeAdapter(mContext, findTypeList);
            gv_type.setAdapter(adapter);
        }
    }

    class SearchBarViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private EditText mEtSearch;
        private ImageButton mIbSearch;

        public SearchBarViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            mEtSearch = itemView.findViewById(R.id.et_searchbar_search);
            mIbSearch = itemView.findViewById(R.id.ib_searchbar_search);
        }

        public void init() {
            // 设置搜索按钮的点击事件
            mIbSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = mEtSearch.getText().toString();
                    if (!TextUtils.isEmpty(key)) {
                        Intent intent = new Intent(mContext, SearchResultActivity.class);
                        intent.putExtra("key", key);
                        mContext.startActivity(intent);
                    }
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
