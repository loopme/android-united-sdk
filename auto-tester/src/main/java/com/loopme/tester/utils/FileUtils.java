package com.loopme.tester.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.loopme.Constants;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.model.FileModel;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

public class FileUtils {

    private static final String NOTATIONS = buildNotations();

    private static final String ITEM_BACK = "../";
    private static final String FILTER_ALLOW_ALL = "*.*";
    public static final String FILTER_TXT_FILE = ".txt";
    public static final String FILTER_JSON_FILE = ".json";

    private static String buildNotations() {
        return "/*\n Allowed values for : "
                + "adType : " + AdType.INTERSTITIAL + ", " + AdType.BANNER + "; "
                + "sdk : " + AdSdk.LOOPME + ", " + AdSdk.LMVPAID + ", " + AdSdk.MOPUB + " \n */ \n";
    }

    public static String addNotations(String contentToSave) {
        return NOTATIONS + contentToSave;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static boolean saveFile(File file, List<AdSpot> adSpotList) {
        if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
            File fileToSave = new File(addExtension(file.getAbsolutePath()));
            try (FileWriter fileWriter = new FileWriter(fileToSave, false)) {
                fileWriter.write(buildFileContent(adSpotList));
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String buildFileContent(List<AdSpot> adSpotList) throws JSONException, JsonProcessingException {
        return addNotations(getKeysInJson(adSpotList));
    }

    public static ArrayList<AdSpot> readFromFile(File file) {
        ArrayList<AdSpot> adSpotList = new ArrayList<>();
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                JsonFactory jsonFactory = new JsonFactory();
                JsonParser jsonParser = jsonFactory.createParser(fileInputStream);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                jsonParser.setCodec(objectMapper);
                jsonParser.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                jsonParser.nextToken();

                while (jsonParser.hasCurrentToken()) {
                    AdSpot[] adSpots = jsonParser.readValueAs(AdSpot[].class);
                    jsonParser.nextToken();
                    adSpotList.addAll(buildKeysList(adSpots));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adSpotList;
    }

    private static List<AdSpot> buildKeysList(AdSpot[] adSpots) {
        for (AdSpot adSpot : adSpots) {
            adSpot.setBaseUrl(Constants.BASE_URL);
        }
        return Arrays.asList(adSpots);
    }

    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static String getKeysInJson(List<AdSpot> keysList) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setVisibility(FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(keysList);
    }

    public static String readFromFile(String path) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<FileModel> getSubDirectories(File directory, ArrayList<FileModel> filesList) {
        File filesArray[] = directory.listFiles();
        if (filesArray != null) {
            for (File file : filesArray) {
                if (FileUtils.isFilePermitted(file, FILTER_JSON_FILE)) {
                    int type = file.isDirectory() ? FileModel.DIRECTORY : FileModel.FILE;
                    filesList.add(new FileModel(file.getName(), type));
                }
            }
            Collections.sort(filesList);
            return filesList;
        } else {
            return new ArrayList<>();
        }
    }

    public static boolean isFilePermitted(File file, final String filter) {
        return isAllowAllFilter(filter) || file.isDirectory() || isExtensionPermitted(file.getName(), filter);
    }

    public static boolean isAllowAllFilter(String filter) {
        return TextUtils.equals(filter, FILTER_ALLOW_ALL);
    }

    public static boolean isExtensionPermitted(String fileName, String filter) {
        if (fileName != null && hasExtension(fileName)) {
            int lastIndexOfPoint = fileName.lastIndexOf('.');
            String fileType = fileName.substring(lastIndexOfPoint).toLowerCase();
            return fileType.compareTo(filter) == 0;
        }
        return false;
    }

    private static boolean hasExtension(String fileName) {
        int lastIndexOfPoint = fileName.lastIndexOf('.');
        return lastIndexOfPoint != -1;
    }

    public static String addExtension(String path) {
        if (hasExtension(path)) {
            return path;
        }
        return path.concat(FileUtils.FILTER_JSON_FILE);
    }

    public static boolean makeDirectory(String path, String newFolderName) {
        return new File(path, newFolderName).mkdir();
    }

    public static ArrayList<FileModel> getAllFiles(File file) {
        if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
            return getSubDirectories(file);
        } else {
            return new ArrayList<>();
        }
    }

    private static ArrayList<FileModel> getSubDirectories(File file) {
        ArrayList<FileModel> subDirectoriesList = new ArrayList<>();
        if (file != null && !TextUtils.equals(file.getAbsolutePath(),
                Environment.getExternalStorageDirectory().getAbsolutePath())) {
            subDirectoriesList.add(new FileModel(ITEM_BACK, FileModel.UP_FOLDER));
        }
        return FileUtils.getSubDirectories(file, subDirectoriesList);
    }

    public static String getLogContent() {
        if (isLogFileExists()) {
            return readFromFile(com.loopme.utils.FileUtils.getCachedLogFile().toString());
        }
        return "";
    }

    public static boolean isLogFileValid() {
        return isLogFileExists() && !TextUtils.isEmpty(getLogContent());
    }

    private static boolean isLogFileExists() {
        return com.loopme.utils.FileUtils.getCachedLogFile().exists();
    }
}
