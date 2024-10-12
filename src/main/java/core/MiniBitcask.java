package core;

/**
 * @Author: liyanlong
 * @Date: 2024-10-08 15:47
 **/

public class MiniBitcask {
    private Log log;

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }


    public MiniBitcask() {
    }

    public MiniBitcask(String path) {
        Log log = new Log(path);
        log.loadIndex();
        this.log = log;
    }

    public void set(byte[] key, byte[] val) {

        KeyDir.ValueEntry write = this.log.write(key, val);
        //val offset
        Long offset = write.getOffset() + write.getLength() - val.length;
        KeyDir.put(key, offset, val.length);
    }

    public byte[] get(byte[] key) {
        KeyDir.ValueEntry valueEntry = KeyDir.ENGINE.get(key);
        if (valueEntry == null) {
            return null;
        }
        return this.log.readValue(valueEntry.getOffset(), valueEntry.getLength());
    }

    public void delete(byte[] key) {
        this.log.write(key, new byte[]{});
        KeyDir.remove(key);
    }

    public void flush() {

    }
}
