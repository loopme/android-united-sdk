package com.loopme.video360;

public class MD360DirectorFactory {
    public static MD360Director createDirector(){
        return MD360Director.builder().build();
    }
}
