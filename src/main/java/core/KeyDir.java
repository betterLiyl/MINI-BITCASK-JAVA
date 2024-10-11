package core;

import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liyanlong
 * @Date: 2024-10-08 15:45
 **/

public class KeyDir {

    /**
     * key ---------> value
     * key's bytes -> value's offset in log file and length
     */
    public static SortedMap<byte[], ValueEntry> ENGINE;
    private static final ReentrantLock LOCK = new ReentrantLock();

    public static void init() {
        ENGINE = new TreeMap<>((b1, b2) -> {
            // 先比较长度
            if (b1.length != b2.length) {
                return Integer.compare(b1.length, b2.length);
            }
            // 长度相同，逐个比较字节
            for (int i = 0; i < b1.length; i++) {
                int result = Byte.compare(b1[i], b2[i]);
                if (result != 0) {
                    return result;
                }
            }
            return 0; // 完全相同
        });
    }

    public static void put(char[] key, Long offset, Integer length) {
        LOCK.lock();
        try {
            ENGINE.put(transferKey(key), new ValueEntry(offset, length));
        } finally {
            if (LOCK.isHeldByCurrentThread()) {
                LOCK.unlock();
            }
        }
    }

    public static void remove(char[] key) {
        LOCK.lock();
        try {
            ENGINE.remove(transferKey(key));
        } finally {
            if (LOCK.isHeldByCurrentThread()) {
                LOCK.unlock();
            }
        }
    }

    public static byte[] transferKey(char[] key) {
        return new String(key).getBytes(StandardCharsets.UTF_8);
    }

    public static class ValueEntry {
        private Long offset;
        private Integer length;

        public Long getOffset() {
            return offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public ValueEntry(Long offset, Integer length) {
            this.offset = offset;
            this.length = length;
        }

        public ValueEntry() {
        }
    }
}
