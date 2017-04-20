package yuut.icinema.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;

/**
 * Created by yuut on 2017/4/12.
 * 3个小标题以及ViewPager
 * TabLayout
 * ViewPager
 */

public class HomeFragment extends Fragment {
    private static String[] TITLES = {"正在上映", "即将上映", "票房排行"};
    //标题栏
    @Bind(R.id.tab_home)
    TabLayout mTabLayout;
    //展示页
    @Bind(R.id.vp_home)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载子视图
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);//获取Fragment中控件的ID
        initData();//初始化数据
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);//释放绑定资源
    }

    private void initData() {
        PagerAdapter mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        //预先加载页面的个数,任何一个页面的左边可以预加载3个页面，右边也可以加载3页面
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);//Tag与ViewPager绑定
    }

    class HomePagerAdapter extends FragmentStatePagerAdapter{

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HomePagerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

}
