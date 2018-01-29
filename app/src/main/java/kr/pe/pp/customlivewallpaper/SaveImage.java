package kr.pe.pp.customlivewallpaper;

/**
 * Created by Administrator on 2018-01-29.
 */

public class SaveImage {
    private String path = "";
    private Integer rotate = 0;

    public SaveImage(String path, Integer rotate) {
        this.path = path;
        this.rotate = rotate;
    }
    public SaveImage(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Integer getRotate() {
        return this.rotate;
    }
    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }
}
