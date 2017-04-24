package yuut.icinema.ui.fragment;

import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.adapter.CollectAdapter;
import yuut.icinema.adapter.SimpleFilmAdapter;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.SubjectBean;
import yuut.icinema.support.Util.DensityUtil;
import yuut.icinema.ui.activity.SubjectActivity;

/**
 * Created by yuut on 2017/4/20.
 */

public class CollectFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        CollectAdapter.OnItemClickListener {
    private List<SubjectBean> mData = new ArrayList<>();
    private CollectAdapter mAdapter;
    private View mView;
    @Bind(R.id.rv_fragment)
    protected RecyclerView mRecView;
    @Bind(R.id.fresh_fragment)
    protected SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.bind(this, mView);
        mRecView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dp2px(getContext(), 32f));
        initData();
        initEvent();
        int padding = DensityUtil.dp2px(getContext(), 4f);
        mRecView.setPadding(padding, padding, padding, padding);
        return mView;
    }

    protected void initData() {
        mAdapter = new CollectAdapter(getContext());
        mRecView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new MyAsyncTask().execute();
    }

    protected void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        new MyAsyncTask().execute();
    }

    @Override
    public void itemClick(String id, String imageUrl) {
        SubjectActivity.toActivity(getActivity(), id, imageUrl);
    }
    //snackbar
    @Override
    public void itemRemove(final int pos, final String id) {
        Snackbar.make(mView, "是否要取消收藏...", Snackbar.LENGTH_LONG).
                setAction("确定",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MyApplication.getDataSource().deleteFilm(id);
                            }
                        }).
                setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            mAdapter.cancelRemove(pos);
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                }).show();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, List<SubjectBean>> {

        @Override
        protected void onPreExecute() {
            mRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<SubjectBean> doInBackground(Void... voids) {
            return MyApplication.getDataSource().getFilmForCollected();
        }

        @Override
        protected void onPostExecute(List<SubjectBean> subjectBeans) {
            mAdapter.update(subjectBeans);
            mRefreshLayout.setRefreshing(false);
        }
    }
}
