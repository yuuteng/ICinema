package yuut.icinema.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import yuut.icinema.R;
import yuut.icinema.ui.fragment.CollectFragment;
import yuut.icinema.ui.fragment.HomeFragment;
import yuut.icinema.ui.fragment.TopFragment;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        DialogInterface.OnClickListener{

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
        //头像设置
        mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                PICTURE_HEADER_FILE);
        if (mFile.exists()) {
            Bitmap header = BitmapFactory.decodeFile(mFile.getPath());
            mNavImage.setImageBitmap(header);
        } else {
            mNavImage.setImageResource(R.mipmap.nav_icon);
        }
        mNavView.setItemIconTintList(null);
        //设置文字
        userSP = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        mUserName.setText(userSP.getString(USER_NAME, "NickName"));
        mUserIntro.setText(userSP.getString(USER_INTRO, "Introduction"));
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
            case R.id.iv_view_nav_image:
                new AlertDialog.Builder(MainActivity.this).
                        setTitle("请选择获得头像方式").
                        setItems(getResources().getStringArray(R.array.select_header_item),this).show();
                break;
            case R.id.iv_view_nav_edit:
                View v = LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.dialog_user_edit, null);
                final EditText nameEdit = (EditText) v.findViewById(R.id.edit_dialog_user_name);
                final EditText introEdit = (EditText) v.findViewById(R.id.edit_dialog_user_intro);
                new AlertDialog.Builder(MainActivity.this).setTitle("用户设置").
                        setView(v).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = userSP.edit();
                                String name = nameEdit.getText().toString().trim();
                                String intro = introEdit.getText().toString().trim();
                                if (name.equals("") && intro.equals("")) {
                                    dialogInterface.cancel();
                                    return;
                                }
                                if (!name.equals("")) {
                                    editor.putString(USER_NAME, name);
                                    mUserName.setText(name);
                                }
                                if (!intro.equals("")) {
                                    editor.putString(USER_INTRO, intro);
                                    mUserIntro.setText(intro);
                                }
                                editor.apply();
                                dialogInterface.cancel();
                            }
                        }).show();
                break;
            default:
                break;
        }
    }
    //选中NavigationItem 响应事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        item.setChecked(true);
        mDrawer.closeDrawers();
        //根据选择的标题,显示相应Fragment
        switchFragment(item.getTitle().toString());
        return true;
    }
    //换掉ViewPager里面的Fragment
    private void switchFragment(String title) {
        mTitle = title;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(title);
        if (fragment == null) {
            transaction.hide(mCurFragment);
            fragment = createFragmentByTitle(title);
            transaction.add(R.id.rl_main_container, fragment, title);
            mCurFragment = fragment;
        } else if (fragment != mCurFragment) {
            transaction.hide(mCurFragment).show(fragment);
            mCurFragment = fragment;
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                commit();
        supportInvalidateOptionsMenu();
        if (mTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(mTitle);
        }
    }
    /**
     * 根据MenuItem的title返回对应的fragment
     */
    private Fragment createFragmentByTitle(String title) {
        switch (title) {
            case "首页":
                return new HomeFragment();
            case "收藏":
                return new CollectFragment();
            case "Top250":
                return new TopFragment();
            default:
                return new HomeFragment();
        }
    }

    //页面跳转
    private void prepareIntent(Class cla) {
        this.startActivity(new Intent(MainActivity.this, cla));
    }
    //搜索框
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cel, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //执行搜索跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cel_search:
                prepareIntent(SearchActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                //打开系统图库
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                break;
            case 1:
                //打开系统相机
                //运行时权限申请
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},1);
                }else { //如果已经申请过权限,打开系统相机
                    openCamera();
                }
                break;
        }
        dialog.cancel();
    }
    //运行时权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    //调用摄像头
    private void openCamera(){
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }
    //
    public Uri getImageUri() {
//        PICTURE_HEADER_FILE: header.jpg
        return Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                PICTURE_HEADER_FILE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    resizeImage(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    resizeImage(getImageUri());
                    break;
                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        showResizeImage(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪获得的图片
     */
    private void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }
    /**
     * 显示得到的经裁剪后的头像并保存
     */
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            mNavImage.setImageBitmap(photo);
            saveBitmap(photo);
        }
    }
    /**
     * 将得到头像图片保存到手机中
     */
    private void saveBitmap(Bitmap bitmap) {
        if (mFile.exists()) {
            mFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 处理返回键逻辑或者使用onBackPressed()
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mCurFragment instanceof HomeFragment) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    this.finish();
                    System.exit(0);
                }
            } else {
                switchFragment("首页");
                homeItem.setChecked(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
