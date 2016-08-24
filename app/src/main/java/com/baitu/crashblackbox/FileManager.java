package com.baitu.crashblackbox;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by baitu on 16/8/24.
 */
public class FileManager {

    public static void saveCrashTraceSync(String crashTrace){
        JSONArray jsonArray = new JSONArray();
        CrashTraceInfo crashTraceInfo = new CrashTraceInfo(crashTrace);
        ArrayList<CrashTraceInfo> traceInfoList = getCrashTraceInfoSync();
        traceInfoList.add(crashTraceInfo);
        for(int i = 0; i < traceInfoList.size(); i++){
            JSONObject jsonObject = traceInfoList.get(0).getJsonObject();
            jsonArray.put(jsonObject);
        }
        Utils.saveJsonToFile(jsonArray.toString(), Constant.FILE_CRASH_INFO);
    }

    public static void saveCrashTraceAsync(final String crashTrace){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveCrashTraceSync(crashTrace);
            }
        }).start();
    }

    public static ArrayList<CrashTraceInfo> getCrashTraceInfoSync(){
        String traceInfo = Utils.getJsonFromFile(Constant.FILE_CRASH_INFO);
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
                ArrayList<CrashTraceInfo> traceInfoList = getCrashTraceInfoSync();
                if(listener != null){
                    listener.onSuccess(traceInfoList);
                }
            }
        }).start();;
    }

    public interface GetCrashTraceInfoListener{
        void onSuccess(ArrayList<CrashTraceInfo> traceInfoList);
    }
}
