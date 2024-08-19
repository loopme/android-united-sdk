package com.loopme;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class JSONBuilderTest {

    @Test
    public void build() {
        JSONBuilder jsonBuilder = new JSONBuilder();
        assertEquals("{}", jsonBuilder.build().toString());
    }

    @Test
    public void putNull() {
        JSONBuilder jsonBuilder = new JSONBuilder();
        jsonBuilder.put("key", null);
        assertEquals("{}", jsonBuilder.build().toString());
    }

    @Test
    public void putNullKey() {
        JSONBuilder jsonBuilder = new JSONBuilder();
        jsonBuilder.put(null, "value");
        assertEquals("{}", jsonBuilder.build().toString());
    }

    @Test
    public void putNullKeyAndValue() {
        JSONBuilder jsonBuilder = new JSONBuilder();
        jsonBuilder.put(null, null);
        assertEquals("{}", jsonBuilder.build().toString());
    }
}