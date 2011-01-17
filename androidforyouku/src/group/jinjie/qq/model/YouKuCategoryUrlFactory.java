package group.jinjie.qq.model;

public class YouKuCategoryUrlFactory extends YouKuUrlFactory {
    private String mCid;
    private String mPage;

    public YouKuCategoryUrlFactory(String cid, String pg) {
        this.mCid = cid;
        this.mPage = pg;
    }

    @Override
    public String newUrlInstance() {
        return bulidUrl("listChannelVideos", mPidStr, "cid=" + mCid, mPzStr,
                mRtStr, mFStr, mLenStr, "pg=" + mPage);
    }

}
