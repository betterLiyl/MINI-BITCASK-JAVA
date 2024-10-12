import core.MiniBitcask;
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
        MiniBitcask eng = new MiniBitcask(path + fileName);
        assert eng.get("not exist".getBytes(StandardCharsets.UTF_8)) == null;

        byte[] key = ProtostuffUtil.serializer("hello");
        byte[] val = ProtostuffUtil.serializer("world");
        eng.set(key, val);
        byte[] getV = eng.get(key);
        if (getV != null) {

            System.out.println("get hello: " + ProtostuffUtil.deserializer(getV, String.class));
        }
        assert Arrays.equals(getV, val);

        byte[] k1 = ProtostuffUtil.serializer("aa");
        byte[] v1 = ProtostuffUtil.serializer("bb");
        eng.set(k1, v1);
        byte[] getV1 = eng.get(k1);
        if (getV1 != null) {

            System.out.println("get v1: " + ProtostuffUtil.deserializer(getV1, String.class));
        }
        assert Arrays.equals(getV1, v1);
        //覆盖旧值
        byte[] v11 = ProtostuffUtil.serializer("bb1");
        eng.set(k1, v11);
        byte[] getV11 = eng.get(k1);
        if (getV11 != null) {

            System.out.println("get v11: " + ProtostuffUtil.deserializer(getV11, String.class));
        }
        assert Arrays.equals(getV11, v11);

        // 删除
        eng.delete(k1);
        assert eng.get(k1) == null;
    }
}
