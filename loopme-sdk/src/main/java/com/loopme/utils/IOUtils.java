package com.loopme.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by katerina on 7/17/17.
 */

public class IOUtils {

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(OutputStream output) {
        flushQuietly(output);
        closeQuietly((Closeable) output);
    }

    private static void flushQuietly(OutputStream output) {
        try {
            output.flush();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static String toString(InputStream inputStream) throws IOException {
        int numberBytesRead;
        StringBuilder out = new StringBuilder();
        byte[] bytes = new byte[4096];

        try {
            while ((numberBytesRead = inputStream.read(bytes)) != -1) {
                out.append(new String(bytes, 0, numberBytesRead));
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return out.toString();
    }

    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int numberBytesRead;

        try {
            while ((numberBytesRead = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, numberBytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return os.toByteArray();
    }
}
