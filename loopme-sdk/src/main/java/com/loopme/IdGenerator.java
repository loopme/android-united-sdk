package com.loopme;

public class IdGenerator {
    private static int sIdCounter;

    private IdGenerator() {
    }

    public static int generateId() {
        return sIdCounter++;
    }
}
