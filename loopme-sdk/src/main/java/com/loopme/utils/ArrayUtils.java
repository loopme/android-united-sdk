package com.loopme.utils;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static boolean isArgumentsValid(Object... args) {
        return isArrayValid(args);
    }

    public static boolean isArrayContainsInteger(Object[] args) {
        if (isArrayValid(args)) {
            for (Object object : args) {
                if (!(object instanceof Integer)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isArrayContainsStrings(Object[] args) {
        if (isArrayValid(args)) {
            for (Object object : args) {
                if (!(object instanceof String)) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
