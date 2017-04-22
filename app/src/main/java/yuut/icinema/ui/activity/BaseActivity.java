package yuut.icinema.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import yuut.icinema.R;

/**
 * Created by yuut on 2017/4/20.
 * 初始化 toolbar
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void initToolBar(Toolbar toolbar, String title, int icon) {
        toolbar.setTitle(title);// 标题的文字需在setSupportActionBar之前，不然会无效
        if (icon != -1) {
            toolbar.setNavigationIcon(R.mipmap.icon_arrow_back);
        }
        setSupportActionBar(toolbar);
    }
}

