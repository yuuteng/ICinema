package yuut.icinema.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.adapter.SimpleFilmAdapter;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.CelebrityEntity;
import yuut.icinema.bean.SimpleCardBean;
import yuut.icinema.bean.SimpleSubjectBean;
import yuut.icinema.bean.SubjectBean;
import yuut.icinema.support.Constant;
import yuut.icinema.support.Util.DensityUtil;
import yuut.icinema.support.Util.StringUtil;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

/**
 * Created by yuut on 2017/4/20.
 */
//电影详情 布局
public class SubjectActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SimpleFilmAdapter.OnItemClickListener, AppBarLayout.OnOffsetChangedListener {
    //intent中subjectId的key用于查询数据
    private static final String KEY_SUBJECT_ID = "subject_id";
    private static final String KEY_IMAGE_URL = "image_url";
    //json中subject的标签
    private static final String JSON_SUBJECTS = "subjects";

    private static final String SHARE_IMAGE = "share_image";
    private static final String URI_FOR_FILE = "file:/";
    private static final String URI_FOR_IMAGE = ".png";

    private int[] cast_id = {R.id.view_cast_layout_1,R.id.view_cast_layout_2,
            R.id.view_cast_layout_3, R.id.view_cast_layout_4,
            R.id.view_cast_layout_5, R.id.view_cast_layout_6};

    private CastViewHolder[] castViewHolders = new CastViewHolder[6];

    @Bind(R.id.refresh_subj)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.btn_subj_skip)
    FloatingActionButton mBtn;

    //film header
    @Bind(R.id.header_container_subj)
    AppBarLayout mHeaderContainer; //AppBarLayout
    @Bind(R.id.toolbar_container_subj)
    CollapsingToolbarLayout mToolbarContainer;
    @Bind(R.id.iv_header_subj)
    ImageView mToolbarImage;
    @Bind(R.id.introduce_container_subj) //中部电影介绍
    LinearLayout mIntroduceContainer;
    @Bind(R.id.toolbar_subj)
    Toolbar mToolbar;
    @Bind(R.id.iv_subj_images)
    ImageView mImage; //隐藏的电影小图片
    @Bind(R.id.rb_subj_rating)
    RatingBar mRatingBar;
    @Bind(R.id.tv_subj_rating)
    TextView mRating;
    @Bind(R.id.tv_subj_collect_count)
    TextView mCollect;
    @Bind(R.id.tv_subj_title)
    TextView mTitle;
    @Bind(R.id.tv_subj_original_title)
    TextView mOriginal_title;
    @Bind(R.id.tv_subj_genres)
    TextView mGenres;
    @Bind(R.id.tv_subj_ake)
    TextView mAke;
    @Bind(R.id.tv_subj_countries)
    TextView mCountries;

    @Bind(R.id.film_container_subj)
    LinearLayout mFilmContainer; //除了头部以外全部内容
    //film summary
    @Bind(R.id.tv_subj_summary)
    TextView mSummaryText;

    //film recommend
    @Bind(R.id.tv_subj_recommend_tip)
    TextView mRecommendTip;
    @Bind(R.id.re_subj_recommend)
    RecyclerView mRecommend;

    //film subject
    private String mId;
    private String mContent;
    private SubjectBean mSubject;

    private String mRecommendTags;
    private List<SimpleCardBean> mRecommendData = new ArrayList<>();//底部推荐电影
    private SimpleFilmAdapter mRecommendFilmAdapter;

    private boolean isSummaryShow = false;

    private File mFile;

    private boolean isCollect = false;

    private int mImageWidth;
    private FrameLayout.LayoutParams mIntroduceContainerParams;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MyApplication.getLoaderOptions();

    //----------------------------------------------------------------------------------------
    //转到该Activity时需要传入的值
    //subject_id    image_url
    public static void toActivity(Activity activity, String id, String imageUrl) {
        Intent intent = new Intent(activity, SubjectActivity.class);
        intent.putExtra(KEY_SUBJECT_ID, id);
        intent.putExtra(KEY_IMAGE_URL, imageUrl);
        //if当前版本>21 增加转换动画
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            共享元素跳转
            activity.startActivity(intent,makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public static void toActivityWithShareElement(Activity context, ImageView image, String id) {
//        Intent intent = new Intent(context, SubjectActivity.class);
//        intent.putExtra(KEY_SUBJECT_ID, id);
//        image.setTransitionName(SHARE_IMAGE);
//        context.startActivity(intent, makeSceneTransitionAnimation(
//                context, image, image.getTransitionName()).toBundle());
//    }

    /**
     * activity转场动画
     *
     */
//    @TargetApi使高版本API的代码在低版本SDK不报错
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Transition makeTransition() {
        TransitionSet transition = new TransitionSet();
        transition.addTransition(new Explode());
        transition.addTransition(new Fade());
        transition.setDuration(400);
        return transition;
    }
//--------------生命周期↓----------------------------------------------
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        设置 activity转场动画
            getWindow().setEnterTransition(makeTransition());
        }
        //获取intent中携带的  subject_id  键值
        mId = getIntent().getStringExtra(KEY_SUBJECT_ID);
        initView();
        initEvent();
        //从数据库中取出数据(收藏过的电影)
        //判断是否被收藏
        mSubject = MyApplication.getDataSource().filmOfId(mId);
        if (mSubject != null) {
            isCollect = true;//收藏过的电影
            initAfterGetData();
        } else {
            volleyGetSubject();
        }
    }
    //右上角 搜索 和 收藏 两个选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        MenuItem collect = menu.findItem(R.id.action_sub_collect);
        if (isCollect) {
            collect.setIcon(R.drawable.ic_collect);
        } else {
            collect.setIcon(R.drawable.ic_uncollect);
        }
        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.removeRequest(mId);
    }

    //--------------生命周期↑-------------------------------------------------

    private void initView() {
        //设置圆形刷新球的偏移量
        mRefresh.setProgressViewOffset(false,
                -DensityUtil.dp2px(getApplication(), 8f),
                DensityUtil.dp2px(getApplication(), 32f));
        mToolbar.setTitle("");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);//返回键

        //用于collapsingToolbar缩放时content中内容和图片的动作
        //电影小图片 隐藏->显示
        mImageWidth = mImage.getLayoutParams().width + DensityUtil.dp2px(getApplication(), 8f);
        mIntroduceContainerParams = (FrameLayout.LayoutParams) mIntroduceContainer.getLayoutParams();
//        底部推荐电影  图片+名字
        for (int i = 0; i < 6; i++) {
            View view = findViewById(cast_id[i]);
            castViewHolders[i] = new CastViewHolder(view);
        }
        //横向底部推荐电影
        mRecommend.setLayoutManager(new LinearLayoutManager(SubjectActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRecommendFilmAdapter = new SimpleFilmAdapter(this);//底部推荐电影
        mRecommend.setAdapter(mRecommendFilmAdapter);
        mRecommendFilmAdapter.update(mRecommendData);

        mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mId + URI_FOR_IMAGE);
        String imageUri = (mFile.exists() ?
                String.format("%s%s", URI_FOR_FILE, mFile.getPath()) :
                getIntent().getStringExtra(KEY_IMAGE_URL));
        imageLoader.displayImage(imageUri, mImage, options);
        imageLoader.displayImage(imageUri, mToolbarImage, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,final Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        //根据电影海报颜色,调整 详情页面顶部的颜色
                        Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int defaultBgColor = Color.parseColor("#3F51B5");//colorPrimary
                                int bgColor = palette.getDarkVibrantColor(defaultBgColor);//暗鲜艳色
                                mToolbarContainer.setBackgroundColor(bgColor);
                            }
                        });
                    }
                });
    }


    private void initEvent() {
        mRefresh.setOnRefreshListener(this);
        mBtn.setOnClickListener(this);
        mRecommendFilmAdapter.setOnItemClickListener(this);
        mRecommendTip.setOnClickListener(this);
        mRecommendTip.setClickable(false);
        //利用appBarLayout的回调接口禁止或启用swipeRefreshLayout
        mHeaderContainer.addOnOffsetChangedListener(this);
    }

    private void volleyGetSubject() {
        String url = Constant.API + Constant.SUBJECT + mId;
        mRefresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mContent = response;
                        //如果film已经收藏,更新数据
                        if (isCollect) filmSave();
                        mSubject = new Gson().fromJson(mContent, Constant.subType);
                        initAfterGetData();
                        mRefresh.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SubjectActivity.this, error.toString(),
                                Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
        MyApplication.addRequest(stringRequest,mId);
    }

    /**
     * 得到网络返回数据初始化界面
     */
    private void initAfterGetData() {
        if (mSubject == null) return;
        if (mSubject.getTitle() != null) {
            mToolbarContainer.setTitle(mSubject.getTitle());
        }

        //评分
        if (mSubject.getRating() != null) {
            float rate = (float) (mSubject.getRating().getAverage() / 2);
            mRatingBar.setRating(rate);
            mRating.setText(String.format("%s", rate * 2));
        }

        mCollect.setText("(");
        mCollect.append(String.format("%s", mSubject.getCollect_count()));
        mCollect.append("人评价)");
        mTitle.setText(String.format("%s   ", mSubject.getTitle()));
        mTitle.append(StringUtil.getSpannableString1(
                String.format("  %s  ", mSubject.getYear()),
                new ForegroundColorSpan(Color.WHITE),
                new BackgroundColorSpan(Color.parseColor("#5ea4ff")),
                new RelativeSizeSpan(0.88f)));

        if (!mSubject.getOriginal_title().equals(mSubject.getTitle())) {
            mOriginal_title.setText(mSubject.getOriginal_title());
            mOriginal_title.setVisibility(View.VISIBLE);
        } else {
            mOriginal_title.setVisibility(View.GONE);
        }
        mGenres.setText(StringUtil.getListString(mSubject.getGenres(), ','));
        mAke.setText(StringUtil.getSpannableString(
                "又名: ", Color.GRAY));
        mAke.append(StringUtil.getListString(mSubject.getAka(), '/'));
        mCountries.setText(StringUtil.getSpannableString(
                "制片国家或地区: ", Color.GRAY));
        mCountries.append(StringUtil.getListString(mSubject.getCountries(), '/'));

        mSummaryText.setText(StringUtil.getSpannableString(
                "简介: ", Color.parseColor("#5ea4ff")));
        mSummaryText.append(mSubject.getSummary());
        mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
        mSummaryText.setOnClickListener(this);

        //获得导演演员数据列表
        getCastData();

        //显示View并配上动画
        //除了头部以外全部内容
        mFilmContainer.setAlpha(0f);
        mFilmContainer.setVisibility(View.VISIBLE);
        mFilmContainer.animate().alpha(1f).setDuration(800);

        //加载推荐
        mRecommendTip.setText("正在加载推荐......");
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < mSubject.getGenres().size(); i++) {
            tag.append(mSubject.getGenres().get(i));
            if (i == 1) break;
        }
        mRecommendTags = tag.toString();
        volley_Get_Recommend();
    }

    //获得导演演员数据列表
    private void getCastData() {
        boolean isDirWithCast = false;
        for (int i = 0; (i < mSubject.getDirectors().size() && i < 2); i++) {
            CelebrityEntity cel = mSubject.getDirectors().get(i);
            //判断导演是不是主演，如果是打上“导演兼主演”标签
            //为满足一些特殊的数据，需要做null判断
            if (i == 0 && mSubject.getCasts().get(0).getId() != null && cel.getId() != null
                    && cel.getId().equals(mSubject.getCasts().get(0).getId())) {
                isDirWithCast = true;
                castViewHolders[i].bindDataForDir(cel, true);
            } else {
                isDirWithCast = false;
                castViewHolders[i].bindDataForDir(cel, false);
            }
        }

        int j = 2;
        for (int i = 0; i < 4; i++) {
            //判断主演是否是导演，如果是就跳过
            if (i == 0 && isDirWithCast) i++;
            if (i == mSubject.getCasts().size()) break;
            CelebrityEntity cel = mSubject.getCasts().get(i);
            castViewHolders[j++].bindData(cel);
        }
    }
    /**
     * 通过查询tag获得recommend数据
     */
    private void volley_Get_Recommend() {
        if (TextUtils.isEmpty(mRecommendTags)) return;
        String url = Constant.API + Constant.SEARCH_TAG + mRecommendTags;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new GsonBuilder().create();
                        try {
                            mRecommendTip.setText("相关推荐: ");
                            mRecommendTip.setClickable(false);
                            String json = response.getString(JSON_SUBJECTS);
                            List<SimpleSubjectBean> data = gson.fromJson(json,
                                    Constant.simpleSubTypeList);
                            mRecommendData = new ArrayList<>();
                            for (SimpleSubjectBean simpleSub : data) {
                                mRecommendData.add(new SimpleCardBean(
                                        simpleSub.getId(),
                                        simpleSub.getTitle(),
                                        simpleSub.getImages().getLarge(),
                                        true));
                            }
                            mRecommendFilmAdapter.update(mRecommendData);
                            mRecommend.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRecommendTip.setText("获取失败请重试......");
                        mRecommendTip.setClickable(true);
                    }
                });
        MyApplication.addRequest(request,mId);
    }

    /**
     * 用于保存filmContent和filmImage
     * 将图片保存成文件,将电影id和Json 信息存入数据库
     */
    private void filmSave() {
        if (mFile.exists()) mFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFile);
            Bitmap bitmap = imageLoader.loadImageSync(mSubject.getImages().getLarge());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //将电影信息存入到数据库中
        mSubject.setLocalImageFile(mFile.getPath());
        String content = new Gson().toJson(mSubject, Constant.subType);
        //将id 和 全部 String Json 信息全部存入数据库中
        MyApplication.getDataSource().insertOrUpDataFilm(mId, content);
        Toast.makeText(this, "收藏成功!  (＾－＾)V", Toast.LENGTH_SHORT).show();
    }
    //删除本地 图片 和 数据库中的信息
    private void cancelSave() {
        //将数据从数据库中删除
        MyApplication.getDataSource().deleteFilm(mId);
        //将保存的海报图片删除
        if (mFile.exists()) mFile.delete();
        Toast.makeText(this, "取消收藏!  ︿(￣︶￣)︿", Toast.LENGTH_SHORT).show();
    }
    //-------------------setEvent↓-------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //简介展开
            case R.id.tv_subj_summary:
                if (isSummaryShow) {
                    isSummaryShow = false;
                    //设置文字溢出隐藏
                    mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
                    mSummaryText.setLines(3);
                } else {
                    isSummaryShow = true;
                    mSummaryText.setEllipsize(null);
                    mSummaryText.setSingleLine(false);
                }
                break;
            case R.id.tv_subj_recommend_tip:
                volley_Get_Recommend();
                break;
            //FloatingActionButton
            case R.id.btn_subj_skip://跳往豆瓣电影的移动版网页
                if (mSubject == null) break;
//                https://movie.douban.com/subject/26776117/mobile
//                https://movie.douban.com/subject/26776117/cinema/dalian/
//                WebActivity.toWebActivity(this, mSubject.getMobile_url(), mSubject.getTitle());
                StringBuilder sb = new StringBuilder();
                sb.append("https://movie.douban.com/subject/");
                sb.append(mSubject.getId());
                sb.append("/cinema/dalian/");

                WebActivity.toWebActivity(this, sb.toString() , mSubject.getTitle());
                break;
        }
    }

    @Override
    public void onRefresh() {
        volleyGetSubject();
    }

    //点击 电影  或者  演员
    @Override
    public void itemClick(String id, String imageUrl, boolean isFilm) {
        if (isFilm) {
            SubjectActivity.toActivity(this, id, imageUrl);
        } else {
            CelebrityActivity.toActivity(this, id);
        }
    }
    //菜单栏点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //返回键
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAfterTransition();
                } else {
                    this.finish();
                }
                break;
            case R.id.action_sub_search:
                //搜索按钮
                this.startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_sub_collect:
                //点击收藏后将subject存入数据库中,并将图片存入文件
                collectFilmAndSaveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * AppBarLayout onOffsetChangeListener
     * AppBarLayout 垂直方向移动的时候
     * 让图片显示出来 + 让layout 右移
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //利用AppBarLayout的回调接口启用或者关闭下拉刷新
        mRefresh.setEnabled(verticalOffset == 0);
        //设置AppBarLayout下方内容的滚动效果
        float alpha = Math.abs(verticalOffset) * 1.0f / appBarLayout.getTotalScrollRange();
        changeContentLayout(alpha);//自定义的动画函数
    }
    //-------------------setEvent↑-------------------------------
    /**
     * 点击收藏后将subject存入数据库中,并将图片存入文件
     */
    private void collectFilmAndSaveImage() {
        if (mSubject == null) return;
        if (isCollect) {
            cancelSave();
            isCollect = false;
        } else {
            filmSave();
            isCollect = true;
        }
        supportInvalidateOptionsMenu();
    }

    /**
     * 使content中内容位置和图片透明度随着AppBarLayout的伸缩而改变
     */
    private void changeContentLayout(float alpha) {
        //透明度完全显示之后,调整对齐方式左对齐
//        setContentGravity(alpha == 1 ? Gravity.START : Gravity.CENTER_HORIZONTAL);
        mImage.setAlpha(alpha);
        mIntroduceContainerParams.leftMargin = (int) (mImageWidth * alpha);
        mIntroduceContainer.setLayoutParams(mIntroduceContainerParams);
    }
//当上滑滚动好了之后,重新设置 简介layout中各个小控件的位置
    private void setContentGravity(int gravity) {
        mIntroduceContainer.setGravity(gravity);
        mAke.setGravity(gravity);
        mCountries.setGravity(gravity);
    }

    class CastViewHolder implements View.OnClickListener {
        CelebrityEntity mCastData;
        View mCastView;
        ImageView mImage;
        TextView mName;

        CastViewHolder(View castView) {
            mCastView = castView;
            mImage = (ImageView) castView.findViewById(R.id.iv_item_simple_cast_image);
            mName = (TextView) castView.findViewById(R.id.tv_item_simple_cast_text);
            mCastView.setVisibility(View.GONE);
        }

        void setCastData(CelebrityEntity data) {
            mCastData = data;
        }

        void bindData(CelebrityEntity data) {
            setCastData(data);
            mName.setText(data.getName());

            mCastView.setVisibility(View.VISIBLE);
            mCastView.setOnClickListener(this);

            if (data.getAvatars() == null) return;
            imageLoader.displayImage(data.getAvatars().getLarge(), mImage, options);
        }

        void bindDataForDir(CelebrityEntity data, boolean isCast) {
            setCastData(data);
            if (isCast) {
                mName.setText(data.getName() + "(导演兼主演)");
            } else {
                mName.setText(data.getName() + "导演");
            }

            mCastView.setVisibility(View.VISIBLE);
            mCastView.setOnClickListener(this);

            if (data.getAvatars() == null) return;
            imageLoader.displayImage(data.getAvatars().getLarge(), mImage, options);
        }

        @Override
        public void onClick(View view) {
            if (mCastData != null) {
                if (mCastData.getId() == null) {
                    Toast.makeText(SubjectActivity.this, "暂无资料", Toast.LENGTH_SHORT).show();
                } else {
                    CelebrityActivity.toActivity( SubjectActivity.this, mCastData.getId());
                }
            }
        }
    }
}
