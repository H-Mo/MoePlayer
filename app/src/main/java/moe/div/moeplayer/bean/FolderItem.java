package moe.div.moeplayer.bean;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * @author 林墨
 * @time 20/2/2  16:52
 * @desc 文件夹实体类
 */
public class FolderItem {

    private String title;       // 显示标题
    private String type;        // 类型
    private File file;          // 文件对象

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
