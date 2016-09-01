package com.baitu.crashblackbox.crash;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baitu on 16/8/24.
 */
public class CrashTraceInfo {

    public static final String KEY_INFO = "crashInfo";
    public static final String KEY_TIME = "time";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_SDCARD_SIZE = "sdcardSize";
    public static final String KEY_SDCARD_CAN_WRITE = "sdcardCanWrite";

    private String mTime;
    private String mNetwork;
    private String mSDCardSize;
    private boolean mSDCardCanWrite;
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

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getNetwork() {
        return mNetwork;
    }

    public void setNetwork(String network) {
        mNetwork = network;
    }

    public String getSDCardSize() {
        return mSDCardSize;
    }

    public void setSDCardSize(String SDCardSize) {
        mSDCardSize = SDCardSize;
    }

    public boolean isSDCardCanWrite() {
        return mSDCardCanWrite;
    }

    public void setSDCardCanWrite(boolean SDCardCanWrite) {
        mSDCardCanWrite = SDCardCanWrite;
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_INFO, mCrashInfo);
            jsonObject.put(KEY_TIME, mTime);
            jsonObject.put(KEY_NETWORK, mNetwork);
            jsonObject.put(KEY_SDCARD_SIZE, mSDCardSize);
            jsonObject.put(KEY_SDCARD_CAN_WRITE, mSDCardCanWrite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static CrashTraceInfo obtain(JSONObject jsonObject){
        CrashTraceInfo crashTraceInfo = new CrashTraceInfo("");
        try {
            String info = jsonObject.getString(KEY_INFO);
            String network = jsonObject.getString(KEY_NETWORK);
            String sdcardSize = jsonObject.getString(KEY_SDCARD_SIZE);
            String time = jsonObject.getString(KEY_TIME);
            boolean sdcardCanWrite = jsonObject.getBoolean(KEY_SDCARD_CAN_WRITE);
            crashTraceInfo.setCrashInfo(info);
            crashTraceInfo.setNetwork(network);
            crashTraceInfo.setSDCardSize(sdcardSize);
            crashTraceInfo.setSDCardCanWrite(sdcardCanWrite);
            crashTraceInfo.setTime(time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return crashTraceInfo;
    }
}
