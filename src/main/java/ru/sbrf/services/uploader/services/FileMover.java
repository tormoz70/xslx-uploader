package ru.sbrf.services.uploader.services;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileMover {
    public static void move(String fromFolder, String toFolder, String fileName) {
        try {
            var processedFileSrc = new File(fromFolder + fileName);
            FileUtils.copyFile(processedFileSrc,
                    new File(toFolder + fileName), REPLACE_EXISTING);
            FileUtils.delete(processedFileSrc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
