package core;

import util.Constant;

import java.io.File;
import java.io.IOException;

/**
 * @Author: liyanlong
 * @Date: 2024-10-08 15:46
 **/

public class Log {

    private String path;

    private File file;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Log(String path) {
        File oldFile = new File(path);
        if (oldFile.exists()) {
            oldFile.delete();
        }

        try {
            File tempFile = File.createTempFile(path, Constant.MERGE_FILE_EXT);

            tempFile.setReadable(true);
            tempFile.setWritable(true);

            this.setPath(tempFile.getAbsolutePath());
            this.setFile(tempFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadIndex(){

    }
}
