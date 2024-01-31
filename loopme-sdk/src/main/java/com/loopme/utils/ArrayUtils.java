package com.loopme.utils;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static boolean isArgumentsValid(Object... args) {
        return isArrayValid(args);
    }

    public static boolean isArrayValid(Object[] args) {
        if (args != null && args.length > 0) {
            for (Object object : args) {
                if (object == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
