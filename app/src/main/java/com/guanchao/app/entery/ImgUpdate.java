package com.guanchao.app.entery;

/**
 * Created by 王建法 on 2017/6/16.
 */

public class ImgUpdate {

    /**
     * data : {"fileId":"78","fileName":"1497602969142.png","filePath":"http://218.92.200.222:8081/imageServ/2017\\06\\16\\fe58cc9e-e8e6-4180-9491-0891f2a54a27.png"}
     * message : 上传文件成功
     * success : true
     */


    /**
     * fileId : 78
     * fileName : 1497602969142.png
     * filePath : http://218.92.200.222:8081/imageServ/2017\06\16\fe58cc9e-e8e6-4180-9491-0891f2a54a27.png
     */

    private String fileId;  //--文件ID
    private String fileName;  //--文件名称
    private String filePath;  //--文件访问URL


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
