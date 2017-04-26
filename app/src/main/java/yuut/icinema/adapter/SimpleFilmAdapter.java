package yuut.icinema.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.SimpleCardBean;

/**
 * Created by yuut on 2017/4/20.
 */
//详情页面中 底部的推荐电影  海报+名字  adapter
public class SimpleFilmAdapter extends RecyclerView.Adapter<SimpleFilmAdapter.ViewHolder>{
    private Context mContext;
    private List<SimpleCardBean> mData = new ArrayList<>(); //推荐 海报+名字
    private OnItemClickListener callback; //SimpleFilmAdapter内定义

    public SimpleFilmAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener callback) {
        this.callback = callback;
    }
    //刷新数据
    public void update(List<SimpleCardBean> data) {
        mData.clear();
        notifyDataSetChanged();
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
            notifyItemInserted(i);
        }
    }
    //----------------基本用法↓-------------------------------------------------------
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载子布局
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_simple_film_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //刷新控件的值
        holder.update();

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_item_simple_film_image)
        ImageView image_film;
        @Bind(R.id.tv_item_simple_film_text)
        TextView text_title;

        SimpleCardBean subj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    if (callback != null) {
//                   接口声明void itemClick(String id, String imageUrl, boolean isFilm);
                        callback.itemClick(mData.get(pos).getId(),
                                mData.get(pos).getImage(),
                                mData.get(pos).getIsFilm());
                    }
                }
            });
        }
        //将数据绑定在控件上
        public void update() {
            subj = mData.get(getLayoutPosition());
            Picasso.with(mContext).load(subj.getImage()).into(image_film);
//            imageLoader.displayImage(subj.getImage(), image_film, options);
            text_title.setText(subj.getName());
        }
    }
    //----------------基本用法↑-------------------------------------------------------
    public interface OnItemClickListener {
        void itemClick(String id, String imageUrl, boolean isFilm);
    }
}
