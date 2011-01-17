package group.jinjie.qq.model;

public class YouKuRelatedUrlFactory extends YouKuUrlFactory {
    private String mVidStr;

    public YouKuRelatedUrlFactory(String vid) {
        this.mVidStr = vid;
    }

    @Override
    public String newUrlInstance() {
        return bulidUrl("relatedVideos", mPidStr, "vid=" + mVidStr, mRtStr,
                mPzStr, mLenStr, mFStr);
    }

}
