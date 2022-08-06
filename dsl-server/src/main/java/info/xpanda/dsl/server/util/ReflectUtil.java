package info.xpanda.dsl.server.util;

import java.lang.reflect.Constructor;

public class ReflectUtil {
    public static <T> T newInstance(String name) {
        try {
            Class clazz = Class.forName(name);
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}