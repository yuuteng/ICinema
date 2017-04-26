package yuut.icinema.app;

import android.app.Application;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.sql.SQLException;

import yuut.icinema.R;
import yuut.icinema.db.DataSource;

/**
 * Created by yuut on 2017/4/13.
 */
//管理全局的状态信息
public class MyApplication extends Application {
    private static RequestQueue mQueue; //volley消息队列
    private static DataSource mSource;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
        mSource = new DataSource(getApplicationContext());
        try {
            mSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return mSource;
    }

    public static RequestQueue getHttpQueue() {
        return mQueue;
    }

    //volley使用
    public static void addRequest(Request request, Object tag) {
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }
    public static void removeRequest(Object tag) {
        mQueue.cancelAll(tag);
    }
}
