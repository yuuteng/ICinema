package yuut.icinema.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import yuut.icinema.R;
import yuut.icinema.ui.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    //for changing headerImage
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;

    //for user information
    private static final String USER_INFO = "user information";
    private static final String USER_NAME = "user name";
    private static final String USER_INTRO = "user introduction";

    //the file for headerImage
    private static final String PICTURE_HEADER_FILE = "header.jpg";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.main_nav)
    NavigationView mNavView;
    @Bind(R.id.main_drawer)
    DrawerLayout mDrawer;

    private CircleImageView mNavImage;
    private CircleImageView mNavEdit;
    private TextView mUserName;
    private TextView mUserIntro;
    private MenuItem homeItem;

    private File mFile;

    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

    private String mTitle;

    /**
     * 记录系统时间，用于退出时做判断
     */
    private long exitTime = 0;

    private SharedPreferences userSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
        initEvent();
    }
    //初始化界面NavigatorView,并获取控件id
    private void initView() {
        homeItem = mNavView.getMenu().findItem(R.id.nav_home);//首页
        View mNavHeader = mNavView.inflateHeaderView(R.layout.view_nav_header);
        mNavImage = (CircleImageView) mNavHeader.findViewById(R.id.iv_view_nav_image);
        mNavEdit = (CircleImageView) mNavHeader.findViewById(R.id.iv_view_nav_edit);
        mUserName = (TextView) mNavHeader.findViewById(R.id.tv_view_nav_name);
        mUserIntro = (TextView) mNavHeader.findViewById(R.id.tv_view_nav_intro);
    }
    //初始化数据
    private void initData() {
        mTitle = "首页";
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        //设置Drawer的开关 三
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer);
        mToggle.syncState();//将小箭头变成三
        mDrawer.setDrawerListener(mToggle);

        //初始化Viewpager
        mFragmentManager = getSupportFragmentManager();//获取当前碎片管理
        mCurFragment = mFragmentManager.findFragmentByTag(mTitle);
        //如果当前Fragment还未创建,则创建
        if (mCurFragment == null) {
            Fragment homeFragment = new HomeFragment();
            //开启一个事务,并提交
            mFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.rl_main_container, homeFragment, mTitle)
                    .commit();
            mCurFragment = homeFragment;
        }
        //-------------------未完待续

    }
    private void initEvent() {
        mNavView.setNavigationItemSelectedListener(this);
        mNavEdit.setOnClickListener(this);
        mNavImage.setOnClickListener(this);
    }
    //点击  编辑按钮  和  头像  响应点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_view_nav_edit:
                Toast.makeText(this, "iv_view_nav_edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_view_nav_image:
                Toast.makeText(this, "iv_view_nav_image", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
    //选中NavigationItem 响应事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "onNavigationItemSelected", Toast.LENGTH_SHORT).show();
        if (item.getItemId() == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        return false;
    }

    //页面跳转
    private void prepareIntent(Class cla) {
        this.startActivity(new Intent(MainActivity.this, cla));
    }
}
