package net.kamfat.omengo.component;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kamfat.omengo.OmengoApplication;
import net.kamfat.omengo.R;
import net.kamfat.omengo.base.BaseActivity;
import net.kamfat.omengo.bean.ServerBean;
import net.kamfat.omengo.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/20.
 */
public class HomeHeaderView extends LinearLayout {

    ScrollAdverView adverView;
    ViewPager pagerView;
    ScrollAdverView.OnSingleTouchListener singleTouchListener;
    int itemWidth;

    public HomeHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public HomeHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        View.inflate(context, R.layout.main_header, this);

        adverView = (ScrollAdverView) findViewById(R.id.adver_scroll_view);
        pagerView = (ViewPager) findViewById(R.id.adver_view_pager);
        itemWidth = (int) (OmengoApplication.getInstance().getScreen_width() / 5f);
    }

    OnClickListener listener;
    public void bind(OnClickListener clickListener, ScrollAdverView.OnSingleTouchListener singleTouchListener){
        this.listener = clickListener;
        this.singleTouchListener = singleTouchListener;
    }

    public void initViewSize(int width) {
        int adverViewHeight = (int) (2 * width / 5f);
        LinearLayout.LayoutParams lp = (LayoutParams) adverView.getLayoutParams();
        lp.height = adverViewHeight;
    }

    private void initViewPager(View[] views) {
        final View view = views[0];
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        LinearLayout.LayoutParams lp = (LayoutParams) pagerView.getLayoutParams();
        lp.height = view.getMeasuredHeight(); // 获取高度
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(views);
        pagerView.setAdapter(pagerAdapter);
    }

    public void setData(BaseActivity activity, ArrayList<ServerBean> adverList,
                        ArrayList<ServerBean> serverList) {
        // 设置广告
        adverView.setData(activity, adverList);
        adverView.setOnSingleTouchListener(singleTouchListener);
        // 设置服务
        if (serverList != null) {
            int size = serverList.size();
            int page = (int) Math.ceil(size / 10.0f);
            View[] views = new View[page];
            for (int i = 0; i < page; i++) {
                ArrayList<ServerBean> list = new ArrayList<>();
                for (int j = 0; j < 10; j++) { // 每页显示10个icon
                    int p = i * 10 + j;
                    if (p < size) {
                        list.add(serverList.get(p));
                    } else {
                        break;
                    }
                }
                views[i] = initPageView(list);
            }
            initViewPager(views);
        }
    }

    LinearLayout.LayoutParams itemLayoutParams;

    private View initPageView(List<ServerBean> list) {
        LinearLayout linearLayout;
        if (itemLayoutParams == null) {
            itemLayoutParams = new LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int size = list.size();
        if (size > 5) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout child1 = getLineView();
            for (int i = 0; i < 5; i++) {
                View itemView = getItemView(list.get(i));
                itemView.setLayoutParams(itemLayoutParams);
                child1.addView(itemView);
            }
            linearLayout.addView(child1);

            LinearLayout child2 = getLineView();
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            child2.setLayoutParams(llp);
            for (int i = 5; i < size; i++) {
                View itemView = getItemView(list.get(i));
                itemView.setLayoutParams(itemLayoutParams);
                child2.addView(itemView);
            }
            linearLayout.addView(child2);
        } else {
            linearLayout = getLineView();
            for (ServerBean g : list) {
                View itemView = getItemView(g);
                itemView.setLayoutParams(itemLayoutParams);
                linearLayout.addView(itemView);
            }
        }
        return linearLayout;
    }

    private LinearLayout getLineView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        return linearLayout;
    }

    // 添加一个item view
    private View getItemView(ServerBean gb) {
        View v = View.inflate(getContext(), R.layout.item_server, null);
        ImageView iconView = (ImageView) v.findViewById(R.id.server_icon);
        TextView nameView = (TextView) v.findViewById(R.id.server_name);
        Tools.setImage((Activity) getContext(), iconView, gb.image);
        nameView.setText(gb.name);
        v.setTag(R.string.main_server, gb);
        v.setOnClickListener(listener);
        return v;
    }

    class MyPagerAdapter extends PagerAdapter {
        View[] views;

        public MyPagerAdapter(View[] views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views[position];
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}