package yuut.icinema.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import yuut.icinema.app.MyApplication;

/**
 * Created by yuut on 2017/4/13.
 */
//对RecyclerView.Adapter<> 进行了封装
public class BaseAdapter <T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private int mLastPosition = -1;
    protected OnItemClickListener mCallback;

    //加载图片
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions options = MyApplication.getLoaderOptions();

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    public void showItemAnim(final View view, int pos) {
//        final Context mContext = view.getContext();
//        if (pos > mLastPosition) {
//            view.setAlpha(0.0f);
//            view.post(new Runnable() {
//                @Override
//                public void run() {
//                    Animator animator = AnimatorInflater.loadAnimator(
//                            mContext, R.animator.slide_from_right);
//                    animator.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            view.setAlpha(1.0f);
//                        }
//                    });
//                    animator.setTarget(view);
//                    animator.start();
//                }
//            });
//            mLastPosition = pos;
//        }
//    }
    //mCallBack
    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl);
    }
    //mCallBack
    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }
}
