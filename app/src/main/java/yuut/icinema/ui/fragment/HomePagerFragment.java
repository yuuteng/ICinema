package yuut.icinema.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.adapter.BaseAdapter;
import yuut.icinema.adapter.SimpleSubjectAdapter;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.SimpleSubjectBean;
import yuut.icinema.support.Util.DensityUtil;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static yuut.icinema.support.Constant.API;
import static yuut.icinema.support.Constant.COMING;
import static yuut.icinema.support.Constant.IN_THEATERS;
import static yuut.icinema.support.Constant.US_BOX;
import static yuut.icinema.support.Constant.simpleSubTypeList;

/**
 * Created by yuut on 2017/4/12.
 * 填充列表的电影信息
 * SwipeRefreshLayout
 * RecyclerView
 * FloatingActionButton
 */

public class HomePagerFragment extends Fragment implements BaseAdapter.OnItemClickListener{
    private static final String AUTO_REFRESH = "auto refresh";
    private static final String LAST_RECORD = "last record";
    private static final int RECORD_COUNT = 20;

    private static final String JSON_TOTAL = "total";
    private static final String JSON_SUBJECTS = "subjects";

    private static final String KEY_FRAGMENT_TITLE = "title";

    private static final int POS_IN_THEATERS = 0;
    private static final int POS_COMING = 1;
    private static final int POS_US_BOX = 2;

    private static final String[] TYPE = {"in theaters", "coming", "us box"};

    private static final String VOLLEY_TAG = "HomePagerFragment";

    @Bind(R.id.rv_fragment)
    RecyclerView mRecView;
    @Bind(R.id.fresh_fragment)
    SwipeRefreshLayout mRefresh;
    //FloatingActionButton
    @Bind(R.id.btn_fragment)
    FloatingActionButton mFABtn;

    private String mDataString;
    private SimpleSubjectAdapter mSimAdapter;
//    private BoxAdapter mBoxAdapter;
    private List<SimpleSubjectBean> mSimData = new ArrayList<>(); //电影数据列表
//    private List<BoxSubjectBean> mBoxData = new ArrayList<>();
    private RecyclerView.OnScrollListener mScrollListener;

    private int mTitlePos;
    private String mRequestUrl;
    private int mTotalItem;
    private boolean isFirstRefresh = true;
    private int mStart = 0;

    private SharedPreferences mSharePreferences;

    public static HomePagerFragment newInstance(int position) {
        //实例化自定义Fragment,将position的值传给Fragment,在create方法中取出
        Bundle args = new Bundle();
        args.putInt(KEY_FRAGMENT_TITLE, position);
        HomePagerFragment fragment = new HomePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //------------------------------------生命周期↓-------------------------------------

    //取出传入需要显示 Fragment position的值 (FRAGMENT_TITLE)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitlePos = getArguments().getInt(KEY_FRAGMENT_TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.bind(this, view);//绑定数据

        mRefresh.setColorSchemeResources(R.color.colorPrimary);
//        第一个参数scale就是就是刷新那个圆形进度是是否缩放,如果为true表示缩放,圆形进度图像就会从小到大展示出来,为false就不缩放
//        第二个参数start和end就是那刷新进度条展示的相对于默认的展示位置,start和end组成一个范围，在这个y轴范围就是那个圆形进度ProgressView展示的位置
        mRefresh.setProgressViewOffset(false, 0, DensityUtil.dp2px(getContext(), 32f));
        mSharePreferences = getActivity().getSharedPreferences(LAST_RECORD, Context.MODE_PRIVATE);
        initData();
        initEvent();
        return view;
    }
        @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //第一次加载 || 取出保存的键值对AUTO_REFRESH,为true则执行
            if (isFirstRefresh || sharedPreferences.getBoolean(AUTO_REFRESH, false)) {
                updateFilmData();
                isFirstRefresh = false;
            }

    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.removeRequest(VOLLEY_TAG + mTitlePos);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //------------------------------------生命周期↑-------------------------------------

    private void initData() {
        switch (mTitlePos) {
            case POS_IN_THEATERS:
                initSimpleRecyclerView(false);
                break;
            case POS_COMING:
                initSimpleRecyclerView(true);
                break;
            case POS_US_BOX:
                //initBoxRecyclerView();
                break;
            default:
        }
    }
    private void initEvent() {
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStart = 0;
                updateFilmData();//刷新电影
            }
        });
        mFABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecView.getAdapter() != null) {
                    mRecView.scrollToPosition(0);//移动到顶部
                }
            }
        });
    }

    //初始化 正在上映 和 即将上映 对应的fragment
    private void initSimpleRecyclerView(boolean isComing) {
        int padding = DensityUtil.dp2px(getContext(), 2f);
        //设置mRecView的padding Fragment中的RecyclerView
        setPaddingForRecyclerView(-padding);
        LinearLayoutManager inManager = new LinearLayoutManager(getActivity());
        inManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecView.setLayoutManager(inManager);

            //请求网络数据前先加载  上次的电影数据
//        List<SimpleSubjectBean> mSimData
            mSimAdapter = new SimpleSubjectAdapter(getActivity(), mSimData, isComing);
            if (getRecord() != null) {//上次有存数据,不空
// public static final Type simpleSubTypeList = new TypeToken<List<SimpleSubjectBean>>() {}.getType();
                mSimData = new Gson().fromJson(getRecord(), simpleSubTypeList);//取出保存的字符串,解析Json
//            RECORD_COUNT=20
                mSimAdapter.updateList(mSimData, RECORD_COUNT);
            }
            mSimAdapter.setOnItemClickListener(this);//点击了电影转入详情界面
            mRecView.setAdapter(mSimAdapter);
    }

    /**
     * 初始化“北美票房”对应的fragment
     */
    private void initBoxRecyclerView() {
//        int padding = DensityUtil.dp2px(getContext(), 2f);
//        setPaddingForRecyclerView(padding);
//        GridLayoutManager boxManager = new GridLayoutManager(getActivity(), 3);
//        mRecView.setLayoutManager(boxManager);
//        //请求网络数据前先加载上次的电影数据
//        if (getRecord() != null) {
//            mBoxData = new Gson().fromJson(getRecord(), simpleBoxTypeList);
//        }
//        mBoxAdapter = new BoxAdapter(getActivity(), mBoxData);
//        mBoxAdapter.setOnItemClickListener(this);
//        mRecView.setAdapter(mBoxAdapter);
    }

    //更新电影数据
    private void updateFilmData() {
        switch (mTitlePos) {
            case POS_IN_THEATERS:
//                http://api.douban.com + /v2/movie/in_theaters
                mRequestUrl = API + IN_THEATERS;
                volley_Get_Coming();
                break;
            case POS_COMING:
                mRequestUrl = API + COMING;
                volley_Get_Coming();
                break;
            case POS_US_BOX:
                mRequestUrl = API + US_BOX;
//                volley_Get_USBox();
                break;
        }
    }
    /**
     * 通过Volley框架的全局消息队列获取到url对应的数据
     */
    private void volley_Get_Coming() {
        mRefresh.setRefreshing(true); //显示加载小圆圈
        //volley.toolbox: JsonObjectRequest
        JsonObjectRequest request = new JsonObjectRequest(mRequestUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mTotalItem = response.getInt(JSON_TOTAL);//total
                            mDataString = response.getString(JSON_SUBJECTS);//subjects
//                         自定义新类型simpleSubTypeList :List<SimpleSubjectBean>
                            //Gson解析(String字符串, 解析成的类型)
                            mSimData = new Gson().fromJson(mDataString, simpleSubTypeList);
                            mSimAdapter.updateList(mSimData, mTotalItem);
                            //实现recyclerView的下拉刷新
                            setOnScrollListener();
                            saveRecord();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mRefresh.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
        MyApplication.addRequest(request,VOLLEY_TAG + mTitlePos);
    }

    /**
     * 为RecyclerView设置下拉刷新及floatingActionButton的消失出现
     */
    private void setOnScrollListener() {
        if (mRecView == null) {//如果RecyclerView不存在则返回
            return;
        }
        if (mScrollListener == null) {//如果ScrollListener还没有,则创建
            mScrollListener = new RecyclerView.OnScrollListener() {
                int lastVisibleItem;//最底下可以看到的元素
                boolean isShow = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView,int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    SCROLL_STATE_IDLE 停止滑动状态
                    //lastVisibleItem + 2 > mSimAdapter.getItemCount() 提前2个进行加载,避免用户等待
                    if (newState == SCROLL_STATE_IDLE
                            && lastVisibleItem + 2 > mSimAdapter.getItemCount()
                            && mSimAdapter.getItemCount() - 1 < mSimAdapter.getTotalDataCount()) {
                        loadMore();
                    }
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                    为floatingActionBar的出现消失设置动画效果
                    LinearLayoutManager lm = (LinearLayoutManager) mRecView.getLayoutManager();
                    lastVisibleItem = lm.findLastVisibleItemPosition();
                    if (lm.findFirstVisibleItemPosition() == 0) {
                        if (isShow) {//正在显示
//                            animatorForGone();
                            isShow = false;
                        }
                    } else if (dy < -50 && !isShow) {//向下滑动,并且没有被显示出来
//                        animatorForVisible();
                        isShow = true;
                    } else if (dy > 20 && isShow) {//向上滑,正在显示
//                        animatorForGone();
                        isShow = false;
                    }
                }
            };
            mRecView.addOnScrollListener(mScrollListener);
        }
    }

    /**
     * adapter加载更多
     */
    private void loadMore() {
        //防止出现多次加载的情况
        if (mSimAdapter.getStart() == mStart) return;
        mStart = mSimAdapter.getStart();
        String url = mRequestUrl + ("?start=" + mStart);//GET 的索引方式 ?start=XXX
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<SimpleSubjectBean> moreData = new GsonBuilder().create().fromJson(
                            response.getString(JSON_SUBJECTS), simpleSubTypeList);
                    mSimAdapter.loadMoreData(moreData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSimAdapter.loadFail();
            }
        });
        MyApplication.addRequest(request,VOLLEY_TAG + mTitlePos);
    }
    /**
     * 为RecyclerView设置-2dp的padding用于抵消item的margin
     */
    private void setPaddingForRecyclerView(int padding) {
        mRecView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 当网络不好或中断时 显示上一次加载的数据
     */
    private String getRecord() {
//        TYPE = {"in theaters", "coming", "us box"}
        //读取哪个界面的信息  mTitlePos
        return mSharePreferences.getString(TYPE[mTitlePos], null);
    }

    /**
     * 保存上一次网络请求得到的数据
     */
    private void saveRecord() {
        if (mDataString != null) {
            SharedPreferences.Editor edit = mSharePreferences.edit();
//            TYPE = {"in theaters", "coming", "us box"}
            //存储哪个mTitlePos的 数据信息
            edit.putString(TYPE[mTitlePos], mDataString);
            edit.apply();
        }
    }

    @Override
    public void onItemClick(String id, String imageUrl) {
        if (id.equals(SimpleSubjectAdapter.FOOT_VIEW_ID)) {
            loadMore();
        } else {
//            SubjectActivity.toActivity(getActivity(), id, imageUrl);
        }
    }

    /**
     * 为floatingActionBar的出现消失设置动画效果
     */
//    private void animatorForGone() {
//        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_gone);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                mBtn.setVisibility(View.GONE);
//            }
//        });
//        anim.setTarget(mBtn);
//        anim.start();
//    }
//
//    private void animatorForVisible() {
//        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_visible);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                mBtn.setVisibility(View.VISIBLE);
//            }
//        });
//        anim.setTarget(mBtn);
//        anim.start();
//    }

}
