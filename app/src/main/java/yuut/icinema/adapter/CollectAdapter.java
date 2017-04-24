package yuut.icinema.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.bean.SubjectBean;
import yuut.icinema.support.Util.CelebrityUtil;
import yuut.icinema.support.Util.StringUtil;

/**
 * Created by yuut on 2017/4/23.
 */

public class CollectAdapter extends BaseAdapter<CollectAdapter.ViewHolder> {

    private static final String URI_FOR_FILE = "file://";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SubjectBean> mData;

    private OnItemClickListener callback;

    private SubjectBean undoSub;

    public void setOnItemClickListener(OnItemClickListener callback) {
        this.callback = callback;
    }

    public CollectAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
    }

    public void add(List<SubjectBean> data) {
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
            notifyItemInserted(i);
        }
    }

    public void update(List<SubjectBean> data) {
        this.mData.clear();
        notifyDataSetChanged();
        add(data);
    }

    /**
     * 移除相应的item
     */
    private void remove(int pos) {
        notifyItemRemoved(pos);
        undoSub = mData.get(pos);
        mData.remove(pos);
        callback.itemRemove(pos, undoSub.getId());
    }

    /**
     * 用于撤销“取消收藏”操作
     */
    public void cancelRemove(int pos) {
        if (undoSub != null) {
            notifyItemInserted(pos);
            mData.add(pos, undoSub);
            undoSub = null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_collect_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_collect_image)
        ImageView image_film;
        @Bind(R.id.tv_item_collect_rating)
        TextView text_rating;
        @Bind(R.id.tv_item_collect_title)
        TextView text_title;
        @Bind(R.id.tv_item_collect_year)
        TextView text_year;
        @Bind(R.id.tv_item_collect_genres)
        TextView text_genres;
        @Bind(R.id.tv_item_collect_cel)
        TextView text_cast;
        @Bind(R.id.tv_item_collect_delete)
        TextView btn_delete;
        @Bind(R.id.tv_item_collect_enter)
        TextView btn_enter;

        SubjectBean subj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btn_delete.setOnClickListener(this);
            btn_enter.setOnClickListener(this);
        }

        public void update() {
            subj = mData.get(getLayoutPosition());
            if (subj.getRating() != null) {
                float rate = (float) subj.getRating().getAverage();
                text_rating.setText(String.format("%s", rate));
            }
            String title = subj.getTitle();
            text_title.setText(title);
            text_year.setText(String.format("  %s  ", subj.getYear()));
            text_genres.setText(StringUtil.getListString(subj.getGenres(), ','));
            text_cast.setText("导演: ");
            text_cast.append(String.format("%s//",
                    CelebrityUtil.list2String(subj.getDirectors(), ',')));
            text_cast.append("主演: ");
            text_cast.append(CelebrityUtil.list2String(subj.getCasts(), ','));

            if (subj.getLocalImageFile() != null) {
                Logger.d(String.format("%s%s", URI_FOR_FILE, subj.getLocalImageFile()));
                Picasso.with(mContext)
                        .load(String.format("%s%s", URI_FOR_FILE, subj.getLocalImageFile()))
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(image_film);
            }
        }

        @Override
        public void onClick(View view) {
            if (view == btn_delete) {
                remove(getLayoutPosition());
            } else {
                callback.itemClick(mData.get(getLayoutPosition()).getId(),
                        mData.get(getLayoutPosition()).getImages().getLarge());
            }
        }
    }

    public interface OnItemClickListener {
        void itemClick(String id, String imageUrl);

        void itemRemove(int pos, String id);
    }
}
