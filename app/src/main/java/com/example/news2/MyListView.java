package com.example.news2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MyListView extends ListView implements AbsListView.OnScrollListener{

    private View hdView,ftView;
    private int totalItemsCount;
    private int lastVisible,firstVisible;
    private LoadListener loadListener;
    private int topHeight;
    private int yLoad;
    boolean isLoading;
    private TextView hdText;
    private ProgressBar progressBar;

    public MyListView(Context context) {
        super(context);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        hdView = LinearLayout.inflate(context,R.layout.header_view,null);
        hdText = hdView.findViewById(R.id.text_header);
        progressBar = (ProgressBar) hdView.findViewById(R.id.progressbar_header);

        ftView = LinearLayout.inflate(context,R.layout.footer_view,null);
        hdView.measure(0,0);
        topHeight = hdView.getMeasuredHeight();
        hdView.setPadding(0,-topHeight,0,0);
        this.addHeaderView(hdView);
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                yLoad = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) getY();
                int paddingY = topHeight + (moveY-yLoad)/2;
                if(paddingY<0){
                    hdText.setText("...");
                    progressBar.setVisibility(View.GONE);
                }else if(paddingY>0){
                    hdText.setText("Release to refresh");
                    progressBar.setVisibility(View.GONE);
                }
                hdView.setPadding(0,paddingY,0,0);
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(firstVisible==0 && i==SCROLL_STATE_IDLE){
            hdView.setPadding(0,0,0,0);
            hdText.setText("Refreshing...");
            progressBar.setVisibility(View.VISIBLE);
            loadListener.PullLoad();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        this.firstVisible = i;
        this.lastVisible = i + i1;
        this.totalItemsCount = i2;
        if(i2!=0 && absListView.getLastVisiblePosition()==i2-1  && isLoading==false) {
            isLoading = true;
            this.addFooterView(ftView);
            loadListener.onLoad();
        }
    }

    public interface LoadListener{
        void onLoad();
        void PullLoad();
    }

    public void loadComplete() {
        isLoading = false;
        this.removeFooterView(ftView);
//        this.removeHeaderView(hdView);
        hdView.setPadding(0,-topHeight,0,0);
    }

    public void setInterface(LoadListener loadListener){
        this.loadListener=loadListener;
    }

}
