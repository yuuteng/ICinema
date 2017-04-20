package yuut.icinema.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.bean.SimpleSubjectBean;
import yuut.icinema.support.Util.CelebrityUtil;
import yuut.icinema.support.Util.DensityUtil;
import yuut.icinema.support.Util.StringUtil;

/**
 * Created by yuut on 2017/4/13.
 * 加载电影简介页面数据
 */
//RecyclerView.ViewHolder  而非SimpleSubjectAdapter.ViewHolder
// 因为要返回FootView 或者 itemView
public class SimpleSubjectAdapter extends BaseAdapter<RecyclerView.ViewHolder> {

    //ItemView的类型，FootView应用于加载更多
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;

    //FootView的显示类型
    public static final int FOOT_LOADING = 0;
    public static final int FOOT_COMPLETED = 1;
    public static final int FOOT_FAIL = 2;
    private FootViewHolder mFootView;

    //用于判断是否是加载失败时点击的FootView
    public static final String FOOT_VIEW_ID = "-1";

    private Context mContext;
    private List<SimpleSubjectBean> mData;//电影简介

//    用于加载更多数据
    private int mTotalDataCount = 0;

    //判断是否属于 即将上映
    private boolean isComingFilm;

//    imageLoader的异步加载监听接口实例
    private ImageLoadingListener imageLoadingListener =new AnimateFirstDisplayListener();

    //构造函数
    public SimpleSubjectAdapter(Context context, List<SimpleSubjectBean> data,boolean isComingFilm) {
        this.mContext = context;
        this.mData = data;
        this.isComingFilm = isComingFilm;
    }
    public SimpleSubjectAdapter(Context context, List<SimpleSubjectBean> data) {
        this(context, data, false);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size()) {
            //第一次只初始化,不加载数据
            //TYPE_FOOT-> isLoadCompleted->FOOT_COMPLETED
            //滑动到底部
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * 用于加载数据时的url起点
     */
    public int getStart() {
        return mData.size();
    }

    public void setTotalDataCount(int totalDataCount) {
        this.mTotalDataCount = totalDataCount;
    }

    /**
     * 返回adapter数据的总数
     */
    public int getTotalDataCount() {
        return mTotalDataCount;
    }

    /**
     * 判断是否已经加载完毕
     */
    public boolean isLoadCompleted() {
        return mData.size() >= getTotalDataCount();
    }

    /**
     * 用于加载更多item
     */
    public void loadMoreData(List<SimpleSubjectBean> data) {
        this.mData.addAll(data);//将新加载的电影放到电影list
        notifyDataSetChanged();//通知适配器数据更改,刷新数据
    }

    public void loadFail() {
        mFootView.setFootView(FOOT_FAIL);
    }

    /**
     * 用于更新数据
     * @param data  更新的数据
     * @param totalDataCount 数据的总量，采取多次加载
     */
    public void updateList(List<SimpleSubjectBean> data, int totalDataCount) {
        this.mData = data;
        setTotalDataCount(totalDataCount);
        notifyDataSetChanged();
    }
//----------------------RecyclerView基本设置----------------------------------------------------------
    //建一个static class ViewHolder extends RecyclerView.ViewHolder{}类
    //此处有 FootViewHolder 和 ItemViewHolder 两个

    //加载item布局,创建ViewHolder实例
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOT) {
            if (mFootView == null) {
                //加载FootView
                View view = LayoutInflater.from(mContext).
                        inflate(R.layout.view_load_tips, parent, false);
                mFootView = new FootViewHolder(view);
            }
            return mFootView;
        } else {//TYPE_ITEM
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.item_simple_subject_layout, parent, false);
            return new ItemViewHolder(view);
        }
    }
    //每个子项滚动到屏幕时执行
    //获取滚入的新 电影,并将对应值赋予ViewHolder中的控件
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_FOOT) { //FootViewHolder
            ((FootViewHolder) holder).update();
        } else {//ItemViewHolder 刷新电影(设置相应控件的值)
            ((ItemViewHolder) holder).update();
        }
    }
    //一共多少子项
    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }
//----------------------RecyclerView基本设置----------------------------------------------------------

    //----------------------两个ViewHolder----------------------------------------------------------
    //加载电影
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //view通常为RecyclerView子项布局;  获取子布局中各控件
        //简介的各项控件
        @Bind(R.id.iv_item_simple_subject_image)
        ImageView image_film;
        @Bind(R.id.ll_item_simple_subject_rating)
        LinearLayout layout_rating;
        @Bind(R.id.rb_item_simple_subject_rating)
        RatingBar rating_bar;
        @Bind(R.id.tv_item_simple_subject_rating)
        TextView text_rating;
        @Bind(R.id.tv_item_simple_subject_count)
        TextView text_collect_count;
        @Bind(R.id.tv_item_simple_subject_title)
        TextView text_title;
        @Bind(R.id.tv_item_simple_subject_original_title)
        TextView text_original_title;
        @Bind(R.id.tv_item_simple_subject_genres)
        TextView text_genres;
        @Bind(R.id.tv_item_simple_subject_director)
        TextView text_director;
        @Bind(R.id.tv_item_simple_subject_cast)
        TextView text_cast;
        //存储简介
        SimpleSubjectBean sub;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);//获取id
            itemView.setOnClickListener(this);//绑定点击事件
        }

        public void update() {
            sub = mData.get(getLayoutPosition());//得到电影数据

            if (!isComingFilm) {//已上映界面更新(比未上映多出几项)
                layout_rating.setVisibility(View.VISIBLE);
                float rate = (float) sub.getRating().getAverage();
                rating_bar.setRating(rate / 2);
                text_rating.setText(String.format("%s", rate));
                text_collect_count.setText("(");
                text_collect_count.append(String.format("%d", sub.getCollect_count()));
                text_collect_count.append("人评价)");
            }
            //已上映与未上映 共同部分
            String title = sub.getTitle();
            String original_title = sub.getOriginal_title();
            text_title.setText(title);
            if (original_title.equals(title)) {//当原名和现在名字相同隐藏原名
                text_original_title.setVisibility(View.GONE);
            } else {
                text_original_title.setText(original_title);
                text_original_title.setVisibility(View.VISIBLE);
            }
            text_genres.setText(StringUtil.getListString(sub.getGenres(), ','));
            text_director.setText(StringUtil.getSpannableString("导演:", Color.GRAY));
            text_director.append(CelebrityUtil.list2String(sub.getDirectors(), '/'));
            text_cast.setText(StringUtil.getSpannableString("主演:", Color.GRAY));
            text_cast.append(CelebrityUtil.list2String(sub.getCasts(), '/'));
            //显示图片(调用)
            imageLoader.displayImage(sub.getImages().getLarge(),
                    image_film, options, imageLoadingListener);
        }
        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                //void onItemClick(String id, String imageUrl);
                //显示大图  Adapter中 控制 Fragment
                mCallback.onItemClick(mData.get(position).getId(),
                        mData.get(position).getImages().getLarge());
            }
        }
    }
    /**
     * recyclerView上拉加载更多的footViewHolder
     */
    public class FootViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ProgressBar progress_bar;
        private TextView text_load_tip;

        public FootViewHolder(View itemView) {
            super(itemView);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.pb_view_load_tip);
            text_load_tip = (TextView) itemView.findViewById(R.id.tv_view_load_tip);
            itemView.setOnClickListener(this);
        }

        public void setFootView(int event) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            switch (event) {
                case FOOT_LOADING:
                    params.height = DensityUtil.dp2px(mContext, 40f);
                    itemView.setLayoutParams(params);
                    progress_bar.setVisibility(View.VISIBLE);
                    text_load_tip.setText("正在加载更多...");
                    itemView.setClickable(false);
                    break;
                case FOOT_COMPLETED:
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    itemView.setClickable(false);
                    break;
                case FOOT_FAIL:
                    params.height = DensityUtil.dp2px(mContext, 40f);
                    itemView.setLayoutParams(params);
                    progress_bar.setVisibility(View.GONE);
                    text_load_tip.setText("加载失败,请点击重试");
                    itemView.setClickable(true);
            }
        }
        public void update() {
            if (isLoadCompleted()){
                setFootView(FOOT_COMPLETED);
            }

            else{
                setFootView(FOOT_LOADING);
            }

        }
        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                setFootView(FOOT_LOADING);
                mCallback.onItemClick(FOOT_VIEW_ID, null);
            }
        }
    }
//----------------------两个ViewHolder   以上为RecyclerView基本操作-----------------------------------
//加载图片 实现
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);//是否第一次被加载
                if (firstDisplay) {//如果是第一次被加载,加载图片并加入list中
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
