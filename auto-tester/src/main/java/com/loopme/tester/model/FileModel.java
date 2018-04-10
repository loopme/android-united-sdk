package com.loopme.tester.model;

public class FileModel implements Comparable<FileModel> {

    public static final int UP_FOLDER = 0;
    public static final int DIRECTORY = 1;
    public static final int FILE = 2;

    private String mFileName;
    private int mFileType;

    public FileModel(final String fileName, final int fileType) {

        if (fileType != UP_FOLDER && fileType != DIRECTORY && fileType != FILE) {
            throw new IllegalArgumentException();
        }
        this.mFileName = fileName;
        this.mFileType = fileType;
    }

    @Override
    public int compareTo(FileModel fileModel) {
        if (fileModel == null) {
            return -1;
        }
        if (mFileType != fileModel.mFileType) {
            return mFileType - fileModel.mFileType;
        } else {
            return mFileName.compareTo(fileModel.mFileName);
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public int getFileType() {
        return mFileType;
    }
}
