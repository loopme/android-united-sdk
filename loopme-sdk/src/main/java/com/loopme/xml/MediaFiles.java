package com.loopme.xml;

import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.InteractiveCreativeFile;
import com.loopme.xml.vast4.Mezzanine;

import java.util.List;

public class MediaFiles {

    @Tag("MediaFile")
    private List<MediaFile> mediaFileList;

    @Tag
    private Mezzanine mezzanine;

    @Tag
    private InteractiveCreativeFile interactiveCreativeFile;

    public List<MediaFile> getMediaFileList() {
        return mediaFileList;
    }

    public InteractiveCreativeFile getInteractiveCreativeFile() {
        return interactiveCreativeFile;
    }

    public Mezzanine getMezzanine() {
        return mezzanine;
    }
}
