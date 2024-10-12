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
        String pathname = path + "." + Constant.MERGE_FILE_EXT;
        File oldFile = new File(pathname);
        if (oldFile.exists()) {
            if (oldFile.isDirectory()) {
                oldFile.delete();
            } else {
                this.setFile(oldFile);
                this.setPath(pathname);
            }
        } else {

            try {

                File tempFile = new File(pathname);

                tempFile.setReadable(true);
                tempFile.setWritable(true);

                this.setPath(tempFile.getAbsolutePath());
                this.setFile(tempFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void loadIndex() {
        KeyDir.init();
//        健值的位数
        int bitLen = Constant.KEY_VAL_HEADER_LEN;

        long length = this.file.length();
        long pos = 0;
//        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(this.file))) {
        try (RandomAccessFile reader = new RandomAccessFile(this.file, "r")) {
            while (pos < length) {
                byte[] lenBuf = new byte[bitLen];
                reader.seek(pos);
                int read = reader.read(lenBuf, 0, bitLen);
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

                reader.read(lenBuf, 0, bitLen);
                //value len
                int valLen = Integer.parseInt(new String(lenBuf), 2);
                //value的offset
                long valPos = pos + bitLen * 2 + keyLen;
                //key 内容
                lenBuf = new byte[keyLen];
                reader.read(lenBuf, 0, keyLen);

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
        System.out.println("加载索引完成---");
        KeyDir.printAll();
    }

    /**
     * 根据值偏移量，读取值
     */
    public byte[] readValue(long valOffset, long valLen) {
        try (RandomAccessFile raf = new RandomAccessFile(this.file, "r")) {
            raf.seek(valOffset);
            byte[] buff = new byte[(int) valLen];
            raf.read(buff, 0, (int) valLen);
            return buff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // +-------------+-------------+----------------+----------------+
    // | key len(4)    val len(4)     key(varint)       val(varint)  |
    // +-------------+-------------+----------------+----------------+
    public KeyDir.ValueEntry write(byte[] key, byte[] val) {
        int keyLen = key.length;
        int valLen = val.length;
        long offset = this.file.length();
        int totalLen = keyLen + valLen + Constant.KEY_VAL_HEADER_LEN * 2;
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(this.file, true))) {
            writer.write(toFixedBinary(keyLen, Constant.KEY_VAL_HEADER_LEN).getBytes());
            writer.write(toFixedBinary(valLen, Constant.KEY_VAL_HEADER_LEN).getBytes());
            writer.write(key);
            writer.write(val);
            writer.flush();
            //传参数
            return new KeyDir.ValueEntry(offset, totalLen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toFixedBinary(int number, int bitLength) {
        // 使用 Integer.toBinaryString() 转换为二进制，并使用 String.format 补齐位数
        String binaryString = Integer.toBinaryString(number);

        // 确保固定位数，如果不足则前面补0
        if (binaryString.length() > bitLength) {
            // 如果超出bit位数，截断超出的部分
            return binaryString.substring(binaryString.length() - bitLength);
        } else {
            // 如果不足，前面补0
            return String.format("%" + bitLength + "s", binaryString).replace(' ', '0');
        }
    }

}
