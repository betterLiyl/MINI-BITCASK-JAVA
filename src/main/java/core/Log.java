package core;

import util.Constant;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public void loadIndex() {
        KeyDir.init();
//        健值的位数
        int bitLen = Constant.KEY_VAL_HEADER_LEN;

        long length = this.file.length();
        long pos = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            while (pos < length) {
                char[] lenBuf = {};

                int read = reader.read(lenBuf, (int) pos, bitLen);
                //二进制转十进制
                //key len
                int keyLen = Integer.parseInt(new String(lenBuf), 2);
                if (keyLen == -1 || keyLen == 0) {
                    pos += bitLen;
                    pos += bitLen;
                    continue;
                }
                //重置为空
//                lenBuf = new char[]{};

                reader.read(lenBuf, (int) pos + bitLen, bitLen);
                //value len
                int valLen = Integer.parseInt(new String(lenBuf), 2);
                //value的offset
                long valPos = pos + bitLen * 2 + keyLen;
                //key 内容
                reader.read(lenBuf, (int) pos + bitLen * 2, keyLen);

                if (valLen == -1 || valLen == 0) {
                    KeyDir.remove(lenBuf);
                    pos = valPos;
                } else {
                    KeyDir.put(lenBuf, valPos, valLen);
                    pos = (valPos + valLen);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据值偏移量，读取值
     */
    public byte[] readValue(long valOffset, long valLen) {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            char[] buff = {};
            reader.read(buff, (int) valOffset, (int) valLen);
            return KeyDir.transferKey(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // +-------------+-------------+----------------+----------------+
    // | key len(4)    val len(4)     key(varint)       val(varint)  |
    // +-------------+-------------+----------------+----------------+
    public void write(char[] key, char[] val) {
        int leyLen = key.length;
        int valLen = val.length;
        int total = Constant.KEY_VAL_HEADER_LEN * 2 + leyLen + valLen;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file))) {
//            bufferedWriter.append();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
