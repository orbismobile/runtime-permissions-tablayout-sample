package pe.com.orbis.runtimepermissions_sample.activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import pe.com.orbis.runtimepermissions_sample.R;
import pe.com.orbis.runtimepermissions_sample.view.fragment.CameraFragment;
import pe.com.orbis.runtimepermissions_sample.view.fragment.GalleryFragment;


/**
 * Created by Carlos Vargas on 13/05/16.
 * Alias: CarlitosDroid
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private String[] titles = new String[2];

    private List<Fragment> listFragment = new ArrayList<>();
    private List<String> listFragmentTitle = new ArrayList<>();

    private AppCompatTextView lblTitle1;
    private AppCompatTextView lblTitle2;
    private AppCompatImageView imgClose;
    private AppCompatImageView imgNext;

    private CameraFragment cameraFragment;
    private GalleryFragment galleryFragment;

    private boolean openPermissionDialogCameraOnlyOneTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lblTitle1 = (AppCompatTextView) findViewById(R.id.lblTitle1);
        lblTitle2 = (AppCompatTextView) findViewById(R.id.lblTitle2);
        imgClose = (AppCompatImageView) findViewById(R.id.imgClose);

        imgNext = (AppCompatImageView) findViewById(R.id.imgNext);

        titles[0] = "Galería";
        titles[1] = "Foto";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        listFragmentTitle.add(titles[0]);
        listFragmentTitle.add(titles[1]);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), listFragment, listFragmentTitle);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        if(savedInstanceState != null){
            galleryFragment = (GalleryFragment) adapter.getFragmentForPosition(0);
            cameraFragment = (CameraFragment) adapter.getFragmentForPosition(1);

            listFragment.add(galleryFragment);
            listFragment.add(cameraFragment);
            openPermissionDialogCameraOnlyOneTime = false;
        }else{
            galleryFragment = new GalleryFragment();
            cameraFragment = new CameraFragment();

            listFragment.add(galleryFragment);
            listFragment.add(cameraFragment);
        }

        adapter.notifyDataSetChanged();
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        imgNext.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float fraction = 1f - positionOffset;
                
                if(positionOffset > 0.5f){
                    if(openPermissionDialogCameraOnlyOneTime){
                        cameraFragment.tryGetPermission();
                        openPermissionDialogCameraOnlyOneTime =  false;
                    }
                }

                switch (position) {
                    case 0:
                        lblTitle1.setAlpha(fraction);
                        lblTitle2.setAlpha(positionOffset);
                        imgNext.setAlpha(fraction);

                        break;
                    case 1:
                        lblTitle1.setAlpha(0);
                        lblTitle2.setAlpha(1);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tab_profile_picture, null);
        tabOne.setText(titles[0]);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tab_profile_picture, null);
        tabTwo.setText(titles[1]);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgClose:
                finish();
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList, List<String> mFragmentTitleList) {
            super(fm);
            this.mFragmentList = mFragmentList;
            this.mFragmentTitleList = mFragmentTitleList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            Log.e("tag ","tag "+createdFragment.getTag());
            return createdFragment;
        }

        /**
         * @param containerViewId the ViewPager this adapter is being supplied to
         * @param id pass in getItemId(position) as this is whats used internally in this class
         * @return the tag used for this pages fragment
         */
        public String makeFragmentName(int containerViewId, long id) {
            return "android:switcher:" + containerViewId + ":" + id;
        }

        /**
         * @return may return null if the fragment has not been instantiated yet for that position - this depends on if the fragment has been viewed
         * yet OR is a sibling covered by {@link android.support.v4.view.ViewPager#setOffscreenPageLimit(int)}. Can use this to call methods on
         * the current positions fragment.
         */
        public @Nullable Fragment getFragmentForPosition(int position)
        {
            String tag = makeFragmentName(viewPager.getId(), getItemId(position));
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            return fragment;
        }
    }

}
