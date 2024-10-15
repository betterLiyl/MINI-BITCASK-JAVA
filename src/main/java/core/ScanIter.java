package core;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * @Author: liyanlong
 * @Date: 2024-10-08 15:47
 **/

public class ScanIter {

    private Log log;

    private SortedMap<byte[], KeyDir.ValueEntry> range;

    private Iterator<Map.Entry<byte[], KeyDir.ValueEntry>> iterator;

    public SortedMap<byte[], KeyDir.ValueEntry> getRange() {
        return range;
    }

    public void setRange(SortedMap<byte[], KeyDir.ValueEntry> range) {
        this.range = range;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public ScanIter(Log log, SortedMap<byte[], KeyDir.ValueEntry> range) {
        this.log = log;
        this.range = range;
        this.iterator = range.entrySet().iterator();
    }

    public KVEntry map(byte[] key, KeyDir.ValueEntry valueEntry) {
        byte[] bytes = this.log.readValue(valueEntry.getOffset(), valueEntry.getLength());
        return new KVEntry(key, bytes);
    }

    public KVEntry next() {
        Map.Entry<byte[], KeyDir.ValueEntry> next = this.iterator.next();
        return this.map(next.getKey(), next.getValue());
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public static class KVEntry {
        private byte[] key;
        private byte[] value;

        public KVEntry(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        public KVEntry() {
        }

        public byte[] getKey() {
            return key;
        }

        public void setKey(byte[] key) {
            this.key = key;
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }
    }
}
