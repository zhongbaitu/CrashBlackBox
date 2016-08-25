package com.baitu.crashblackbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class CrashInfoListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CrashInfoListAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_info_list);
        init();
        loadCrashInfoData();
    }

    public static void start(Context context){
        context.startActivity(new Intent(context, CrashInfoListActivity.class));
    }

    private void init(){
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_crash_info);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(null);
        mAdapter = new CrashInfoListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadCrashInfoData(){
        FileManager.getCrashTraceInfoAsync(new FileManager.GetCrashTraceInfoListener() {
            @Override
            public void onSuccess(ArrayList<CrashTraceInfo> traceInfoList) {
                mAdapter.refreshData(traceInfoList);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private static class CrashInfoListAdapter extends RecyclerView.Adapter {

        private ArrayList<CrashTraceInfo> mDataList;

        public void refreshData(ArrayList<CrashTraceInfo> dataList){
            mDataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            if(mDataList != null){
                return mDataList.size();
            }
            return 0;
        }
    }
}
