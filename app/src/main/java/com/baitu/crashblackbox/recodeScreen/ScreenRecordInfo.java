package com.baitu.crashblackbox.recodeScreen;

/**
 * Created by baitu on 16/9/24.
 */
public class ScreenRecordInfo {

    public static final int TYPE_MP4 = 0;
    public static final int TYPE_GIF = 1;

    private int mType;
    private String mFilePath;
    private String mFileName;
    private String mCreatTime;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getCreatTime() {
        return mCreatTime;
    }

    public void setCreatTime(String creatTime) {
        mCreatTime = creatTime;
    }
}
