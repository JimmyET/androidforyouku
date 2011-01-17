package group.jinjie.qq.model;

public class YouKuMostViewedUrlFactory extends YouKuUrlFactory {
    private String mPage;
    private String mPd;

    public YouKuMostViewedUrlFactory(String pd, String pg) {
        this.mPd = pd;
        this.mPage = pg;
    }

    @Override
    public String newUrlInstance() {
        // TODO Auto-generated method stub
        return bulidUrl("getVideosOrderByPv", mPidStr, mPzStr, mRtStr, mFStr,
                "pd=" + this.mPd, mLenStr, "pg=" + this.mPage);
    }

}
