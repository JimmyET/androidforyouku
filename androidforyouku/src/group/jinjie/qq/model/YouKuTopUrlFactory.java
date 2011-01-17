package group.jinjie.qq.model;

public class YouKuTopUrlFactory extends YouKuUrlFactory {
    private String mPage;
    
    public YouKuTopUrlFactory(String pg) {
        this.mPage = pg;
    }
    
    @Override
    public String newUrlInstance() {
        // TODO Auto-generated method stub
         return bulidUrl("getTopVideos", mPidStr, "mdn=354635034158902", 
                 "phone=1", "rt=0",
                mPzStr, mLenStr, "pg=" + this.mPage);
    }

}
