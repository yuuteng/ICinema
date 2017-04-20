package yuut.icinema.app;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yuut on 2017/4/13.
 */
//Volley 需要使用的底层请求
public class OkHttpStack extends HurlStack{
    private final OkHttpClient okHttpClient;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.okHttpClient = okHttpClient;
    }
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        OkUrlFactory mFactory = new OkUrlFactory(okHttpClient);
        return mFactory.open(url);
    }
}
