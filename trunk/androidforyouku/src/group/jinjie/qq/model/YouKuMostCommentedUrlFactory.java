package group.jinjie.qq.model;

public class YouKuMostCommentedUrlFactory extends YouKuUrlFactory {

    private String mPage;

    public YouKuMostCommentedUrlFactory(String pg) {
        this.mPage = pg;
    }
    
    @Override
    public String newUrlInstance() {
        return bulidUrl("getVideosOrderByPv", mPidStr, mPzStr, mRtStr, mFStr,
                mLenStr, "pg=" + this.mPage);
    }

}
