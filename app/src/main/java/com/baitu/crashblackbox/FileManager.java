package com.baitu.crashblackbox;

import android.os.Looper;
import android.text.TextUtils;

import com.baitu.crashblackbox.crash.CrashTraceInfo;
import com.baitu.crashblackbox.recodeScreen.ScreenRecordInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by baitu on 16/8/24.
 */
public class FileManager {

    public static void saveCrashTraceSync(CrashTraceInfo crashTraceInfo){
        JSONArray jsonArray = new JSONArray();
        ArrayList<CrashTraceInfo> traceInfoList = getCrashTraceInfoSync();
        traceInfoList.add(crashTraceInfo);
        for(int i = 0; i < traceInfoList.size(); i++){
            JSONObject jsonObject = traceInfoList.get(0).getJsonObject();
            jsonArray.put(jsonObject);
        }
        BlackBoxUtils.saveJsonToFile(jsonArray.toString(), Constant.FILE_CRASH_INFO);
    }

    public static void saveCrashTraceAsync(final CrashTraceInfo crashTraceInfo){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveCrashTraceSync(crashTraceInfo);
            }
        }).start();
    }

    public static ArrayList<CrashTraceInfo> getCrashTraceInfoSync(){
        String traceInfo = BlackBoxUtils.getJsonFromFile(Constant.FILE_CRASH_INFO);
        ArrayList<CrashTraceInfo> traceInfoList = null;
        if(!TextUtils.isEmpty(traceInfo)){
            try {
                JSONArray jsonArray = new JSONArray(traceInfo);
                if(jsonArray != null){
                    traceInfoList = new ArrayList(jsonArray.length());
                    CrashTraceInfo crashTraceInfo;
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        crashTraceInfo = CrashTraceInfo.obtain(jsonObject);
                        traceInfoList.add(crashTraceInfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(traceInfoList == null){
            traceInfoList = new ArrayList<>(0);
        }
        return traceInfoList;
    }

    public static void getCrashTraceInfoAsync(final GetCrashTraceInfoListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<CrashTraceInfo> traceInfoList = getCrashTraceInfoSync();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        if(listener != null){
                            listener.onSuccess(traceInfoList);
                        }

                        Looper.loop();
                    }
                }).start();

            }
        }).start();;
    }

    public static void getScreenReccordListInfoAsync(final GetScreenRecordListInfoListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = BlackBoxUtils.getScreenRecordPath();
                File dir = new File(path);
                if(dir.exists()){
                    File[] files = dir.listFiles();
                    if(files != null && files.length > 0){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
                        final ArrayList<ScreenRecordInfo> dataList = new ArrayList<>(files.length);
                        for(int i = 0; i < files.length; i++){
                            File file = files[i];
                            String fileName = file.getName();
                            ScreenRecordInfo info = new ScreenRecordInfo();
                            info.setFileName(fileName);
                            info.setFilePath(file.getAbsolutePath());
                            if(fileName.endsWith("mp4")){
                                info.setType(ScreenRecordInfo.TYPE_MP4);
                            }else if(fileName.endsWith("gif")){
                                info.setType(ScreenRecordInfo.TYPE_GIF);
                            }
                            Date date=new Date(file.lastModified());
                            String time = dateFormat.format(date);
                            info.setCreatTime(time);
                            dataList.add(info);
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();

                                if(listener != null){
                                    listener.onSuccess(dataList);
                                }

                                Looper.loop();
                            }
                        }).start();
                    }
                }
            }
        }).start();
    }


    public interface GetScreenRecordListInfoListener{
        void onSuccess(ArrayList<ScreenRecordInfo> dataList);
    }

    public interface GetCrashTraceInfoListener{
        void onSuccess(ArrayList<CrashTraceInfo> traceInfoList);
    }
}
