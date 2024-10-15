package core;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
        ENGINE = initMap();
    }

    public static TreeMap<byte[], ValueEntry> initMap() {
        return new TreeMap<>((b1, b2) -> {
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

    public static void put(byte[] key, Long offset, Integer length) {
        LOCK.lock();
        try {
            ENGINE.put(key, new ValueEntry(offset, length));
        } finally {
            if (LOCK.isHeldByCurrentThread()) {
                LOCK.unlock();
            }
        }
    }

    public static void remove(byte[] key) {
        LOCK.lock();
        try {
            ENGINE.remove(key);
        } finally {
            if (LOCK.isHeldByCurrentThread()) {
                LOCK.unlock();
            }
        }
    }

    public static void printAll() {
        System.out.println("ENGINE size: " + ENGINE.size());
        ENGINE.keySet().forEach(key -> System.out.println(new String(key)));
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

    public static <T> ScanIter scan(Bound<T> bound, Log log) {
        final SortedMap<byte[], ValueEntry> range = KeyDir.initMap();
//        ENGINE.subMap()
        ENGINE.entrySet().stream()
                .filter(entry -> {
                    byte[] key = entry.getKey();
                    String kStr = new String(key);
                    boolean b = bound.from.include ? kStr.compareTo((String) bound.from.value) >= 0 : kStr.compareTo((String) bound.from.value) > 0;
                    boolean b1 = bound.to.include ? kStr.compareTo((String) bound.to.value) <= 0 : kStr.compareTo((String) bound.to.value) < 0;
                    return b && b1;
                })
                .forEach(entry -> range.put(entry.getKey(), entry.getValue()));
        return new ScanIter(log, range);
    }

    public static class Bound<T> {
        private Boundary<T> from;
        private Boundary<T> to;

        public Boundary<T> include(T value) {
            return new Boundary<>(value, true);
        }

        public Boundary<T> exclude(T value) {
            return new Boundary<>(value, false);
        }


        public Bound(Boundary<T> from, Boundary<T> to) {
            this.from = from;
            this.to = to;
        }

        public Bound() {
        }

        public Boundary<T> getFrom() {
            return from;
        }

        public void setFrom(Boundary<T> from) {
            this.from = from;
        }

        public Boundary<T> getTo() {
            return to;
        }

        public void setTo(Boundary<T> to) {
            this.to = to;
        }
    }

    public static class Boundary<T> {
        private T value;
        private boolean include;

        public Boundary(T value, boolean include) {
            this.value = value;
            this.include = include;
        }

        public Boundary() {
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public boolean isInclude() {
            return include;
        }

        public void setInclude(boolean include) {
            this.include = include;
        }
    }

}
