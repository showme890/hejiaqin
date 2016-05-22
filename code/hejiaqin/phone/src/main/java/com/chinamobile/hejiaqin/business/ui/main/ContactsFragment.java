package com.chinamobile.hejiaqin.business.ui.main;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.hejiaqin.R;
import com.chinamobile.hejiaqin.business.ui.basic.BasicFragment;
import com.chinamobile.hejiaqin.business.ui.contact.fragment.AppContactListFragment;
import com.chinamobile.hejiaqin.business.ui.contact.fragment.SysContactListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:联系人列表页面
 * author: tonghui
 * Created: 2016/4/22.
 */
public class ContactsFragment extends BasicFragment {

    /**
     * 作为页面容器的ViewPager.
     */
    ViewPager mViewPager;

    /**
     * 应用内联系人布局
     */
    View mAppContactsLay;

    /**
     * 系统联系人布局
     */
    View mSysContactsLay;

    private List<Fragment> fragmentList;

    private TextView[] mTextView = new TextView[2];
    private ImageView[] mImageView = new ImageView[2];
    private final int mAppContactsIndex = 0;
    private final int mSysContactsIndex = 1;
    //当前选中的项
    int currentIndex = -1;

    private BackListener listener = new BackListener() {
        public void onAction(int actionId, Object obj) {

        }
    };

    @Override
    protected void handleFragmentMsg(Message msg) {

    }

    @Override
    protected void handleLogicMsg(Message msg) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact_list;
    }

    @Override
    protected void initView(View view) {
        mAppContactsLay = view.findViewById(R.id.app_contact_layout);
        mSysContactsLay = view.findViewById(R.id.sys_contact_layout);
        mTextView[0] = (TextView) view.findViewById(R.id.app_contact_text);
        mTextView[1] = (TextView) view.findViewById(R.id.sys_contact_text);
        mImageView[0] = (ImageView) view.findViewById(R.id.app_contact_img);
        mImageView[1] = (ImageView) view.findViewById(R.id.sys_contact_img);
        mViewPager = (ViewPager) view.findViewById(R.id.contact_viewpager);

        fragmentList = new ArrayList<Fragment>();
        BasicFragment appContactListFragment = new AppContactListFragment();
        appContactListFragment.setActivityListener(listener);
        fragmentList.add(appContactListFragment);

        BasicFragment sysContactListFragment = new SysContactListFragment();
        sysContactListFragment.setActivityListener(listener);
        fragmentList.add(sysContactListFragment);

        mViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
    }

    @Override
    protected void initData() {

    }

    /**
     * 定义自己的ViewPager适配器。
     */
    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem == currentIndex) {
                return;
            }
            mTextView[currentItem].setTextColor(getResources().getColor(R.color.navigation_selected));
            // mImageView[currentItem].setImageResource(R.mipmap.bottom_line);
            if (currentIndex != -1) {
                mTextView[currentIndex].setTextColor(getResources().getColor(R.color.navigation_unselected));
                mImageView[currentIndex].setImageDrawable(null);
            }
            currentIndex = mViewPager.getCurrentItem();
        }

    }
}
