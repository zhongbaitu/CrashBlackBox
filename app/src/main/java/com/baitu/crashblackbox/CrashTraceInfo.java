package com.baitu.crashblackbox;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baitu on 16/8/24.
 */
public class CrashTraceInfo {

    public static final String KEY_INFO = "info";

    private String mCrashInfo;

    public CrashTraceInfo() {
    }

    public CrashTraceInfo(String crashInfo) {
        mCrashInfo = crashInfo;
    }

    public String getCrashInfo() {
        return mCrashInfo;
    }

    public void setCrashInfo(String crashInfo) {
        mCrashInfo = crashInfo;
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_INFO, mCrashInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static CrashTraceInfo obtain(JSONObject jsonObject){
        CrashTraceInfo crashTraceInfo = new CrashTraceInfo("");
        try {
            String info = jsonObject.getString(KEY_INFO);
            crashTraceInfo.setCrashInfo(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return crashTraceInfo;
    }
}
