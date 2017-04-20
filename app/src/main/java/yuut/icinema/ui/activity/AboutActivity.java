package yuut.icinema.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yuut.icinema.BuildConfig;
import yuut.icinema.R;

/**
 * Created by yuut on 2017/4/20.
 * 关于界面
 */

public class AboutActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_app_version)
    TextView tvAppVersion;
    @Bind(R.id.fab)
    FloatingActionButton starView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        initToolBar(toolbar, "关于", R.mipmap.icon_arrow_back);
        tvAppVersion.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
    }
    //加载分享菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_share:
                shareApp(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //打开应用商店点赞
    @OnClick(R.id.fab)
    public void starAPP() {
        Uri uri = Uri.parse(String.format(this.getString(R.string.market), getPackageName()));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    //分享APP
    public static void shareApp(Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_app));
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_app_to_friend)));
    }
}
