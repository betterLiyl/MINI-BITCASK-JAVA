import core.KeyDir;
import core.MiniBitcask;
import core.ScanIter;
import util.ProtostuffUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Author: liyanlong
 * @Date: 2024-10-08 15:49
 **/

public class Main {

    public static final String path = "C:\\work\\JetBrains\\MINI-BITCASK-JAVA\\src\\main\\resources\\";
    public static final String fileName = "hello";

    public static void main(String[] args) {
//        MiniBitcask eng = new MiniBitcask(path + fileName);
//        assert eng.get("not exist".getBytes(StandardCharsets.UTF_8)) == null;
//
//        byte[] key = ProtostuffUtil.serializer("hello");
//        byte[] val = ProtostuffUtil.serializer("world");
//        eng.set(key, val);
//        byte[] getV = eng.get(key);
//        if (getV != null) {
//
//            System.out.println("get hello: " + ProtostuffUtil.deserializer(getV, String.class));
//        }
//        assert Arrays.equals(getV, val);
//
//        byte[] k1 = ProtostuffUtil.serializer("aa");
//        byte[] v1 = ProtostuffUtil.serializer("bb");
//        eng.set(k1, v1);
//        byte[] getV1 = eng.get(k1);
//        if (getV1 != null) {
//
//            System.out.println("get v1: " + ProtostuffUtil.deserializer(getV1, String.class));
//        }
//        assert Arrays.equals(getV1, v1);
//        //覆盖旧值
//        byte[] v11 = ProtostuffUtil.serializer("bb1");
//        eng.set(k1, v11);
//        byte[] getV11 = eng.get(k1);
//        if (getV11 != null) {
//
//            System.out.println("get v11: " + ProtostuffUtil.deserializer(getV11, String.class));
//        }
//        assert Arrays.equals(getV11, v11);
//
//        // 删除
//        eng.delete(k1);
//        assert eng.get(k1) == null;

        MiniBitcask eng2 = new MiniBitcask(path + "scan");
        eng2.set("apple".getBytes(StandardCharsets.UTF_8), "1".getBytes(StandardCharsets.UTF_8));
        eng2.set("banana".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8));
        eng2.set("cherry".getBytes(StandardCharsets.UTF_8), "3".getBytes(StandardCharsets.UTF_8));
        eng2.set("dare".getBytes(StandardCharsets.UTF_8), "4".getBytes(StandardCharsets.UTF_8));
        eng2.set("egg".getBytes(StandardCharsets.UTF_8), "5".getBytes(StandardCharsets.UTF_8));

        KeyDir.printAll();

        KeyDir.Bound<String> bound = new KeyDir.Bound<>(new KeyDir.Boundary<>("a", true), new KeyDir.Boundary<>("e", false));
        ScanIter scan = KeyDir.scan(bound, eng2.getLog());
        while (scan.hasNext()) {
            ScanIter.KVEntry next = scan.next();
            System.out.println(new String(next.getKey()) + ":" + new String(next.getValue()));
        }
    }
}
