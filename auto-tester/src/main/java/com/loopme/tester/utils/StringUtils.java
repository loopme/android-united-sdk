package com.loopme.tester.utils;

/**
 * Created by katerina on 2/15/17.
 */

public class StringUtils {

    public static String capitalizeFirstLetter(String string) {
        String lowerCaseString = string.toLowerCase();
        if (lowerCaseString.length() > 2) {
            return Character.toString(lowerCaseString.charAt(0)).toUpperCase() + lowerCaseString.substring(1);
        } else {
            return string;
        }
    }

}



