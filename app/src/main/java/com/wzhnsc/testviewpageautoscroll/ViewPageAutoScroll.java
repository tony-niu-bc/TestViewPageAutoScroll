package com.wzhnsc.testviewpageautoscroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.nostra13.universalimageloader.utils.StorageUtils.getOwnCacheDirectory;

/**
 * 自动且左右循环
 */
public class ViewPageAutoScroll extends FrameLayout {

    private Context mContext;

    // 标识是不是自动翻页
    private boolean mbIsAutoScroll = false;

    public ViewPageAutoScroll(Context context) {
        super(context);

        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.banner, this);
    }

    public ViewPageAutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.banner, this);
    }

    public ViewPageAutoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.banner, this);
    }

    public static class BannerInfo {
        // 标题
        String strTitle;

        public String getStrTitle() {
            return strTitle;
        }

        public void setStrTitle(String strTitle) {
            this.strTitle = strTitle;
        }

        // 背景图 URL
        String strBgUrl;

        public String getStrBgUrl() {
            return strBgUrl;
        }

        public void setStrBgUrl(String strBgUrl) {
            this.strBgUrl = strBgUrl;
        }
    }

    // 图片缓存路径
    public static final String IMAGE_CACHE_PATH = "imageloader/Cache";

    private ViewPager mViewPager;
    private ImageView mImagePage[] = new ImageView[3];

    // 滑动的图片集合
    private List<ImageView> mImageViewList;

    // 图片标题正文的那些点
    private List<View> mDotList;

    private TextView mtvTitle;
    private TextView mtvPagination;

    private ScheduledExecutorService mScheduledExecutorService;

    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions milOptions;

    // 轮播banner的数据
    private List<BannerInfo> mBannerDataList;

    private Handler mHandler = new Handler();

    private Runnable doUpdateUI = new Runnable() {
        public void run() {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    };

    private void initImageLoader() {
        // 获取 ImageLoader 实例
        mImageLoader = ImageLoader.getInstance();

        File cacheDir = getOwnCacheDirectory(mContext, IMAGE_CACHE_PATH);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .imageDownloader(new BaseImageDownloader(mContext))
                .build();

        mImageLoader.init(config);

        milOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnFail(R.drawable.top_banner_android)
                .showImageOnLoading(R.drawable.top_banner_android)
                .showImageForEmptyUri(R.drawable.top_banner_android)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .build();
    }

    // 轮播图模拟数据
    private List<BannerInfo> getTestData() {
        List<BannerInfo> listBannerInfo = new ArrayList<BannerInfo>();

        BannerInfo biData = new BannerInfo();
        biData.setStrTitle("这是第一页");
        biData.setStrBgUrl("http://g.hiphotos.baidu.com/image/w%3D310/sign=bb99d6add2c8a786be2a4c0f5708c9c7/d50735fae6cd7b8900d74cd40c2442a7d9330e29.jpg");
        listBannerInfo.add(biData);

        BannerInfo biData2 = new BannerInfo();
        biData2.setStrTitle("这是第二页");
        biData2.setStrBgUrl("http://g.hiphotos.baidu.com/image/w%3D310/sign=7cbcd7da78f40ad115e4c1e2672e1151/eaf81a4c510fd9f9a1edb58b262dd42a2934a45e.jpg");
        listBannerInfo.add(biData2);

//        BannerInfo biData3 = new BannerInfo();
//        biData3.setStrTitle("这是第三页");
//        biData3.setStrBgUrl("http://e.hiphotos.baidu.com/image/w%3D310/sign=392ce7f779899e51788e3c1572a6d990/8718367adab44aed22a58aeeb11c8701a08bfbd4.jpg");
//        listBannerInfo.add(biData3);
//
//        BannerInfo biData4 = new BannerInfo();
//        biData4.setStrTitle("这是第四页");
//        biData4.setStrBgUrl("http://d.hiphotos.baidu.com/image/w%3D310/sign=54884c82b78f8c54e3d3c32e0a282dee/a686c9177f3e670932e4cf9338c79f3df9dc55f2.jpg");
//        listBannerInfo.add(biData4);
//
//        BannerInfo biData5 = new BannerInfo();
//        biData5.setStrTitle("这是第五页");
//        biData5.setStrBgUrl("http://e.hiphotos.baidu.com/image/w%3D310/sign=66270b4fe8c4b7453494b117fffd1e78/0bd162d9f2d3572c7dad11ba8913632762d0c30d.jpg");
//        listBannerInfo.add(biData5);

        return listBannerInfo;
    }

    private void createImageView(int Position) {
        ImageView imageView = new ImageView(mContext);
        // 保持原图大小，以原图的几何中心点和ImagView的几何中心点为基准，只绘制ImagView大小的图像
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 异步加载图片
        mImageLoader.displayImage(mBannerDataList.get(Position).getStrBgUrl(), imageView, milOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.i("ViewPageAutoScroll", "onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Log.i("ViewPageAutoScroll", "onLoadingFailed");
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.i("ViewPageAutoScroll", "onLoadingComplete");
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                Log.i("ViewPageAutoScroll", "onLoadingCancelled");
            }
        });

        mImageViewList.add(imageView);
    }

    private void initInterface() {
        // 使用 ImageLoader 之前先要初始化
        initImageLoader();

        mScrollTask = new ScrollTask();

        // 广告数据
        mBannerDataList = getTestData();

        mImageViewList = new ArrayList<ImageView>();

        // 定义的五个指示点
        mDotList = new ArrayList<View>();
        View dot0 = findViewById(R.id.v_dot0);
        View dot1 = findViewById(R.id.v_dot1);
        View dot2 = findViewById(R.id.v_dot2);
        View dot3 = findViewById(R.id.v_dot3);
        View dot4 = findViewById(R.id.v_dot4);
        mDotList.add(dot0);
        mDotList.add(dot1);
        mDotList.add(dot2);
        mDotList.add(dot3);
        mDotList.add(dot4);

        mtvTitle = (TextView)findViewById(R.id.tv_title);
        // 设置标题
        mtvTitle.setText(mBannerDataList.get(0).getStrTitle());

        mtvPagination = (TextView)findViewById(R.id.tv_pagination);
        // 设置页码
        mtvPagination.setText(1 + "/" + mBannerDataList.size());

        mViewPager = (ViewPager)findViewById(R.id.vp_banner);
        // 设置填充ViewPager页面的适配器
        mViewPager.setAdapter(new ViewPageAdapter());
        // 设置一个监听器，当ViewPager中的页面改变时调用
        mViewPager.addOnPageChangeListener(new ViewPageChangeListener());

        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() / 2);

        ImageView ivMasking = (ImageView)findViewById(R.id.iv_masking);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.masking_banner);

        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ivMasking.setBackground(bd);
        }
        else {
            ivMasking.setBackgroundDrawable(bd);
        }

        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        for (int i = 0; i < mBannerDataList.size(); i++) {
            createImageView(i);

            mDotList.get(i).setVisibility(View.VISIBLE);
        }

        mImagePage[0] = new ImageView(mContext);
        mImagePage[0].setScaleType(ImageView.ScaleType.CENTER_CROP);

        mImagePage[1] = new ImageView(mContext);
        mImagePage[1].setScaleType(ImageView.ScaleType.CENTER_CROP);

        mImagePage[2] = new ImageView(mContext);
        mImagePage[2].setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private class ScrollTask implements Runnable {
        @Override
        public void run() {
            synchronized (mViewPager) {
                mHandler.post(doUpdateUI);
            }
        }
    }

    private ScrollTask mScrollTask;

    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {

        private int mOldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.i("ViewPageAutoScroll", "ViewPageChangeListener - onPageScrollStateChanged - arg0 = " + arg0);

            // 正在滑动
            if (1 == arg0) {
                // 手动翻页时要停了定时器
                if (!mbIsAutoScroll) {
                    mScheduledExecutorService.shutdown();
                }
            }
            // 滑动完毕
            else if (2 == arg0) {
                // 手动翻页时要停了定时器
                if (!mbIsAutoScroll) {
                    mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    // 当 ViewPage 显示出来后，每五秒切换一次图片显示
                    mScheduledExecutorService.scheduleAtFixedRate(mScrollTask, 5, 5, TimeUnit.SECONDS);
                }
                else {
                    mbIsAutoScroll = false;
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.i("ViewPageAutoScroll", "ViewPageChangeListener - onPageScrolled - " +
                                        "arg0 = " + arg0 +
                                        " arg1 = " + arg1 +
                                        " arg2 = " + arg2);
        }

        @Override
        public void onPageSelected(int position) {
            final int mapPos = position % mBannerDataList.size();

            Log.i("ViewPageAutoScroll", "ViewPageChangeListener - onPageScrollStateChanged - " +
                                        "position = " + position +
                                        " mapPos = " + mapPos);

            BannerInfo bannerInfo = mBannerDataList.get(mapPos);
            // 设置标题
            mtvTitle.setText(bannerInfo.getStrTitle());
            // 设置页码
            mtvPagination.setText(mapPos + 1 + "/" + mBannerDataList.size());

            mDotList.get(mOldPosition).setBackgroundResource(R.drawable.dot_normal);
            mDotList.get(mapPos).setBackgroundResource(R.drawable.dot_focused);
            mOldPosition = mapPos;
        }
    }

    private class ViewPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return (1 == mBannerDataList.size()) ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final int mapPos = position % mBannerDataList.size();

            Log.i("ViewPageAutoScroll", "ViewPageAdapter - instantiateItem - " +
                                        "position = " + position +
                                        " mapPos = " + mapPos);

            ImageView iv = mImagePage[position % 3];

            if (null != iv.getParent()) {
                ((ViewGroup)iv.getParent()).removeView(iv);
            }

            iv.setImageBitmap(((BitmapDrawable)mImageViewList.get(mapPos).getDrawable()).getBitmap());

            ((ViewPager)container).addView(iv);

            final BannerInfo bannerInfo = mBannerDataList.get(mapPos);
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理跳转逻辑
                    Toast.makeText(mContext, "You click" + mapPos + "Image", Toast.LENGTH_LONG)
                         .show();
                }
            });

            return iv;
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            Log.i("ViewPageAutoScroll", "ViewPageAdapter - destroyItem - arg1 = " + arg1);

            // 循环时不可以在这里释放重复利用的视图，
            // 因为左翻时是先instantiateItem创建新位置视图再在这里释放最右边的旧视图，就会出现空白页
            // 右翻时则是先释放，再创建
            /*
            11-21 18:43:41.910 19297-19297/com.wzhnsc.testviewpageautoscroll I/ViewPageAutoScroll: ViewPageAdapter - destroyItem - arg1 = 1073741826
            11-21 18:43:41.910 19297-19297/com.wzhnsc.testviewpageautoscroll I/ViewPageAutoScroll: ViewPageAdapter - instantiateItem - position = 1073741829 mapPos = 1
            11-21 18:45:45.180 19297-19297/com.wzhnsc.testviewpageautoscroll I/ViewPageAutoScroll: ViewPageAdapter - instantiateItem - position = 1073741826 mapPos = 0
            11-21 18:45:45.180 19297-19297/com.wzhnsc.testviewpageautoscroll I/ViewPageAutoScroll: ViewPageAdapter - destroyItem - arg1 = 1073741829
             */
//            ((ViewPager)arg0).removeView((View)arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup arg0) {
        }

        @Override
        public void finishUpdate(ViewGroup arg0) {
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initInterface();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当 ViewPage 显示出来后，每两秒切换一次图片显示
        mScheduledExecutorService.scheduleAtFixedRate(mScrollTask, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // 当 View 不可见的时候停止切换
        mScheduledExecutorService.shutdown();
    }
}
