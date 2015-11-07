package com.wzhnsc.testviewpageautoscroll;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ListViewActivity extends AppCompatActivity {

    private PullToRefreshListView mListView;
    private ListViewAdapter mListViewAdapter;

    public class ListViewAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;
        private Context mContext;

        public ListViewAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if ((null == convertView)
//             && (0 == position)) {
                // 经测试得出：
                // 含有 Fragment 的布局只能创建一个视图，
                // 创建第二个时报错：
                // android.view.InflateException: Binary XML file line #9: Error inflating class fragment
                // convertView = mLayoutInflater.inflate(R.layout.list_view_fragment_item, null);
//            }
//            else {
//                convertView = mLayoutInflater.inflate(R.layout.list_view_normal_item, null);
//            }

            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.list_view_frame_item, null);

                Fragment fragment = (Fragment)new FragmentBanner();
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.frame_banner, fragment)
                                           .commit();
            }

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_view);

        mListView = (PullToRefreshListView)findViewById(R.id.lv_banner);
        mListViewAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mListViewAdapter);
        mListViewAdapter.notifyDataSetChanged();
    }
}
