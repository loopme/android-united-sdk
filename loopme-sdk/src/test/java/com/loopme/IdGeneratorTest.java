package com.loopme;

import org.junit.Assert;
import org.junit.Test;

public class IdGeneratorTest {

    @Test
    public void generateId() {
        Assert.assertEquals(0, IdGenerator.generateId());
        Assert.assertEquals(1, IdGenerator.generateId());
        Assert.assertEquals(2, IdGenerator.generateId());
        Assert.assertEquals(3, IdGenerator.generateId());
        Assert.assertEquals(4, IdGenerator.generateId());
        Assert.assertEquals(5, IdGenerator.generateId());
        Assert.assertEquals(6, IdGenerator.generateId());
        Assert.assertEquals(7, IdGenerator.generateId());
    }
}