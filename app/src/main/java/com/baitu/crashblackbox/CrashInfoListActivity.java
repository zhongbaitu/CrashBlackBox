package com.baitu.crashblackbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        mAdapter = new CrashInfoListAdapter(this);
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

    private static class CrashInfoListAdapter extends RecyclerView.Adapter<ItemHolder> {

        private ArrayList<CrashTraceInfo> mDataList;
        private Context mContext;

        public CrashInfoListAdapter(Context context) {
            mContext = context;
        }

        public void refreshData(ArrayList<CrashTraceInfo> dataList){
            mDataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_crash_info_list, null);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.setInfo(mDataList.get(position));
        }

        @Override
        public int getItemCount() {
            if(mDataList != null){
                return mDataList.size();
            }
            return 0;
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder{

        private View mParentView;
        private TextView mTitleTv;
        private TextView mTimeTv;

        public ItemHolder(View itemView) {
            super(itemView);
            mParentView = itemView;
            mTitleTv = (TextView) itemView.findViewById(R.id.tv_title);
            mTimeTv = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void setInfo(final CrashTraceInfo info){
            mTitleTv.setText(info.getCrashInfo());
            mTimeTv.setText(info.getTime());
            mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCrashDialog(info);
                }
            });
        }

        private void showCrashDialog(final CrashTraceInfo crashTraceInfo){
            CrashDialogController crashDialogController = new CrashDialogController(mParentView.getContext());
            crashDialogController.setCrashTraceInfo(crashTraceInfo);
            crashDialogController.showDialog();

        }
    }
}