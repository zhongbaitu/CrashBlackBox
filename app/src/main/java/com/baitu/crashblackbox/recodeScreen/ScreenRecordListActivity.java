package com.baitu.crashblackbox.recodeScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baitu.crashblackbox.BlackBoxUtils;
import com.baitu.crashblackbox.FileManager;
import com.baitu.crashblackbox.R;

import java.io.File;
import java.util.ArrayList;

public class ScreenRecordListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ListAdapter mListAdapter;

    public static void start(Context context){
        context.startActivity(new Intent(context, ScreenRecordListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_record_list);

        init();
        loadData();
    }

    private void init(){
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_screen_record);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListAdapter = new ListAdapter(this);
        mRecyclerView.setAdapter(mListAdapter);
    }

    private void loadData(){
        FileManager.getScreenReccordListInfoAsync(new FileManager.GetScreenRecordListInfoListener() {
            @Override
            public void onSuccess(ArrayList<ScreenRecordInfo> dataList) {
                mListAdapter.refreshData(dataList);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private static class ListAdapter extends RecyclerView.Adapter<ItemHolder> {

        private ArrayList<ScreenRecordInfo> mDataList;
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        public void refreshData(ArrayList<ScreenRecordInfo> dataList){
            mDataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_screen_record_list, null);
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

        private Context mContext;
        private View mParentView;
        private ImageView mThumbnailIv;
        private TextView mCreatTimeTv;

        public ItemHolder(View itemView) {
            super(itemView);
            mParentView = itemView;
            mContext = mParentView.getContext();
            init();
        }

        private void init(){
            mThumbnailIv = (ImageView) mParentView.findViewById(R.id.image_thumbnail);
            mCreatTimeTv = (TextView) mParentView.findViewById(R.id.creat_time);
        }

        public void setInfo(final ScreenRecordInfo info){
            Bitmap bitmap;
            if(info.getType() == ScreenRecordInfo.TYPE_MP4){
                bitmap = ThumbnailUtils.createVideoThumbnail(info.getFilePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }else{
                bitmap = BitmapFactory.decodeFile(info.getFilePath());
            }
            mThumbnailIv.setImageBitmap(bitmap);
            mCreatTimeTv.setText(info.getCreatTime());
            mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayRecordActivity.play(mContext, info.getFilePath());
                }
            });
            mParentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BlackBoxUtils.sendFile(mContext, new File(info.getFilePath()));
                    return true;
                }
            });
        }
    }
}
