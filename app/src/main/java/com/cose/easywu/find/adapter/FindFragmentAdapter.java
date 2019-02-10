package com.cose.easywu.find.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.cose.easywu.utils.RecycleViewDivider;
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
    public static final int NEWEST_FIND_PEOPLE = 2; // 最新发布的寻找失主类型
    public static final int NEWEST_FIND_GOODS = 3; // 最新发布的寻找失物类型

    public static final String GOODS_BEAN = "findGoodsBean";

    private int currentType = 0;
    private Context mContext;
    private FindDataBean findDataBean;
    private LayoutInflater mLayoutInflater; // 用于初始化布局

    private NewestFindPeopleViewHolder newestFindPeopleViewHolder;
    private NewestFindGoodsViewHolder newestFindGoodsViewHolder;

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
        } else if (viewType == NEWEST_FIND_PEOPLE) {
            return new NewestFindPeopleViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.newest_find_people_layout, null));
        } else if (viewType == NEWEST_FIND_GOODS) {
            return new NewestFindGoodsViewHolder(mContext, mLayoutInflater.
                    inflate(R.layout.newest_find_goods_layout, null));
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
        } else if (getItemViewType(position) == NEWEST_FIND_PEOPLE) {
            newestFindPeopleViewHolder = (NewestFindPeopleViewHolder) holder;
            newestFindPeopleViewHolder.setData(findDataBean.getNewestFindPeopleList());
        } else if (getItemViewType(position) == NEWEST_FIND_GOODS) {
            newestFindGoodsViewHolder = (NewestFindGoodsViewHolder) holder;
            newestFindGoodsViewHolder.setData(findDataBean.getNewestFindGoodsList());
        }
    }

    class NewestFindGoodsViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private ImageView iv_newest_refresh;
        private TextView tv_newest_more;
        private RecyclerView rv_newest;
        private NewestFindGoodsAdapter adapter;

        public NewestFindGoodsViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            iv_newest_refresh = itemView.findViewById(R.id.iv_newest_refresh);
            tv_newest_more = itemView.findViewById(R.id.tv_newest_more);
            rv_newest = itemView.findViewById(R.id.rv_newest);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_newest.setLayoutManager(layoutManager);
            rv_newest.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        }

        public void setData(final List<FindDataBean.FindNewestInfo> goodsList) {
            adapter = new NewestFindGoodsAdapter(mContext, goodsList);
            rv_newest.setAdapter(adapter);

            // 设置item的监听
//            rv_newest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                    HomeDataBean.NewestInfoBean newestInfoBean = homeDataBean.getNewest_info().get(position);
////                    // 商品信息类
////                    startGoodsInfoActivity(newestInfoBean);
//                }
//            });

            // 设置刷新按钮的监听
            iv_newest_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestNewestFindGoods();
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

        public void requestNewestFindGoods() {
            // 设置刷新按钮的动画
            Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            anim.setInterpolator(lin);
            anim.setDuration(500); // 设置动画持续周期
            anim.setRepeatCount(Animation.INFINITE); // 设置重复次数无限
            anim.setRepeatMode(Animation.RESTART);
            anim.setFillAfter(true); // 动画执行完后是否停留在执行完的状态
            iv_newest_refresh.startAnimation(anim);

            HttpUtil.sendGetRequest(Constant.NEWEST_FIND_GOODS_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("最新发布的寻找失物数据", "请求失败，原因：" + e.getMessage());
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
                        Log.e("最新发布的寻找失物数据", "请求成功");
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

    class NewestFindPeopleViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private ImageView iv_newest_refresh;
        private TextView tv_newest_more;
        private RecyclerView rv_newest;
        private NewestFindPeopleAdapter adapter;

        public NewestFindPeopleViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            iv_newest_refresh = itemView.findViewById(R.id.iv_newest_refresh);
            tv_newest_more = itemView.findViewById(R.id.tv_newest_more);
            rv_newest = itemView.findViewById(R.id.rv_newest);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_newest.setLayoutManager(layoutManager);
            rv_newest.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        }

        public void setData(final List<FindDataBean.FindNewestInfo> goodsList) {
            adapter = new NewestFindPeopleAdapter(mContext, goodsList);
            rv_newest.setAdapter(adapter);

            // 设置item的监听
//            rv_newest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                    HomeDataBean.NewestInfoBean newestInfoBean = homeDataBean.getNewest_info().get(position);
////                    // 商品信息类
////                    startGoodsInfoActivity(newestInfoBean);
//                }
//            });

            // 设置刷新按钮的监听
            iv_newest_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestNewestFindPeople();
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

        public void requestNewestFindPeople() {
            // 设置刷新按钮的动画
            Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            anim.setInterpolator(lin);
            anim.setDuration(500); // 设置动画持续周期
            anim.setRepeatCount(Animation.INFINITE); // 设置重复次数无限
            anim.setRepeatMode(Animation.RESTART);
            anim.setFillAfter(true); // 动画执行完后是否停留在执行完的状态
            iv_newest_refresh.startAnimation(anim);

            HttpUtil.sendGetRequest(Constant.NEWEST_FIND_PEOPLE_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("最新发布的寻找失主数据", "请求失败，原因：" + e.getMessage());
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
                        Log.e("最新发布的寻找失主数据", "请求成功");
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

    public void requestNewestFindPeople() {
        newestFindPeopleViewHolder.requestNewestFindPeople();
    }

    public void requestNewestFindGoods() {
        newestFindGoodsViewHolder.requestNewestFindGoods();
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
            case NEWEST_FIND_PEOPLE:
                currentType = NEWEST_FIND_PEOPLE;
                break;
            case NEWEST_FIND_GOODS:
                currentType = NEWEST_FIND_GOODS;
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
