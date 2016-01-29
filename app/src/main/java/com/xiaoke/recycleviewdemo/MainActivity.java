package com.xiaoke.recycleviewdemo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import space.sye.z.library.RefreshRecyclerView;
import space.sye.z.library.adapter.RefreshRecyclerViewAdapter;
import space.sye.z.library.listener.OnBothRefreshListener;
import space.sye.z.library.manager.RecyclerMode;
import space.sye.z.library.manager.RecyclerViewManager;

public class MainActivity extends AppCompatActivity {
    private List<String> mData=new ArrayList<>();
    private int counts=10;//这是一次加载10条数据的标记
    private View header,footer;
    private Toolbar toolbar;//顶部的toobar
    private RefreshRecyclerView recyclerView;
    public MyRecycleViewAdapter myRecycleViewAdapter;//数据适配器
    public static final int PULL_DOWN= 1;
    public static final int LOAD_MORE=2;
    public int page=1;//一次加载一页的数据

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PULL_DOWN:
                    mData.add(0,"我是最新的数据："+getCurrentTime());
                    break;
                case LOAD_MORE:
                    for(int i=0;i<10;i++){
                        mData.add("最新数据"+(counts+i));
                    }
                    //每加载一页的数据，counts就加10
                    counts+=10;
                    break;

            }
            recyclerView.onRefreshCompleted();
            myRecycleViewAdapter.notifyDataSetChanged();

        }
    };

    /**
     * 获取当前时间
     * @return
     */
    public String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化视图
        initView();
        //初始化数据
        initData();
        //把头部尾部的view给渲染出来
        initInflateView();

        //给RecycleView设置数据适配器
        myRecycleViewAdapter = new MyRecycleViewAdapter();
        RecyclerViewManager.with(myRecycleViewAdapter,new LinearLayoutManager(this))
                .setMode(RecyclerMode.BOTH)
                .addHeaderView(header)
                .addFooterView(footer)
                .setOnBothRefreshListener(new OnBothRefreshListener() {

                    @Override
                    public void onPullDown() {
                        //下拉刷新
                        //使用本地的数据，模拟网络请求
                        Message msg = Message.obtain();
                        msg.what=PULL_DOWN;
                        handler.sendMessageDelayed(msg,2000);
                    }

                    @Override
                    public void onLoadMore() {
                        //上拉加载
                        //使用本地的数据模拟网络请求
                        if(page>8){
                            Toast.makeText(MainActivity.this,"数据加载完毕！",Toast.LENGTH_LONG).show();
                            //刷新完毕
                            recyclerView.onRefreshCompleted();
                            return;
                        }
                        page++;
                        Message msg = Message.obtain();
                        msg.what=LOAD_MORE;
                        handler.sendMessageDelayed(msg,2000);
                    }
                })
                .setOnItemClickListener(new RefreshRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder viewHolder, int i) {
                        //点击那个recycleView
                        Toast.makeText(MainActivity.this,"最新数据"+i,Toast.LENGTH_LONG).show();
                    }
                })
                .into(recyclerView,this);


    }

    private class MyRecycleViewHolder extends RecyclerView.ViewHolder{

        public  TextView tv_item_item;
        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            //找到布局中的文字
            tv_item_item = (TextView) itemView.findViewById(R.id.tv_item_item);
        }
    }

    private class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewHolder>{
        @Override
        public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = View.inflate(MainActivity.this, R.layout.recycler_item, null);
            //这个holder可不能与ViewHolder一样，否则报错
            MyRecycleViewHolder holder = new MyRecycleViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
            //设置每一条item的数据
            holder.tv_item_item.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            //总数目
            return mData.size();
        }
    }

    private void initView(){
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置toolbar
        setSupportActionBar(toolbar);
        //找到RecycleView
        recyclerView = (RefreshRecyclerView) findViewById(R.id.recyclerView);
    }

    private void initData() {
        for (int i=0;i<counts;i++){
            mData.add("最新数据"+i);
        }
    }

    private void initInflateView() {
        //头部布局
        header = View.inflate(this, R.layout.recycler_header, null);
        //底部布局
        footer = View.inflate(this, R.layout.recycler_footer, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_linear:
                RecyclerViewManager.setLayoutManager(new LinearLayoutManager(this));
                break;
            case R.id.action_grid:
                RecyclerViewManager.setLayoutManager(new GridLayoutManager(this,2));
                break;
            case R.id.action_staggered:
                RecyclerViewManager.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
