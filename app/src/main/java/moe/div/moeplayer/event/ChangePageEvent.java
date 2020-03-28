package moe.div.moeplayer.event;

import java.io.Serializable;

/**
 * @author 林墨
 * @time 20/2/2  14:13
 * @desc 切换页面事件
 */
public class ChangePageEvent implements Serializable {

    public static final int Page_Start = 0xa01;
    public static final int Page_Data_List = 0xa02;
    public static final int Page_Explorer = 0xa03;

    private int mPage;

    public ChangePageEvent(int page){
        mPage = page;
    }

    public int getPage() {
        return mPage;
    }
}
