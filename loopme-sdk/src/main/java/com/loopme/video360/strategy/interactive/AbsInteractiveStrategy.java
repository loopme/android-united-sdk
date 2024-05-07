package com.loopme.video360.strategy.interactive;

import com.loopme.video360.MD360Director;
import com.loopme.video360.strategy.IModeStrategy;

import java.util.List;

public abstract class AbsInteractiveStrategy implements IModeStrategy, IInteractiveMode {
    private final List<MD360Director> mDirectorList;

    public AbsInteractiveStrategy(List<MD360Director> mDirectorList) {
        this.mDirectorList = mDirectorList;
    }

    protected List<MD360Director> getDirectorList() {
        return mDirectorList;
    }
}
